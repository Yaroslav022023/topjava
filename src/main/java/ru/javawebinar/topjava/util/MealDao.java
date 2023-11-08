package ru.javawebinar.topjava.util;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

public class MealDao implements Storage {
    private static final Logger log = getLogger(MealDao.class);
    ConcurrentHashMap<Integer, Meal> storage = new ConcurrentHashMap<>();

    public MealDao() {
        log.info("MealDao: Initializing...");
        MealsInitialization.meals.forEach(meal -> storage.put(meal.getId(), meal));
        log.info("MealDao: storage created");
    }

    @Override
    public List<Meal> getAll() {
        log.info("getAll(): Execution action...");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void save(Meal meal) {
        log.info("save() Meal: Execution action...");
        storage.put(meal.getId(), meal);
        log.info("save() Meal: Finish!");
    }

    @Override
    public Meal get(Integer id) {
        log.info("get(): Execution action...");
        return storage.get(id);
    }

    @Override
    public void update(Meal meal) {
        log.info("update(): Execution action...");
        storage.put(meal.getId(), meal);
        log.info("update(): Finish!");
    }

    @Override
    public void delete(Integer id) {
        log.info("delete(): Execution action...");
        storage.remove(id);
        log.info("delete(): Finish!");
    }
}