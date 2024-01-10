package ru.javawebinar.topjava.service.user.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.user.AbstractUserServiceTest;

@ActiveProfiles(Profiles.JPA)
public class JpaUserServiceTest extends AbstractUserServiceTest {
}
