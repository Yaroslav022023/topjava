package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.*;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal save(int userId, Meal meal) {
        return checkNotFoundWithId(repository.save(userId, meal), meal.getId());
    }

    public void delete(int userId, int mealId) {
        checkNotFoundWithId(repository.delete(userId, mealId), mealId);
    }

    public Meal get(int userId, int mealId) {
        return checkNotFoundWithId(repository.get(userId, mealId), mealId);
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(repository.getAll(), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(String startDate, String endDate, String startTime, String endTime) {
        return MealsUtil.getFilteredTos(getAllFilteredByDate(startDate, endDate),
                SecurityUtil.authUserCaloriesPerDay(), toLocalTimeOrMin(startTime), toLocalTimeOrMax(endTime));
    }

    private List<Meal> getAllFilteredByDate(String startDate, String endDate) {
        return MealsUtil.getFilteredByDate(repository.getAll(), toLocalDateOrMin(startDate), toLocalDateOrMax(endDate));
    }
}