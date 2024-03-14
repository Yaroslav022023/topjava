package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UsersUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestJsonUtil.readExceptionsFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.web.user.ProfileRestController.REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isNoContent());
        USER_MATCHER.assertMatch(userService.getAll(), admin, guest);
    }

    @Test
    void register() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        User newUser = UsersUtil.createNewFromTo(newTo);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPasswordTo(newTo, newTo.getPassword())))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = USER_MATCHER.readFromJson(action);
        int newId = created.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userService.get(newId), newUser);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void registerDuplicateEmail() throws Exception {
        UserTo newTo = new UserTo(null, "newName", user.getEmail(), "newPassword", 1500);
        MvcResult result = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPasswordTo(newTo, newTo.getPassword())))
                .andExpect(status().isConflict())
                .andReturn();

        List<String> responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertThat(responseBodyErrors)
                .hasSize(1)
                .contains("User with this email is already in the application");
    }

    @Test
    void update() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(USER_ID), UsersUtil.updateFromTo(new User(user), updatedTo));
    }

    @Test
    void updateInvalidName() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setName("a");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        List<String> responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertThat(responseBodyErrors)
                .hasSize(1)
                .contains("[name] size must be between 2 and 128");
    }

    @Test
    void updateInvalidName_2() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setName("");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        List<String> responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertThat(responseBodyErrors)
                .hasSize(2)
                .contains("[name] must not be blank")
                .contains("[name] size must be between 2 and 128");
    }

    @Test
    void updateInvalidEmail() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setEmail("abc");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        List<String> responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertThat(responseBodyErrors)
                .hasSize(1)
                .contains("[email] must be a well-formed email address");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateDuplicateEmail() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setEmail(admin.getEmail());
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isConflict())
                .andReturn();

        List<String> responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertThat(responseBodyErrors)
                .hasSize(1)
                .contains("User with this email is already in the application");
    }

    @Test
    void updateInvalidPassword() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setPassword(" ");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        List<String> responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertThat(responseBodyErrors)
                .hasSize(2)
                .contains("[password] must not be blank")
                .contains("[password] size must be between 5 and 128");
    }

    @Test
    void getWithMeals() throws Exception {
        assumeDataJpa();
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-meals")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_WITH_MEALS_MATCHER.contentJson(user));
    }
}