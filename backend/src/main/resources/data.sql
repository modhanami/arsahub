-- liquibase formatted sql

-- changeset Rooted:1695443436540-4
INSERT INTO organizer (name)
VALUES ('Organizer 1'),
       ('Organizer 2');

-- changeset Rooted:1695443436540-5
INSERT INTO event (title, description, location, start_date, end_date, organizer_id)
VALUES ('Event 1', 'Description for Event 1', 'Location 1', '2023-09-25 10:00:00', '2023-09-25 12:00:00', 1),
       ('Event 2', 'Description for Event 2', 'Location 2', '2023-09-26 15:00:00', '2023-09-26 17:00:00', 2);

-- changeset Rooted:1695443436540-6
INSERT INTO "user" (username)
VALUES ('User 1'),
       ('User 2');