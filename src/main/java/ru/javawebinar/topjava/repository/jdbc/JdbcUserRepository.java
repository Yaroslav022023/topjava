package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert insertUser;
    private final Validator validator;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate, Validator validator) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.validator = validator;
    }

    @Override
    @Transactional
    public User save(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            insertRoles(user);
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            }
            updateRoles(user);
        }
        return user;
    }

    private void insertRoles(User user) {
        Set<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            List<SqlParameterSource> parameters = new ArrayList<>();
            for (Role role : roles) {
                MapSqlParameterSource param = new MapSqlParameterSource();
                param.addValue("userId", user.getId());
                param.addValue("role", role.name());
                parameters.add(param);
            }
            namedParameterJdbcTemplate.batchUpdate(
                    "INSERT INTO user_role (user_id, role) VALUES (:userId, :role)",
                    parameters.toArray(new SqlParameterSource[0])
            );
        }
    }

    private void updateRoles(User user) {
        Set<Role> currentRoles = new HashSet<>();
        jdbcTemplate.query(
                "SELECT role FROM user_role WHERE user_id = ?",
                (rs, rowNum) -> currentRoles.add(Role.valueOf(rs.getString("role"))),
                user.getId()
        );
        if (!currentRoles.equals(user.getRoles())) {
            jdbcTemplate.update("DELETE FROM user_role WHERE user_id = ?", user.getId());
            insertRoles(user);
        }
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT u.*, r.role FROM users u LEFT JOIN user_role r ON u.id = r.user_id WHERE u.id=?",
                new UserWithRolesExtractor(), id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                "SELECT u.*, r.role FROM users u LEFT JOIN user_role r ON u.id = r.user_id WHERE u.email=?",
                new UserWithRolesExtractor(), email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT u.*, r.role FROM users u LEFT JOIN user_role r ON u.id = r.user_id ORDER BY u.name, u.email",
                new UserWithRolesExtractor());
    }

    private static class UserWithRolesExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
            final Map<Integer, User> userMap = new LinkedHashMap<>();

            while (rs.next()) {
                int userId = rs.getInt("id");
                User user = userMap.get(userId);

                if (user == null) {
                    user = ROW_MAPPER.mapRow(rs, 0);
                    assert user != null;
                    user.setRoles(new HashSet<>());
                    userMap.put(userId, user);
                }

                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    user.getRoles().add(Role.valueOf(roleStr));
                }
            }

            return new ArrayList<>(userMap.values());
        }
    }
}
