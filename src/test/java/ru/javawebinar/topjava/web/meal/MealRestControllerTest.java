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
        //getBetween() 30-01-2020 - 30-01-2020. Should return: meal3, meal2, meal1.
    void getBetween_Date() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=" + meal1.getDate() + "&endDate=" + meal3.getDate()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(
                        MealsUtil.getTos(List.of(meal3, meal2, meal1), authUserCaloriesPerDay())));
    }

    @Test
        //getBetween() 10:00 - 13:00. Should return: meal5, meal1.
    void getBetween_Time() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                meals, authUserCaloriesPerDay(), meal1.getTime(), meal2.getTime());
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startTime=" + meal1.getTime() + "&endTime=" + meal2.getTime()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }

    @Test
        //getBetween() 31-01-2020 - 31-01-2020 20:00. Should return: meal6, meal5, meal4.
    void getBetween_DateAndTime() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                List.of(meal7, meal6, meal5, meal4), authUserCaloriesPerDay(), null, meal7.getTime());
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=" + meal4.getDate()
                + "&endDate=" + meal4.getDate() + "&endTime=" + meal3.getTime()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }

    @Test
        //getBetween() 30-01-2020 13:00 - 31-01-2020. Should return: meal7, meal6, meal3, meal2.
    void getBetween_DateAndTime2() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                meals, authUserCaloriesPerDay(), meal2.getTime(), null);
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=" + meal1.getDate()
                + "&endDate=" + meal4.getDate() + "&startTime=" + meal2.getTime()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }

    @Test
        //getBetween() 30-01-2020 13:00 - 31-01-2020 20:00. Should return: meal6, meal2.
    void getBetween_DateAndTime3() throws Exception {
        List<MealTo> expected = MealsUtil.getFilteredTos(
                meals, authUserCaloriesPerDay(), meal2.getTime(), meal3.getTime());
        perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=" + meal1.getDate()
                + "&endDate=" + meal4.getDate() + "&startTime=" + meal2.getTime() + "&endTime=" + meal3.getTime()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_DTO_MATCHER.contentJson(expected));
    }
}
