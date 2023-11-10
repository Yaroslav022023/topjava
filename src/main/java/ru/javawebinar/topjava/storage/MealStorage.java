package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    List<Meal> getAll();

    Meal create(Meal meal);

    Meal get(int id);

    void update(Meal meal);

    void delete(int id);
}