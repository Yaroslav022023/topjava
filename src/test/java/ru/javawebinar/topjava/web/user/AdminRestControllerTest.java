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
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestJsonUtil.readExceptionsFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;

class AdminRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestController.REST_URL + '/';
    @Autowired
    private UserService userService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID)
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin));
    }

    @Test
    void getNotFound() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("Data not found"));
    }

    @Test
    void getByEmail() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by-email?email=" + user.getEmail())
                .with(userHttpBasic(admin)))
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
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> userService.get(USER_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("Data not found"));
    }

    @Test
    void update() throws Exception {
        User updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(USER_ID), updated);
    }

    @Test
    void updateInvalidName() throws Exception {
        User updated = getUpdated();
        updated.setName(" ");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(2, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("[name] must not be blank"));
        assertTrue(responseBodyErrors.contains("The size of [name] must be between 2 and 100"));
    }

    @Test
    void updateInvalidEmail() throws Exception {
        User updated = getUpdated();
        updated.setEmail("test");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("[email] entered incorrectly"));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateDuplicateEmail() throws Exception {
        User duplicateEmail = new User(user);
        duplicateEmail.setEmail(admin.getEmail());
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(duplicateEmail, duplicateEmail.getPassword())))
                .andExpect(status().isConflict())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("User with this email is already in the application"));
    }

    @Test
    void updateInvalidPassword() throws Exception {
        User updated = getUpdated();
        updated.setPassword("1234");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("The size of [password] must be between 5 and 128"));
    }

    @Test
    void createWithLocation() throws Exception {
        User newUser = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isCreated());

        User created = USER_MATCHER.readFromJson(action);
        int newId = created.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userService.get(newId), newUser);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createWithLocationDuplicateEmail() throws Exception {
        User newUser = getNew();
        newUser.setEmail(admin.getEmail());
        MvcResult result = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isConflict())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("User with this email is already in the application"));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin, guest, user));
    }

    @Test
    void getWithMeals() throws Exception {
        assumeDataJpa();
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID + "/with-meals")
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_WITH_MEALS_MATCHER.contentJson(admin));
    }

    @Test
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(userService.get(USER_ID).isEnabled());
    }
}