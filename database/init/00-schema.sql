create sequence achievement_activity_id_seq;

create sequence rule_progress_times_rule_progress_single_id_seq;

create table "user"
(
    user_id          bigint generated by default as identity
        constraint idx_16492_primary
            primary key,
    created_at       timestamp with time zone default now() not null,
    updated_at       timestamp with time zone default now() not null,
    external_user_id text                                   not null
        constraint user_pk
            unique,
    email            text                                   not null,
    google_user_id   text,
    name             text                                   not null
);

create table app
(
    app_id      bigint generated by default as identity
        constraint idx_16500_primary
            primary key,
    title       text                                   not null,
    description text,
    api_key     text                                   not null,
    created_at  timestamp with time zone default now() not null,
    updated_at  timestamp with time zone default now() not null,
    owner_id    bigint                                 not null
        constraint app_user_user_id_fk
            references "user",
    timezone    text
);

create table achievement
(
    achievement_id bigint generated by default as identity
        constraint idx_16391_primary
            primary key,
    title          text                                   not null,
    description    text,
    created_at     timestamp with time zone default now() not null,
    updated_at     timestamp with time zone default now() not null,
    image_key      text,
    app_id         bigint
        constraint achievement_app_app_id_fk
            references app,
    image_metadata jsonb,
    constraint achievement_pk
        unique (app_id, title)
);

create table trigger
(
    trigger_id  bigint generated by default as identity
        constraint idx_16468_primary
            primary key,
    title       text                                   not null,
    description text,
    created_at  timestamp with time zone default now() not null,
    updated_at  timestamp with time zone default now() not null,
    key         text                                   not null,
    app_id      bigint
        constraint trigger_app_app_id_fk
            references app,
    constraint trigger_pk
        unique (app_id, key),
    constraint trigger_pk_2
        unique (app_id, title)
);

create table rule
(
    rule_id                  bigint generated by default as identity
        constraint idx_16438_primary
            primary key,
    title                    text                                   not null,
    description              text,
    created_at               timestamp with time zone default now() not null,
    updated_at               timestamp with time zone default now() not null,
    trigger_id               bigint                                 not null
        constraint rule_ibfk_2
            references trigger,
    trigger_params           jsonb,
    app_id                   bigint
        constraint rule_app_app_id_fk
            references app,
    action                   text                                   not null,
    action_points            integer,
    action_achievement_id    bigint
        constraint rule_achievement_achievement_id_fk
            references achievement,
    repeatability            text                                   not null,
    condition_expression     text,
    deleted_at               timestamp with time zone,
    accumulatedfields        text[],
    action_points_expression text,
    constraint rule_pk
        unique (app_id, title)
);

create index idx_16438_trigger_id
    on rule (trigger_id);

create table app_user
(
    app_user_id  bigint generated by default as identity
        primary key,
    unique_id    text                                not null,
    display_name text                                not null,
    created_at   timestamp default current_timestamp not null,
    updated_at   timestamp default current_timestamp,
    app_id       bigint                              not null
        constraint app_user_app_app_id_fk
            references app,
    points       integer   default 0                 not null,
    user_id      bigint
        constraint app_user_user_user_id_fk
            references "user",
    constraint app_user_pk
        unique (app_id, unique_id)
);

create table rule_progress
(
    rule_progress_id bigint generated by default as identity
        constraint idx_16462_primary
            primary key,
    rule_id          bigint                                 not null
        constraint rule_progress_times_ibfk_1
            references rule,
    activation_count integer,
    created_at       timestamp with time zone default now() not null,
    updated_at       timestamp with time zone default now() not null,
    completed_at     timestamp with time zone,
    app_user_id      bigint
        constraint rule_progress_app_user_app_user_id_fk
            references app_user
            on delete cascade,
    app_id           bigint
        constraint rule_progress_app_app_id_fk
            references app,
    constraint rule_progress_pk
        unique (app_id, rule_id, app_user_id)
);

alter sequence rule_progress_times_rule_progress_single_id_seq owned by rule_progress.rule_progress_id;

create index idx_16462_rule_id
    on rule_progress (rule_id);

create table trigger_log
(
    trigger_log_id bigserial
        constraint trigger_log_pk
            primary key,
    trigger_id     integer
        constraint trigger_log_trigger_trigger_id_fk
            references trigger,
    request_body   jsonb                    not null,
    app_id         bigint                   not null
        constraint trigger_log_app_app_id_fk
            references app,
    app_user_id    bigint                   not null
        constraint trigger_log_app_user_app_user_id_fk
            references app_user
            on delete cascade,
    created_at     timestamp with time zone not null
);

create table app_user_achievement
(
    app_user_achievement_id bigint generated by default as identity
        constraint pk_app_user_achievement
            primary key,
    achievement_id          bigint not null
        constraint fk_app_user_achievement_on_achievement
            references achievement,
    app_user_id             bigint not null
        constraint fk_app_user_achievement_on_app_user
            references app_user
            on delete cascade,
    completed_at            timestamp,
    app_id                  bigint
        constraint app_user_achievement_app_app_id_fk
            references app
);

create table trigger_field
(
    trigger_field_id bigserial
        constraint trigger_field_pk
            primary key,
    key              text   not null,
    type             text   not null,
    label            text,
    trigger_id       bigint not null
        constraint trigger_field_trigger_trigger_id_fk
            references trigger,
    constraint trigger_field_pk_2
        unique (trigger_id, key)
);

create table app_user_points_history
(
    app_user_id                bigint                                 not null
        constraint app_user_points_history_app_user_app_user_id_fk
            references app_user
            on delete cascade,
    app_id                     bigint                                 not null
        constraint app_user_points_history_app_app_id_fk
            references app
            on delete cascade,
    points                     bigint                                 not null,
    points_change              bigint                                 not null,
    from_rule_id               bigint
        constraint app_user_points_history_rule_rule_id_fk
            references rule
            on delete cascade,
    created_at                 timestamp with time zone default now() not null,
    app_user_points_history_id bigserial
        constraint app_user_points_history_pk
            primary key
);

create table webhook
(
    webhook_id bigserial
        constraint webhook_pk
            primary key,
    app_id     bigint                                 not null
        constraint webhook_app_app_id_fk
            references app,
    url        text                                   not null,
    created_at timestamp with time zone default now() not null,
    updated_at timestamp with time zone,
    secret_key text                                   not null,
    deleted_at timestamp with time zone,
    constraint webhook_pk_2
        unique (app_id, url)
);

create table webhook_request_status
(
    webhook_request_status_id bigserial
        constraint webhook_request_status_pk
            primary key,
    name                      text
);

create table webhook_request
(
    webhook_request_id bigserial
        constraint webhook_request_pk
            primary key,
    webhook_id         bigint                                 not null
        constraint webhook_request_webhook_webhook_id_fk
            references webhook,
    request_body       jsonb                                  not null,
    status_id          smallint                               not null
        constraint webhook_request_status_fk
            references webhook_request_status,
    created_at         timestamp with time zone default now() not null,
    updated_at         timestamp with time zone,
    signature          text                                   not null,
    app_id             bigint                   default 1     not null
        constraint webhook_request_app_app_id_fk
            references app,
    constraint webhook_request_pk_2
        unique (app_id, signature)
);

