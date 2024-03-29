create sequence achievement_activity_id_seq;

create sequence rule_progress_times_rule_progress_single_id_seq;

create sequence user_activity_progress_user_activity_progress_id_seq;

create sequence rule_state_rule_state_id_seq;

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
            references "user"
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
    image_metadata jsonb
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
            references app
);

create table rule
(
    rule_id               bigint generated by default as identity
        constraint idx_16438_primary
            primary key,
    title                 text                                   not null,
    description           text,
    created_at            timestamp with time zone default now() not null,
    updated_at            timestamp with time zone default now() not null,
    trigger_id            bigint                                 not null
        constraint rule_ibfk_2
            references trigger,
    trigger_params        jsonb,
    app_id                bigint
        constraint rule_app_app_id_fk
            references app,
    action                text                                   not null,
    action_points         integer,
    action_achievement_id bigint
        constraint rule_achievement_achievement_id_fk
            references achievement,
    repeatability         text                                   not null,
    condition_expression  text,
    deleted_at            timestamp with time zone,
    accumulatedfields     text[]
);

create index idx_16438_trigger_id
    on rule (trigger_id);

create table app_user
(
    app_user_id  bigint generated by default as identity
        primary key,
    unique_id    text                                not null,
    display_name text                                not null,
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    updated_at   timestamp default CURRENT_TIMESTAMP,
    app_id       bigint                              not null
        constraint app_user_app_app_id_fk
            references app,
    points       integer   default 0                 not null,
    user_id      bigint
        constraint app_user_user_user_id_fk
            references "user"
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
            references app
);

alter sequence rule_progress_times_rule_progress_single_id_seq owned by rule_progress.rule_progress_id;

create index idx_16462_rule_id
    on rule_progress (rule_id);

create table custom_unit_type
(
    custom_unit_type_id bigserial
        constraint custom_unit_type_pk
            primary key,
    name                text not null
);

create table custom_unit
(
    unit_id             bigint generated by default as identity
        constraint unit_pkey
            primary key,
    name                text                                   not null,
    key                 text                                   not null,
    created_at          timestamp with time zone default now() not null,
    updated_at          timestamp with time zone default now() not null,
    app_id              bigint                                 not null
        constraint custom_unit___fk
            references app,
    custom_unit_type_id integer                                not null
        constraint custom_unit_custom_unit_type_custom_unit_type_id_fk
            references custom_unit_type
);

create table app_user_progress
(
    app_user_activity_progress_id bigint generated by default as identity
        constraint user_activity_progress_pkey
            primary key,
    unit_id                       integer                 not null
        constraint user_activity_progress_unit_id_fkey
            references custom_unit,
    created_at                    timestamp default now() not null,
    updated_at                    timestamp default now() not null,
    value_int                     integer,
    value_int_array               integer[],
    app_user_id                   bigint
        constraint app_user_progress_app_user_app_user_id_fk
            references app_user
);

alter sequence user_activity_progress_user_activity_progress_id_seq owned by app_user_progress.app_user_activity_progress_id;

create table trigger_log
(
    trigger_log_id bigserial
        constraint trigger_log_pk
            primary key,
    trigger_id     integer
        constraint trigger_log_trigger_trigger_id_fk
            references trigger,
    request_body   jsonb  not null,
    app_id         bigint not null
        constraint trigger_log_app_app_id_fk
            references app,
    app_user_id    bigint not null
        constraint trigger_log_app_user_app_user_id_fk
            references app_user
            on delete cascade
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
            references trigger
);

create table reward_type
(
    reward_type_id bigserial
        primary key,
    key            text not null,
    name           text not null
        unique
);

create table reward_template
(
    reward_template_id bigserial
        primary key,
    name               text    not null,
    description        text,
    price              integer not null,
    type_id            bigint  not null
        references reward_type,
    data               jsonb,
    unique (name, type_id)
);

create table reward
(
    reward_id      bigserial
        primary key,
    app_id         bigint                  not null
        references app,
    name           text                    not null,
    description    text,
    price          integer                 not null,
    type_id        bigint
        references reward_type,
    data           jsonb,
    created_at     timestamp default now() not null,
    updated_at     timestamp default now() not null,
    quantity       integer,
    image_key      text,
    image_metadata jsonb,
    unique (name, type_id)
);

create table transaction
(
    transaction_id   bigserial
        primary key,
    app_user_id      bigint                  not null
        references app_user
            on delete cascade,
    reward_id        bigint                  not null
        references reward,
    points_spent     integer                 not null,
    created_at       timestamp default now() not null,
    app_id           bigint                  not null
        constraint transaction_app_app_id_fk
            references app,
    reference_number text                    not null
        constraint transaction_pk
            unique
);

create table app_invitation_status
(
    id         bigserial
        primary key,
    status     text                                not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table app_invitation
(
    id                   bigserial
        primary key,
    app_id               integer                             not null
        references app,
    user_id              integer                             not null
        references "user",
    invitation_status_id integer                             not null
        references app_invitation_status,
    created_at           timestamp default CURRENT_TIMESTAMP not null,
    updated_at           timestamp default CURRENT_TIMESTAMP not null
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
    updated_at timestamp with time zone
);

create table leaderboard_type
(
    leaderboard_type_id bigserial
        constraint leaderboard_type_pk
            primary key,
    name                text not null
);

create table leaderboard_config
(
    leaderboard_config_id bigserial
        constraint leaderboard_config_pk
            primary key,
    app_id                bigint   not null
        constraint leaderboard_config_app_app_id_fk
            references app,
    leaderboard_type_id   smallint not null
        constraint leaderboard_config_leaderboard_type_leaderboard_type_id_fk
            references leaderboard_type,
    start_day             smallint,
    reset_day             smallint,
    reset_time            time     not null,
    timezone              text     not null
);

create table rule_trigger_field_state
(
    rule_trigger_field_state_id bigint default nextval('rule_state_rule_state_id_seq'::regclass) not null
        constraint rule_trigger_field_state_pk
            primary key,
    app_id                      bigint                                                           not null
        constraint rule_trigger_field_state_app_app_id_fk
            references app,
    app_user_id                 bigint                                                           not null
        constraint rule_trigger_field_state_app_user_app_user_id_fk
            references app_user,
    trigger_field_id            bigint                                                           not null
        constraint rule_trigger_field_state_trigger_field_trigger_field_id_fk
            references trigger_field,
    created_at                  timestamp with time zone                                         not null,
    updated_at                  timestamp with time zone,
    state_int_list              integer[],
    rule_id                     bigint                                                           not null
        constraint rule_trigger_field_state_rule_rule_id_fk
            references rule
);

alter sequence rule_state_rule_state_id_seq owned by rule_trigger_field_state.rule_trigger_field_state_id;

