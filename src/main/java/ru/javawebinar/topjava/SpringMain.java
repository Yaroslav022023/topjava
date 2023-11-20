package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            MealRestController mealRestController = appCtx.getBean(MealRestController.class);

            System.out.println("-------------------create(): user");
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));

            System.out.println("-------------------getByMail(): user");
            User user = adminUserController.getByMail("email@mail.ru");
            System.out.println(user);

            System.out.println("-------------------getAll(): meals");
            for (MealTo mealTo : mealRestController.getAll()) {
                System.out.println(mealTo);
            }

            System.out.println("-------------------get(): meal");
            System.out.println(mealRestController.get(1));

            System.out.println("-------------------save(): meal");
            System.out.println(mealRestController.create(new Meal(LocalDateTime.of(
                    2023, Month.FEBRUARY, 3, 12, 0), "Test dish", 1010)));

            System.out.println("-------------------delete(): meal");
            mealRestController.delete(1);

            System.out.println("-------------------get(): meal");
            try {
                System.out.println(mealRestController.get(1));
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("-------------------getAll(): meals");
            for (MealTo mealTo : mealRestController.getAll()) {
                System.out.println(mealTo);
            }
        }
    }
}