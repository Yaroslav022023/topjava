package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository {
    Meal save(int userId, Meal meal);

    boolean delete(int userId, int mealId);

    Meal get(int userId, int mealId);

    List<Meal> getAll(int userId);

    List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate);
}