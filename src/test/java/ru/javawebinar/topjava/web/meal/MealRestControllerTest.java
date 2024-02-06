package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

public class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MealService service;

    @Test
    void getAll_User() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(MealsUtil.getTos(meals, authUserCaloriesPerDay())));
    }

    @Test
    void getAll_Admin() throws Exception {
        SecurityUtil.setAuthUserId(UserTestData.ADMIN_ID);
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(MealsUtil.getTos(adminMeals, authUserCaloriesPerDay())));
        SecurityUtil.setAuthUserId(UserTestData.USER_ID);
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated());

        Meal created = MEAL_MATCHER.readFromJson(action);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(service.get(newId, authUserId()), newMeal);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + meal1.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(meal1.getId(), authUserId()));
    }

    @Test
    void update() throws Exception {
        Meal updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + meal1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(service.get(meal1.getId(), authUserId()), updated);
    }

    @Test
    void getBetweenDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30")
                .param("endDate", "2020-01-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(
                        MealsUtil.getTos(List.of(meal3, meal2, meal1), authUserCaloriesPerDay())));
    }

    @Test
    void getBetween_Time() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                meals, authUserCaloriesPerDay(), meal1.getTime(), meal2.getTime());

        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startTime", "10:00")
                .param("endTime", "13:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }

    @Test
    void getBetween_DateAndTime() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                List.of(meal7, meal6, meal5, meal4), authUserCaloriesPerDay(), null, meal7.getTime());

        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-31")
                .param("endDate", "2020-01-31")
                .param("endTime", "20:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }

    @Test
    void getBetween_DateAndTime2() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                meals, authUserCaloriesPerDay(), meal2.getTime(), null);

        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30")
                .param("startTime", "13:00")
                .param("endDate", "2020-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }

    @Test
    void getBetween_DateAndTime3() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                meals, authUserCaloriesPerDay(), meal2.getTime(), meal3.getTime());

        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30")
                .param("startTime", "13:00")
                .param("endDate", "2020-01-31")
                .param("endTime", "20:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }
}
