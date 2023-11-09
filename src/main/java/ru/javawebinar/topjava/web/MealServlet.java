package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MapMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.IdGenerator;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private MealStorage storageMeal;

    @Override
    public void init() {
        storageMeal = new MapMealStorage();
    }

    @Override
    public void destroy() {
        storageMeal = null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Inside MealServlet.doGet()...");
        String action = request.getParameter("action");
        if (action == null || action.isEmpty() || action.equals("meals")) {
            displayMeals(action, request, response);
            return;
        }
        switch (action) {
            case "save": {
                log.debug("action = {}. Display editMeal.jsp...", action);
                Meal meal = new Meal();
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/WEB-INF/jsp/editMeal.jsp").forward(request, response);
                break;
            }
            case "update": {
                log.debug("action = {}. Display editMeal.jsp...", action);
                int id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("meal", storageMeal.get(id));
                request.getRequestDispatcher("/WEB-INF/jsp/editMeal.jsp").forward(request, response);
                break;
            }
            case "delete": {
                int id = Integer.parseInt(request.getParameter("id"));
                log.debug("action = {}. Deleted id {}. Display meals.jsp...", action, id);
                storageMeal.delete(id);
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
        final int caloriesPerDay = 2000;
        request.setAttribute("mealsToList", MealsUtil.getMealsWithExcess(storageMeal.getAll(), caloriesPerDay));
        request.getRequestDispatcher("/WEB-INF/jsp/meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        log.debug("Inside MealServlet.doPost()...");
        int currentId = Integer.parseInt(request.getParameter("id"));
        int idMeal = currentId != 0 ? currentId : IdGenerator.generateUniqueId();
        final Meal meal = new Meal(
                idMeal,
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );

        if (currentId != 0) {
            storageMeal.update(meal);
            log.debug("Completed doPost(): updated id {}.", idMeal);
        } else {
            storageMeal.save(meal);
            log.debug("Completed doPost(): saved id {}.", idMeal);
        }
        response.sendRedirect("meals");
    }
}