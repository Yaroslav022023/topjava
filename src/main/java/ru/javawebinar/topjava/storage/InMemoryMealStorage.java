package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryMealStorage implements MealStorage {
    private static final Logger log = getLogger(InMemoryMealStorage.class);
    private final ConcurrentHashMap<Integer, Meal> storage = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger();

    public InMemoryMealStorage() {
        log.debug("MealDao: Initializing...");
        MealsUtil.meals.forEach(this::create);
        log.debug("MealDao: storage created");
    }

    @Override
    public List<Meal> getAll() {
        log.debug("getAll(): Execution action...");
        return new ArrayList<>(storage.values());
    }

    @Override
    public Meal create(Meal meal) {
        log.debug("create() new Meal: Execution action...");
        meal.setId(id.incrementAndGet());
        storage.put(meal.getId(), meal);
        log.debug("create() id {}: Finish!", meal.getId());
        return meal;
    }

    @Override
    public Meal get(int id) {
        log.debug("get(): id {}.", id);
        return storage.get(id);
    }

    @Override
    public void update(Meal meal) {
        log.debug("update() id {}: Execution action...", meal.getId());
        storage.replace(meal.getId(), meal);
        log.debug("update() id {}: Finish!", meal.getId());
    }

    @Override
    public void delete(int id) {
        log.debug("delete() id {}: Execution action...", id);
        storage.remove(id);
        log.debug("delete() id {}: Finish!", id);
    }
}