package ru.javawebinar.topjava.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.UserService;

@ActiveProfiles(Profiles.JPA)
public class JpaUserServiceTest extends AbstractUserServiceTest {

    @Override
    @Autowired
    protected void setService(UserService service) {
        super.setService(service);
    }
}
