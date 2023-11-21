package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MealsUtil {
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static final List<Meal> meals1 = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static final List<Meal> meals2 = Arrays.asList(
            new Meal(LocalDateTime.of(2005, Month.MARCH, 5, 10, 10), "Завтрак", 500),
            new Meal(LocalDateTime.of(2005, Month.MARCH, 5, 13, 10), "Обед", 1000),
            new Meal(LocalDateTime.of(2005, Month.MARCH, 5, 20, 10), "Ужин", 1000),
            new Meal(LocalDateTime.of(2007, Month.APRIL, 7, 0, 0), "Ужин", 100),
            new Meal(LocalDateTime.of(2007, Month.APRIL, 7, 10, 20), "Завтрак", 1000)
    );

    public static List<Meal> getFilteredByDate(Collection<Meal> meals, LocalDate startDate, LocalDate endDate) {
        return meals.stream()
                .filter(meal -> DateTimeUtil.isBetweenInclusiveByDate(meal.getDate(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public static List<Meal> getFilteredByTime(Collection<Meal> meals, LocalTime startTime, LocalTime endTime) {
        return meals.stream()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpenByTime(meal.getTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public static List<MealTo> getTos(Collection<Meal> meals, int caloriesPerDay) {
        return filterByPredicate(meals, caloriesPerDay, meal -> true);
    }

    public static List<MealTo> getFilteredTos(Collection<Meal> filteredMeals, List<MealTo> tos) {
        return filteredMeals.stream()
                .filter(meal -> tos.stream().anyMatch(to -> Objects.equals(to.getId(), meal.getId())))
                .map(meal -> {
                    boolean excess = tos.stream()
                            .filter(to -> Objects.equals(to.getId(), meal.getId()))
                            .findFirst()
                            .map(MealTo::isExcess)
                            .get();
                    return createTo(meal, excess);
                })
                .collect(Collectors.toList());
    }

    private static List<MealTo> filterByPredicate(Collection<Meal> meals, int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
                );
        return meals.stream()
                .filter(filter)
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}