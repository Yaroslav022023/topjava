package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int GUEST_ID = START_SEQ + 2;
    public static final List<Meal> meals = Arrays.asList(
            new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "breakfast", 500),
            new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "lunch", 1000),
            new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "dinner", 500),

            new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Food on the boundary value", 100),
            new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "breakfast", 1000),
            new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "lunch", 500),
            new Meal(7, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "dinner", 410),

            new Meal(8, LocalDateTime.of(2021, Month.MARCH, 3, 10, 0), "breakfast", 600),
            new Meal(9, LocalDateTime.of(2021, Month.MARCH, 3, 13, 0), "lunch", 1000),
            new Meal(10, LocalDateTime.of(2021, Month.MARCH, 3, 20, 0), "dinner", 500),

            new Meal(11, LocalDateTime.of(2021, Month.MARCH, 4, 10, 0), "breakfast", 300),
            new Meal(12, LocalDateTime.of(2021, Month.MARCH, 4, 13, 0), "lunch", 1000),
            new Meal(13, LocalDateTime.of(2021, Month.MARCH, 4, 20, 0), "dinner", 500)
    );
    public static final Map<Integer, Meal> mealsMap;

    static {
        mealsMap = meals.stream()
                .collect(Collectors.toMap(Meal::getId, meal -> meal));
    }

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2023, Month.APRIL, 5, 12, 0), "lunch", 1400);
    }

    public static Meal getUpdated(int id, LocalDateTime dateTime) {
        Meal expected = new Meal(mealsMap.get(id));
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
        assertThat(actual).isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).isEqualTo(expected);
    }
}