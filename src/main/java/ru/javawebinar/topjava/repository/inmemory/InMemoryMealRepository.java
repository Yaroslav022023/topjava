package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.AutoCloseableLock;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, Lock> locks = new ConcurrentHashMap<>();

    {
        MealsUtil.meals.forEach(meal -> save(SecurityUtil.authUserId(), meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        try (AutoCloseableLock ignored = new AutoCloseableLock(getLock(userId))) {
            Map<Integer, Meal> userMeals =
                    repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                userMeals.put(meal.getId(), meal);
                return meal;
            } else {
                if (userMeals.containsKey(meal.getId())) {
                    userMeals.replace(meal.getId(), meal);
                    return meal;
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public boolean delete(int userId, int mealId) {
        try (AutoCloseableLock ignored = new AutoCloseableLock(getLock(userId))) {
            if (isMealBelongsToUser(userId, mealId)) {
                return repository.get(userId).remove(mealId) != null;
            }
            return false;
        }
    }

    private Lock getLock(int userId) {
        locks.computeIfAbsent(userId, k -> new ReentrantLock());
        return locks.get(userId);
    }

    @Override
    public Meal get(int userId, int mealId) {
        if (isMealBelongsToUser(userId, mealId)) {
            return repository.get(userId).get(mealId);
        }
        return null;
    }

    private boolean isMealBelongsToUser(int userId, int mealId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null && userMeals.containsKey(mealId);
    }

    @Override
    public Collection<Meal> getAll() {
        return repository.values().stream()
                .parallel()
                .flatMap(map -> map.values().stream())
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }
}