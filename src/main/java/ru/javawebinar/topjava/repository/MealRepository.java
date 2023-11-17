package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

public interface MealRepository {
    Meal save(int userId, Meal meal);

    boolean delete(int userId, int mealId);

    // null if meal does not belong to userId
    Meal get(int userId, int mealId);

    // ORDERED dateTime desc
    Collection<Meal> getAll();
}
