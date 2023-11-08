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
        log.info(String.format("save() id [%s]: Execution action...", meal.getId()));
        storage.put(meal.getId(), meal);
        log.info(String.format("save() id [%s]: Finish!", meal.getId()));
    }

    @Override
    public Meal get(Integer id) {
        log.info(String.format("get() id [%s]: Execution action...", id));
        return storage.get(id);
    }

    @Override
    public void delete(Integer id) {
        log.info(String.format("delete() id [%s]: Execution action...", id));
        storage.remove(id);
        log.info(String.format("delete() id [%s]: Finish!", id));
    }

    @Override
    public int getSize() {
        log.info(String.format("getSize(): size = [%s]", storage.size()));
        return storage.size();
    }
}