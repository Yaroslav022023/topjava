package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MealDao;
import ru.javawebinar.topjava.util.MealsInitialization;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static Storage storage;
    private static AtomicInteger id;

    @Override
    public void init() {
        storage = new MealDao();
        id = new AtomicInteger(storage.getSize());
    }

    @Override
    public void destroy() {
        storage = null;
        id = null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Inside MealServlet.doGet()...");
        String action = request.getParameter("action");
        if (action == null || action.isEmpty() || action.equals("meals")) {
            request.setAttribute("mealsToList", MealsUtil.getMealsWithExcess(storage.getAll(),
                    MealsInitialization.CALORIES_PER_DAY));
            request.getRequestDispatcher("/WEB-INF/jsp/meals.jsp").forward(request, response);
            return;
        }
        switch (action) {
            case "create": {
                Meal meal = new Meal(id.incrementAndGet());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/WEB-INF/jsp/edit.jsp").forward(request, response);
                break;
            }
            case "update": {
                Integer id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("meal", storage.get(id));
                request.getRequestDispatcher("/WEB-INF/jsp/edit.jsp").forward(request, response);
                break;
            }
            case "delete": {
                Integer id = Integer.parseInt(request.getParameter("id"));
                storage.delete(id);
                response.sendRedirect("meals?action=meals");
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        log.debug("Inside MealServlet.doPost()...");
        storage.save(new Meal(
                Integer.parseInt(request.getParameter("id")),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        ));
        response.sendRedirect("meals?action=meals");
    }
}