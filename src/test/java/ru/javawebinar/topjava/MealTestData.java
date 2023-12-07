package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final Meal meal_1_user = new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "breakfast - user", 500);
    public static final Meal meal_2_user = new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "lunch - user", 1000);
    public static final Meal meal_3_user = new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "dinner - user", 500);
    public static final Meal meal_4_user = new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Food on the boundary value - user", 100);
    public static final Meal meal_5_user = new Meal(START_SEQ + 7, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "breakfast - user", 1000);
    public static final Meal meal_6_user = new Meal(START_SEQ + 8, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "lunch - user", 500);
    public static final Meal meal_7_user = new Meal(START_SEQ + 9, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "dinner - user", 410);
    public static final Meal meal_8_admin = new Meal(START_SEQ + 10, LocalDateTime.of(2021, Month.MARCH, 3, 10, 0), "breakfast - admin", 600);
    public static final Meal meal_9_admin = new Meal(START_SEQ + 11, LocalDateTime.of(2021, Month.MARCH, 3, 13, 0), "lunch - admin", 1000);
    public static final Meal meal_10_admin = new Meal(START_SEQ + 12, LocalDateTime.of(2021, Month.MARCH, 3, 20, 0), "dinner - admin", 500);
    public static final Meal meal_11_admin = new Meal(START_SEQ + 13, LocalDateTime.of(2021, Month.MARCH, 4, 10, 0), "breakfast - admin", 300);
    public static final Meal meal_12_admin = new Meal(START_SEQ + 14, LocalDateTime.of(2021, Month.MARCH, 4, 13, 0), "lunch - admin", 1000);
    public static final Meal meal_13_admin = new Meal(START_SEQ + 15, LocalDateTime.of(2021, Month.MARCH, 4, 20, 0), "dinner - admin", 500);
    public static final List<Meal> userMeals = Arrays.asList(meal_1_user, meal_2_user, meal_3_user,
            meal_4_user, meal_5_user, meal_6_user, meal_7_user);
    public static final List<Meal> adminMeals = Arrays.asList(meal_8_admin, meal_9_admin, meal_10_admin,
            meal_11_admin, meal_12_admin, meal_13_admin);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2023, Month.APRIL, 5, 12, 0), "lunch", 1400);
    }

    public static Meal getUpdated(Meal meal, LocalDateTime dateTime) {
        Meal updated = new Meal(meal);
        updated.setCalories(850);
        updated.setDescription("updated description");
        updated.setDateTime(dateTime);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}