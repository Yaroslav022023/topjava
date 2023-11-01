package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println("\n" + filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        final Map<LocalDate, Integer> totalCaloriesMap = new HashMap<>();
        meals.forEach(meal -> totalCaloriesMap.merge(meal.getDateTime().toLocalDate(),
                meal.getCalories(), Integer::sum));
        final List<UserMealWithExcess> userMealWithExcesses = new ArrayList<>();
        meals.forEach(meal -> {
            final LocalDate localDate = meal.getDateTime().toLocalDate();
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealWithExcesses.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), totalCaloriesMap.get(localDate) > caloriesPerDay));
            }
        });
        return userMealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .values().stream()
                .flatMap(userMeals -> {
                    int totalCalories = userMeals.stream().mapToInt(UserMeal::getCalories).sum();
                    return userMeals.stream()
                            .filter(userMealWithExcess -> TimeUtil.isBetweenHalfOpen(userMealWithExcess.
                                    getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                                    meal.getCalories(), totalCalories > caloriesPerDay));
                })
                .collect(Collectors.toList());
    }
}