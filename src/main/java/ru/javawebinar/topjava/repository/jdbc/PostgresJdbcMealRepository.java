package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;

@Repository
@Profile(Profiles.POSTGRES_DB)
public class PostgresJdbcMealRepository extends AbstractJdbcMealRepository<LocalDateTime> {
    @Autowired
    public PostgresJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected void initRowMapper() {
        super.rowMapper = BeanPropertyRowMapper.newInstance(Meal.class);
    }

    @Override
    protected LocalDateTime getDateTime(LocalDateTime dateTime) {
        return dateTime;
    }
}
