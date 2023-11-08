package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface Storage {
    List<Meal> getAll();

    void save(Meal meal);

    Meal get(Integer id);

    void delete(Integer id);

    int getSize();
}