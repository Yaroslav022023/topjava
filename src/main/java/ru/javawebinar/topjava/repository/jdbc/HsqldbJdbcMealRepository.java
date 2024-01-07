package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
@Profile(Profiles.HSQL_DB)
public class HsqldbJdbcMealRepository extends AbstractJdbcMealRepository<Timestamp> {
    @Autowired
    public HsqldbJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected void initRowMapper() {
        super.rowMapper = (rs, rowNum) -> {
            Meal meal = new Meal();
            meal.setId(rs.getInt("id"));
            meal.setDescription(rs.getString("description"));
            meal.setCalories(rs.getInt("calories"));

            Timestamp timestamp = rs.getTimestamp("date_time");
            if (timestamp != null) {
                meal.setDateTime(timestamp.toLocalDateTime());
            }
            return meal;
        };
    }

    @Override
    protected Timestamp getDateTime(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime);
    }
}
