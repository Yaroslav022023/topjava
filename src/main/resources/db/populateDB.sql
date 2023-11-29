DELETE FROM meals;
DELETE FROM user_role;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (dateTime, description, calories, user_id)
VALUES ('2020-01-30 10:00', 'breakfast', 500, 100000),
       ('2020-01-30 13:00', 'lunch', 1000, 100000),
       ('2020-01-30 20:00', 'dinner', 500, 100000),
       ('2020-01-31 00:00', 'Food on the boundary value', 100, 100000),
       ('2020-01-31 10:00', 'breakfast', 1000, 100000),
       ('2020-01-31 13:00', 'lunch', 500, 100000),
       ('2020-01-31 20:00', 'dinner', 410, 100000),
       ('2021-03-03 10:00', 'breakfast', 600, 100001),
       ('2021-03-03 13:00', 'lunch', 1000, 100001),
       ('2021-03-03 20:00', 'dinner', 500, 100001),
       ('2021-03-04 10:00', 'breakfast', 300, 100001),
       ('2021-03-04 13:00', 'lunch', 1000, 100001),
       ('2021-03-04 20:00', 'dinner', 500, 100001);