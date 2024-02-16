insert into app_invitation_status (status)
values ('pending'),
       ('accepted'),
       ('declined');

-- Built-in triggers
WITH points_reached_trigger AS (
    INSERT INTO trigger (title, description, key, app_id) VALUES ('Points Reached', null, 'points_reached',
                                                                  null) RETURNING trigger_id)
INSERT
INTO trigger_field (key, type, label, trigger_id)
VALUES ('points', 'integer', 'Points', (SELECT trigger_id FROM points_reached_trigger));
