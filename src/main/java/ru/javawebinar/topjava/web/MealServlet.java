package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.InMemoryMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private MealStorage mealStorage;
    private static final int CALORIES_PER_DAY = 2000;

    @Override
    public void init() {
        mealStorage = new InMemoryMealStorage();
    }

    @Override
    public void destroy() {
        mealStorage = null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Inside MealServlet.doGet()...");
        String action = request.getParameter("action");
        if (action == null) {
            displayMeals(null, request, response);
            return;
        }
        switch (action) {
            case "create": {
                log.debug("action = {}. Display editMeal.jsp...", action);
                Meal meal = new Meal();
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/WEB-INF/jsp/editMeal.jsp").forward(request, response);
                break;
            }
            case "update": {
                log.debug("action = {}. Display editMeal.jsp...", action);
                int id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("meal", mealStorage.get(id));
                request.getRequestDispatcher("/WEB-INF/jsp/editMeal.jsp").forward(request, response);
                break;
            }
            case "delete": {
                int id = Integer.parseInt(request.getParameter("id"));
                log.debug("action = {}. Deleted id {}. Display meals.jsp...", action, id);
                mealStorage.delete(id);
                response.sendRedirect("meals");
                break;
            }
            default: {
                displayMeals(action, request, response);
                break;
            }
        }
        log.debug("Completed doGet().");
    }

    private void displayMeals(String action, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("action = {}. Display meals.jsp...", action);
        request.setAttribute("mealsToList", MealsUtil.getMealsWithExcess(mealStorage.getAll(), CALORIES_PER_DAY));
        request.getRequestDispatcher("/WEB-INF/jsp/meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        log.debug("Inside MealServlet.doPost()...");
        Integer id = Objects.equals(request.getParameter("id"), "") ? null :
                Integer.parseInt(request.getParameter("id"));
        final Meal meal = new Meal(
                id,
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );
        if (id == null) {
            mealStorage.create(meal);
            log.debug("Completed doPost(): saved id {}.", meal.getId());
        } else {
            mealStorage.update(meal);
            log.debug("Completed doPost(): updated id {}.", id);
        }
        response.sendRedirect("meals");
    }
}