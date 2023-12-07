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

INSERT INTO meals (id, date_time, description, calories, user_id)
VALUES (100003, '2020-01-30 10:00', 'breakfast - user', 500, 100000),
       (100004, '2020-01-30 13:00', 'lunch - user', 1000, 100000),
       (100005, '2020-01-30 20:00', 'dinner - user', 500, 100000),
       (100006, '2020-01-31 00:00', 'Food on the boundary value - user', 100, 100000),
       (100007, '2020-01-31 10:00', 'breakfast - user', 1000, 100000),
       (100008, '2020-01-31 13:00', 'lunch - user', 500, 100000),
       (100009, '2020-01-31 20:00', 'dinner - user', 410, 100000),
       (100010, '2021-03-03 10:00', 'breakfast - admin', 600, 100001),
       (100011, '2021-03-03 13:00', 'lunch - admin', 1000, 100001),
       (100012, '2021-03-03 20:00', 'dinner - admin', 500, 100001),
       (100013, '2021-03-04 10:00', 'breakfast - admin', 300, 100001),
       (100014, '2021-03-04 13:00', 'lunch - admin', 1000, 100001),
       (100015, '2021-03-04 20:00', 'dinner - admin', 500, 100001);

SELECT setval('global_seq', (SELECT MAX(id) FROM meals) + 1);