package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int GUEST_ID = START_SEQ + 2;
    public static final Meal Meal1_ID = new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "breakfast", 500);
    public static final Meal Meal2_ID = new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "lunch", 1000);
    public static final Meal Meal3_ID = new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "dinner", 500);
    public static final Meal Meal4_ID = new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Food on the boundary value", 100);
    public static final Meal Meal5_ID = new Meal(START_SEQ + 7, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "breakfast", 1000);
    public static final Meal Meal6_ID = new Meal(START_SEQ + 8, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "lunch", 500);
    public static final Meal Meal7_ID = new Meal(START_SEQ + 9, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "dinner", 410);
    public static final Meal Meal8_ID = new Meal(START_SEQ + 10, LocalDateTime.of(2021, Month.MARCH, 3, 10, 0), "breakfast", 600);
    public static final Meal Meal9_ID = new Meal(START_SEQ + 11, LocalDateTime.of(2021, Month.MARCH, 3, 13, 0), "lunch", 1000);
    public static final Meal Meal10_ID = new Meal(START_SEQ + 12, LocalDateTime.of(2021, Month.MARCH, 3, 20, 0), "dinner", 500);
    public static final Meal Meal11_ID = new Meal(START_SEQ + 13, LocalDateTime.of(2021, Month.MARCH, 4, 10, 0), "breakfast", 300);
    public static final Meal Meal12_ID = new Meal(START_SEQ + 14, LocalDateTime.of(2021, Month.MARCH, 4, 13, 0), "lunch", 1000);
    public static final Meal Meal13_ID = new Meal(START_SEQ + 15, LocalDateTime.of(2021, Month.MARCH, 4, 20, 0), "dinner", 500);
    public static final List<Meal> userMeals = Arrays.asList(Meal1_ID, Meal2_ID, Meal3_ID,
            Meal4_ID, Meal5_ID, Meal6_ID, Meal7_ID);
    public static final List<Meal> adminMeals = Arrays.asList(Meal8_ID, Meal9_ID, Meal10_ID,
            Meal11_ID, Meal12_ID, Meal13_ID);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2023, Month.APRIL, 5, 12, 0), "lunch", 1400);
    }

    public static Meal getUpdated(Meal meal, LocalDateTime dateTime) {
        Meal expected = new Meal(meal);
        expected.setCalories(850);
        expected.setDescription("updated description");
        expected.setDateTime(dateTime);
        return expected;
    }

    public static List<Meal> getCreated(MealService service, int userId) {
        Meal actual = service.create(getNew(), userId);
        Integer newId = actual.getId();
        Meal expected = getNew();
        expected.setId(newId);
        return Arrays.asList(actual, expected);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}