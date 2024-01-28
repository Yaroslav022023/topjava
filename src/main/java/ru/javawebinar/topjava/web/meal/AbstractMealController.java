package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class AbstractMealController {
    protected static final Logger log = LoggerFactory.getLogger(AbstractMealController.class);
    @Autowired
    protected MealService service;
    protected int userId = SecurityUtil.authUserId();

    protected List<MealTo> getAll() {
        return MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
    }

    protected Meal get(int id) {
        return service.get(id, userId);
    }

    protected Meal create(Meal meal) {
        checkNew(meal);
        return service.create(meal, userId);
    }

    protected void update(Meal meal, int id) {
        assureIdConsistent(meal, id);
        service.update(meal, userId);
    }

    protected void delete(int id) {
        service.delete(id, userId);
    }

    protected List<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return MealsUtil.getFilteredTos(service.getBetweenInclusive(startDate, endDate, userId),
                SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }
}
