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
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
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
        Meal actual = service.get(1, USER_ID);
        Meal expected = mealsMap.get(1);
        assertMatch(actual, expected);
    }

    @Test
    public void getForAdmin() {
        Meal actual = service.get(8, ADMIN_ID);
        Meal expected = mealsMap.get(8);
        assertMatch(actual, expected);
    }

    @Test
    public void getForUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(8, USER_ID));
    }

    @Test
    public void getForAdminNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(1, ADMIN_ID));
    }

    @Test
    public void deleteForUser() {
        service.delete(1, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(1, USER_ID));
    }

    @Test
    public void deleteForAdmin() {
        service.delete(8, ADMIN_ID);
        assertThrows(NotFoundException.class, () -> service.get(8, ADMIN_ID));
    }

    @Test
    public void deleteForUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(8, USER_ID));
    }

    @Test
    public void deleteForAdminNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(1, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive_30_01_2020_ForUser() {
        List<Meal> expected = Arrays.asList(
                mealsMap.get(1),
                mealsMap.get(2),
                mealsMap.get(3)
        );
        expected.sort(Comparator.comparing(Meal::getDateTime).reversed());
        List<Meal> actual = service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30),
                USER_ID);
        assertMatch(actual, expected);
    }

    @Test
    public void getBetweenInclusive_03_03_2021_AND_04_03_2021_ForAdmin() {
        List<Meal> expected = Arrays.asList(
                mealsMap.get(8),
                mealsMap.get(9),
                mealsMap.get(10),
                mealsMap.get(11),
                mealsMap.get(12),
                mealsMap.get(13)
        );
        expected.sort(Comparator.comparing(Meal::getDateTime).reversed());
        List<Meal> actual = service.getBetweenInclusive(
                LocalDate.of(2021, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 4),
                ADMIN_ID);
        assertMatch(actual, expected);
    }

    @Test
    public void getBetweenInclusiveEmptyForUser() {
        List<Meal> actual = service.getBetweenInclusive(
                LocalDate.of(2021, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 4),
                USER_ID);
        assertMatch(actual, new ArrayList<Meal>());
    }

    @Test
    public void getBetweenInclusiveEmptyForAdmin() {
        List<Meal> actual = service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 31),
                ADMIN_ID);
        assertMatch(actual, new ArrayList<Meal>());
    }

    @Test
    public void getAllForUser() {
        List<Meal> expected = Arrays.asList(
                mealsMap.get(1),
                mealsMap.get(2),
                mealsMap.get(3),
                mealsMap.get(4),
                mealsMap.get(5),
                mealsMap.get(6),
                mealsMap.get(7)
        );
        expected.sort(Comparator.comparing(Meal::getDateTime).reversed());
        List<Meal> actual = service.getAll(USER_ID);
        assertMatch(actual, expected);
    }

    @Test
    public void getAllForAdmin() {
        List<Meal> expected = Arrays.asList(
                mealsMap.get(8),
                mealsMap.get(9),
                mealsMap.get(10),
                mealsMap.get(11),
                mealsMap.get(12),
                mealsMap.get(13)
        );
        expected.sort(Comparator.comparing(Meal::getDateTime).reversed());
        List<Meal> actual = service.getAll(ADMIN_ID);
        assertMatch(actual, expected);
    }

    @Test
    public void updateForUser() {
        Meal expected = getUpdated(1, LocalDateTime.now());
        service.update(expected, USER_ID);
        assertMatch(service.get(1, USER_ID), expected);
    }

    @Test
    public void updateForAdmin() {
        Meal expected = getUpdated(8, LocalDateTime.now());
        service.update(expected, ADMIN_ID);
        assertMatch(service.get(8, ADMIN_ID), expected);
    }

    @Test
    public void updateForUserNotFound() {
        Meal expected = getUpdated(8, LocalDateTime.now());
        assertThrows(NotFoundException.class, () -> service.update(expected, USER_ID));
    }

    @Test
    public void updateForAdminNotFound() {
        Meal expected = getUpdated(1, LocalDateTime.now());
        assertThrows(NotFoundException.class, () -> service.update(expected, ADMIN_ID));
    }

    @Test
    public void updateForUserDuplicateDateTime() {
        Meal expected = getUpdated(1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0));
        assertThrows(DataIntegrityViolationException.class, () -> service.update(expected, USER_ID));
    }

    @Test
    public void updateForAdminDuplicateDateTime() {
        Meal expected = getUpdated(8, LocalDateTime.of(2021, Month.MARCH, 3, 20, 0));
        assertThrows(DataIntegrityViolationException.class, () -> service.update(expected, ADMIN_ID));
    }

    @Test
    public void createForUser() {
        List<Meal> mealList = getCreated(service, USER_ID);
        Meal expected = mealList.get(1);
        assertMatch(mealList.get(0), expected);
        assertMatch(service.get(expected.getId(), USER_ID), expected);
    }

    @Test
    public void createForAdmin() {
        List<Meal> mealList = getCreated(service, ADMIN_ID);
        Meal expected = mealList.get(1);
        assertMatch(mealList.get(0), expected);
        assertMatch(service.get(expected.getId(), ADMIN_ID), expected);
    }

    @Test
    public void createForGuest() {
        List<Meal> mealList = getCreated(service, GUEST_ID);
        Meal expected = mealList.get(1);
        assertMatch(mealList.get(0), expected);
        assertMatch(service.get(expected.getId(), GUEST_ID), expected);
    }

    @Test
    public void createForUserDuplicateDateTime() {
        Meal meal = new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "breakfast", 2400);
        assertThrows(DataIntegrityViolationException.class, () -> service.create(meal, USER_ID));
    }

    @Test
    public void createForAdminDuplicateDateTime() {
        Meal meal = new Meal(LocalDateTime.of(2021, Month.MARCH, 4, 20, 0), "dinner", 1300);
        assertThrows(DataIntegrityViolationException.class, () -> service.create(meal, ADMIN_ID));
    }
}