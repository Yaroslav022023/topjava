package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface MealRepository {
    Meal save(int userId, Meal meal);

    boolean delete(int userId, int mealId);

    Meal get(int userId, int mealId);

    List<Meal> getAll(int userId);

    Map<Meal, Integer> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate,
                                      LocalTime startTime, LocalTime endTime);
}