-- liquibase formatted sql

-- changeset Rooted:1695443436540-5
INSERT INTO activity (title, description)
VALUES ('Activity 1', 'Description for Activity 1'),
       ('Activity 2', 'Description for Activity 2'),
       ('Activity 3', 'Description for Activity 2');

INSERT INTO user (username, name, external_user_id)
VALUES ('user1', 'User 1', 'user1'),
       ('user2', 'User 2', 'user2'),
       ('user3', 'User 3', 'user3');

INSERT INTO member (user_id, activity_id, points)
VALUES (1, 1, 0),
       (2, 1, 20),
       (3, 1, 60);

INSERT INTO achievement (title, description, required_progress)
VALUES ('Achievement 1', 'Description for Achievement 1', 3);
