package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MealRepository {
    Meal save(int userId, Meal meal);

    boolean delete(int userId, int mealId);

    // null if meal does not belong to userId
    Meal get(int userId, int mealId);

    // ORDERED dateTime desc
    List<Meal> getAll(int userId);

    List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate,
                              LocalTime startTime, LocalTime endTime);
}