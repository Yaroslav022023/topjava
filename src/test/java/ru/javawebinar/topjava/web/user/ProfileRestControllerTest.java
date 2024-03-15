package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UsersUtil;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPasswordTo(newTo, newTo.getPassword())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type", is(ErrorType.DATA_ERROR.toString())))
                .andExpect(jsonPath("$.typeMessage", is("Data error")))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details", hasItem("User with this email is already in the application")));
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
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_ERROR.toString())))
                .andExpect(jsonPath("$.typeMessage", is("Error in entered data")))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details", hasItem("[name] size must be between 2 and 128")));
    }

    @Test
    void updateEmptyName() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setName("");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_ERROR.toString())))
                .andExpect(jsonPath("$.typeMessage", is("Error in entered data")))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(2)))
                .andExpect(jsonPath("$.details", hasItem("[name] must not be blank")))
                .andExpect(jsonPath("$.details", hasItem("[name] size must be between 2 and 128")));
    }

    @Test
    void updateInvalidEmail() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setEmail("abc");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_ERROR.toString())))
                .andExpect(jsonPath("$.typeMessage", is("Error in entered data")))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details", hasItem("[email] must be a well-formed email address")));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateDuplicateEmail() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setEmail(admin.getEmail());
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type", is(ErrorType.DATA_ERROR.toString())))
                .andExpect(jsonPath("$.typeMessage", is("Data error")))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details", hasItem("User with this email is already in the application")));
    }

    @Test
    void updateInvalidPassword() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setPassword(" ");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(jsonWithPasswordTo(updatedTo, updatedTo.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_ERROR.toString())))
                .andExpect(jsonPath("$.typeMessage", is("Error in entered data")))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(2)))
                .andExpect(jsonPath("$.details", hasItem("[password] must not be blank")))
                .andExpect(jsonPath("$.details", hasItem("[password] size must be between 5 and 128")));
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