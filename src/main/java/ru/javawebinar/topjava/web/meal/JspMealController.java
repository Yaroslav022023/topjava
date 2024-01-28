package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    @GetMapping()
    public String doGetAll(Model model) {
        model.addAttribute("meals", getAll());
        log.info("getAll for user {}", userId);
        return "meals";
    }

    @GetMapping("/create")
    public String doCreate(Model model) {
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        log.info("creating for user {}...", userId);
        return "mealForm";
    }

    @GetMapping("/update/{id}")
    public String doUpdate(Model model, @PathVariable("id") int id) {
        model.addAttribute("meal", get(id));
        log.info("updating {} for user {}...", id, userId);
        return "mealForm";
    }

    @PostMapping()
    public String save(HttpServletRequest request) throws UnsupportedEncodingException {
        final Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            update(meal, Integer.parseInt(request.getParameter("id")));
            log.info("updated {} for user {}", meal, userId);
        } else {
            create(meal);
            log.info("created {} for user {}", meal, userId);
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete/{id}")
    public String doDelete(@PathVariable("id") int id) {
        delete(id);
        log.info("deleted meal {} for user {}", id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String doGetFiltered(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Model model) {

        LocalDate startLocalDate = parseLocalDate(startDate);
        LocalDate endLocalDate = parseLocalDate(endDate);
        LocalTime startLocalTime = parseLocalTime(startTime);
        LocalTime endLocalTime = parseLocalTime(endTime);

        model.addAttribute("meals", getFiltered(startLocalDate, endLocalDate, startLocalTime, endLocalTime));
        log.info("getBetween dates({} - {}) time({} - {}) for user {}",
                startLocalDate, endLocalDate, startLocalTime, endLocalTime, userId);
        return "meals";
    }
}
