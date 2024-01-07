package ru.javawebinar.topjava.service.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.MealService;

@ActiveProfiles(Profiles.JPA)
public class JpaMealServiceTest extends AbstractMealServiceTest {

    @Override
    @Autowired
    protected void setService(MealService service) {
        super.setService(service);
    }
}
