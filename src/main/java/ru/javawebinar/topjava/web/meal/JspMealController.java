package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
@RequestMapping("/meals")
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    private final MealService service;

    public JspMealController(MealService service) {
        this.service = service;
    }

    @GetMapping()
    public String getAll(Model model) {
        final int userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/create")
    public String create(Model model) {
        final int userId = SecurityUtil.authUserId();
        log.info("create for user {}", userId);
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable("id") int id) {
        final int userId = SecurityUtil.authUserId();
        log.info("update {} for user {}", id, userId);
        model.addAttribute("meal", service.get(id, userId));
        return "mealForm";
    }

    @PostMapping()
    public String save(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        final Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        final int userId = SecurityUtil.authUserId();

        if (StringUtils.hasLength(request.getParameter("id"))) {
            assureIdConsistent(meal, Integer.parseInt(request.getParameter("id")));
            log.info("update {} for user {}", meal, userId);
            service.update(meal, userId);
        } else {
            checkNew(meal);
            log.info("create {} for user {}", meal, userId);
            service.create(meal, userId);
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        final int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Model model) {

        LocalDate startLocalDate = parseLocalDate(startDate);
        LocalDate endLocalDate = parseLocalDate(endDate);
        LocalTime startLocalTime = parseLocalTime(startTime);
        LocalTime endLocalTime = parseLocalTime(endTime);

        final int userId = SecurityUtil.authUserId();
        log.info("getBetween dates({} - {}) time({} - {}) for user {}",
                startLocalDate, endLocalDate, startLocalTime, endLocalTime, userId);

        model.addAttribute("meals", MealsUtil.getFilteredTos(
                service.getBetweenInclusive(startLocalDate, endLocalDate, userId),
                SecurityUtil.authUserCaloriesPerDay(), startLocalTime, endLocalTime));
        return "meals";
    }
}
