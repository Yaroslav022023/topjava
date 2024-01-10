package ru.javawebinar.topjava.service.meal.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.meal.AbstractMealServiceTest;

@ActiveProfiles(Profiles.JDBC)
public class JdbcMealServiceTest extends AbstractMealServiceTest {
}
