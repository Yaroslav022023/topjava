package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MealsUtil {
    public static final List<Meal> meals = Arrays.asList(
            new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(7, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime,
                                                 LocalTime endTime, int caloriesPerDay) {
        return filterMeals(meals, startTime, endTime, caloriesPerDay);
    }

    public static List<MealTo> getMealsWithExcess(List<Meal> meals, int caloriesPerDay) {
        return filterMeals(meals, null, null, caloriesPerDay);
    }

    private static List<MealTo> filterMeals(List<Meal> meals, LocalTime startTime,
                                            LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));
        Stream<Meal> stream = meals.stream();
        if (startTime != null && endTime != null) {
            stream = stream.filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime));
        }
        return stream
                .sorted(Comparator.comparing(Meal::getDateTime))
                .map(meal -> new MealTo(meal.getId(), TimeUtil.getDateTimeFormatted(meal.getDateTime()),
                        meal.getDescription(), meal.getCalories(),
                        caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}