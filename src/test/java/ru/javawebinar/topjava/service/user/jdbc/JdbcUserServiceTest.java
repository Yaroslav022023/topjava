package ru.javawebinar.topjava.service.user.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.user.AbstractUserServiceTest;

@ActiveProfiles(Profiles.JDBC)
public class JdbcUserServiceTest extends AbstractUserServiceTest {
}
