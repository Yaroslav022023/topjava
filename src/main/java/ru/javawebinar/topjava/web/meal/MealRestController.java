package ru.javawebinar.topjava.web.meal;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MealRestController extends AbstractMealController {

    public List<MealTo> doGetAll() {
        log.info("getAll for user {}", userId);
        return getAll();
    }

    public Meal doGet(int id) {
        log.info("get meal {} for user {}", id, userId);
        return get(id);
    }

    public Meal doCreate(Meal meal) {
        log.info("create {} for user {}", meal, userId);
        return create(meal);
    }

    public void doUpdate(Meal meal, int id) {
        update(meal, id);
        log.info("update {} for user {}", meal, userId);
    }

    public void doDelete(int id) {
        delete(id);
        log.info("delete meal {} for user {}", id, userId);
    }

    /**
     * <ol>Filter separately
     * <li>by date</li>
     * <li>by time for every date</li>
     * </ol>
     */
    public List<MealTo> doGetFiltered(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                      @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, userId);
        return getFiltered(startDate, endDate, startTime, endTime);
    }
}