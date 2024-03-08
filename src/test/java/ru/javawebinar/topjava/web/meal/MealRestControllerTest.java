package ru.javawebinar.topjava.web.meal;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestJsonUtil.readExceptionsFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.user;
import static ru.javawebinar.topjava.util.MealsUtil.createTo;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MealService mealService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));
    }

    @Test
    void getUnauth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getNotFound() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(user)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("Data not found"));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID)
                .with(userHttpBasic(user)))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.delete(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(user)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("Data not found"));
    }

    @Test
    void update() throws Exception {
        Meal updated = MealTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    void updateInvalidDescription() throws Exception {
        Meal updated = MealTestData.getUpdated();
        updated.setDescription(" ");
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(2, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("[description] must not be blank"));
        assertTrue(responseBodyErrors.contains("The size of [description] must be between 5 and 120"));
    }

    @Test
    void updateInvalidCaloriesMin() throws Exception {
        Meal updated = MealTestData.getUpdated();
        updated.setCalories(9);
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("The size of [calories] must be between 10 and 5000"));
    }

    @Test
    void updateInvalidCaloriesMax() throws Exception {
        Meal updated = MealTestData.getUpdated();
        updated.setCalories(5001);
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("The size of [calories] must be between 10 and 5000"));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateDuplicateDateTime() throws Exception {
        Meal updated = MealTestData.getUpdated();
        updated.setDateTime(meal2.getDateTime());
        MvcResult result = perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isConflict())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("You already have food with this date/time"));
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = MealTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated());

        Meal created = MEAL_MATCHER.readFromJson(action);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createWithLocationDuplicateDateTime() throws Exception {
        Meal newMeal = MealTestData.getNew();
        newMeal.setDateTime(meal1.getDateTime());
        MvcResult result = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isConflict())
                .andReturn();

        responseBodyErrors = readExceptionsFromJson(result.getResponse().getContentAsString(), "details");
        assertEquals(1, responseBodyErrors.size());
        assertTrue(responseBodyErrors.contains("You already have food with this date/time"));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TO_MATCHER.contentJson(getTos(meals, user.getCaloriesPerDay())));
    }

    @Test
    void getBetween() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30").param("startTime", "07:00")
                .param("endDate", "2020-01-31").param("endTime", "11:00")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(TO_MATCHER.contentJson(createTo(meal5, true), createTo(meal1, false)));
    }

    @Test
    void getBetweenAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=&endTime=")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(TO_MATCHER.contentJson(getTos(meals, user.getCaloriesPerDay())));
    }
}