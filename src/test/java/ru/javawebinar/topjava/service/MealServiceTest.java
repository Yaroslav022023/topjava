package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.getUpdated;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-jdbc.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void getForUser() {
        testGet(meal_1_user, USER_ID);
    }

    @Test
    public void getForAdmin() {
        testGet(meal_8_admin, ADMIN_ID);
    }

    private void testGet(Meal meal, int userId) {
        Meal actual = service.get(meal.getId(), userId);
        assertMatch(actual, meal);
    }

    @Test
    public void getForUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(meal_8_admin.getId(), USER_ID));
    }

    @Test
    public void getForAdminNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(meal_1_user.getId(), ADMIN_ID));
    }

    @Test
    public void deleteForUser() {
        testDelete(meal_1_user.getId(), USER_ID);
    }

    @Test
    public void deleteForAdmin() {
        testDelete(meal_8_admin.getId(), ADMIN_ID);
    }

    private void testDelete(int id, int userId) {
        service.delete(id, userId);
        assertThrows(NotFoundException.class, () -> service.get(id, userId));
    }

    @Test
    public void deleteForUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(meal_8_admin.getId(), USER_ID));
    }

    @Test
    public void deleteForAdminNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(meal_1_user.getId(), ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive_30_01_2020_ForUser() {
        testGetBetweenInclusive(
                Arrays.asList(meal_1_user, meal_2_user, meal_3_user),
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30),
                USER_ID
        );
    }

    @Test
    public void getBetweenInclusive_03_03_2021_AND_04_03_2021_ForAdmin() {
        testGetBetweenInclusive(
                adminMeals,
                LocalDate.of(2021, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 4),
                ADMIN_ID);
    }

    private void testGetBetweenInclusive(List<Meal> meals, LocalDate startDate, LocalDate endDate, int userId) {
        meals.sort(Comparator.comparing(Meal::getDateTime).reversed());
        List<Meal> actual = service.getBetweenInclusive(startDate, endDate, userId);
        assertMatch(actual, meals);
    }

    @Test
    public void getBetweenInclusiveEmptyForUser() {
        testGetBetweenInclusiveEmpty(
                LocalDate.of(2021, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 4),
                USER_ID);
    }

    @Test
    public void getBetweenInclusiveEmptyForAdmin() {
        testGetBetweenInclusiveEmpty(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 31),
                ADMIN_ID);
    }

    private void testGetBetweenInclusiveEmpty(LocalDate startDate, LocalDate endDate, int userId) {
        List<Meal> actual = service.getBetweenInclusive(startDate, endDate, userId);
        assertMatch(actual, new ArrayList<>());
    }

    @Test
    public void getAllForUser() {
        testGetAll(userMeals, USER_ID);
    }

    @Test
    public void getAllForAdmin() {
        testGetAll(adminMeals, ADMIN_ID);
    }

    private void testGetAll(List<Meal> meals, int userId) {
        meals.sort(Comparator.comparing(Meal::getDateTime).reversed());
        List<Meal> actual = service.getAll(userId);
        assertMatch(actual, meals);
    }

    @Test
    public void updateForUser() {
        testUpdate(meal_1_user, USER_ID);
    }

    @Test
    public void updateForAdmin() {
        testUpdate(meal_8_admin, ADMIN_ID);
    }

    private void testUpdate(Meal meal, int userId) {
        Meal updated = getUpdated(meal, LocalDateTime.now());
        service.update(updated, userId);
        assertMatch(service.get(meal.getId(), userId), updated);
    }

    @Test
    public void updateForUserNotFound() {
        testUpdateNotFound(meal_8_admin, USER_ID);
    }

    @Test
    public void updateForAdminNotFound() {
        testUpdateNotFound(meal_1_user, ADMIN_ID);
    }

    private void testUpdateNotFound(Meal meal, int userId) {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(meal, LocalDateTime.now()), userId));
    }

    @Test
    public void updateForUserDuplicateDateTime() {
        testUpdateDuplicateDateTime(meal_1_user, meal_2_user.getDateTime(), USER_ID);
    }

    @Test
    public void updateForAdminDuplicateDateTime() {
        testUpdateDuplicateDateTime(meal_8_admin, meal_10_admin.getDateTime(), ADMIN_ID);
    }

    private void testUpdateDuplicateDateTime(Meal meal, LocalDateTime dateTime, int userId) {
        assertThrows(DataIntegrityViolationException.class, () ->
                service.update(getUpdated(meal, dateTime), userId));
    }

    @Test
    public void createForUser() {
        testCreate(USER_ID);
    }

    @Test
    public void createForAdmin() {
        testCreate(ADMIN_ID);
    }

    @Test
    public void createForGuest() {
        testCreate(GUEST_ID);
    }

    private void testCreate(int userId) {
        Meal actual = service.create(MealTestData.getNew(), userId);
        Integer newId = actual.getId();
        Meal expected = MealTestData.getNew();
        expected.setId(newId);
        assertMatch(actual, expected);
        assertMatch(service.get(actual.getId(), userId), expected);
    }

    @Test
    public void createForUserDuplicateDateTime() {
        testCreateDuplicateDateTime(meal_1_user.getDateTime(), USER_ID);
    }

    @Test
    public void createForAdminDuplicateDateTime() {
        testCreateDuplicateDateTime(meal_13_admin.getDateTime(), ADMIN_ID);
    }

    private void testCreateDuplicateDateTime(LocalDateTime dateTime, int userId) {
        assertThrows(DataIntegrityViolationException.class, () ->
                service.create(new Meal(dateTime, "dinner", 1300), userId));
    }
}