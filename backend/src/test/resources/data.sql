-- Built-in triggers
with points_reached_trigger as (
    insert into trigger (title, description, key, app_id) values ('Points Reached', null, 'points_reached',
                                                                  null) returning trigger_id)
insert
into trigger_field (key, type, label, trigger_id)
values ('points', 'integer', 'Points', (select trigger_id from points_reached_trigger));

insert into webhook_request_status (webhook_request_status_id, name)
values (1, 'SUCCESS'),
       (2, 'FAILED');
