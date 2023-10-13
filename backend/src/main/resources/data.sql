-- liquibase formatted sql

-- changeset Rooted:1695443436540-4
INSERT INTO organizer (name)
VALUES ('Organizer 1'),
       ('Organizer 2');

-- changeset Rooted:1695443436540-5
INSERT INTO event (title, description, location, start_time, end_time, organizer_id, completed, points, completed_at)
VALUES ('Event 1', 'Description for Event 1', 'Location 1', '2023-09-25 10:00:00', '2023-09-25 12:00:00', 1, true, 50, null),
       ('Event 2', 'Description for Event 2', 'Location 2', '2023-09-26 15:00:00', '2023-09-26 17:00:00', 2, false, 30, null),
       ('Event 3', 'Description for Event 2', 'Location 2', '2023-09-24 15:00:00', '2023-09-25 14:00:00', 2, false, 20, null);

-- changeset Rooted:1695443436540-6
INSERT INTO "user" (username, points)
VALUES ('User 1', 0),
       ('User 2', 0);

INSERT INTO participation (event_id, user_id, completed, completed_at)
VALUES (1, 1, false, null),
       (3, 1, false, null);
