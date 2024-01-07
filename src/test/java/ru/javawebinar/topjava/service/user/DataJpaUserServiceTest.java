package ru.javawebinar.topjava.service.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MatcherFactory;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;

import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Override
    @Autowired
    protected void setService(UserService service) {
        super.setService(service);
    }

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(USER_ID);
        final MatcherFactory.Matcher<Meal> MEAL_MATCHER = MatcherFactory.usingIgnoringFieldsComparator("user");
        MEAL_MATCHER.assertMatch(user.getMeals(), MealTestData.meals);
    }
}
