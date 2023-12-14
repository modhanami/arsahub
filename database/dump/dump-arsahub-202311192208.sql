--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: arsahub_dev; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA arsahub_dev;


ALTER SCHEMA arsahub_dev OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: achievement; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.achievement (
    achievement_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    image_url text,
    activity_id bigint NOT NULL
);


ALTER TABLE arsahub_dev.achievement OWNER TO postgres;

--
-- Name: achievement_achievement_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.achievement_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.achievement_achievement_id_seq OWNER TO postgres;

--
-- Name: achievement_achievement_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.achievement_achievement_id_seq OWNED BY arsahub_dev.achievement.achievement_id;


--
-- Name: achievement_activity_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.achievement_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.achievement_activity_id_seq OWNER TO postgres;

--
-- Name: achievement_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.achievement_activity_id_seq OWNED BY arsahub_dev.achievement.activity_id;


--
-- Name: action; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.action (
    effect_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    json_schema jsonb,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL
);


ALTER TABLE arsahub_dev.action OWNER TO postgres;

--
-- Name: activity; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.activity (
    activity_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    external_system_id bigint
);


ALTER TABLE arsahub_dev.activity OWNER TO postgres;

--
-- Name: activity_activity_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.activity_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.activity_activity_id_seq OWNER TO postgres;

--
-- Name: activity_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.activity_activity_id_seq OWNED BY arsahub_dev.activity.activity_id;


--
-- Name: custom_unit; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.custom_unit (
    unit_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    key text NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arsahub_dev.custom_unit OWNER TO postgres;

--
-- Name: effect_effect_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.effect_effect_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.effect_effect_id_seq OWNER TO postgres;

--
-- Name: effect_effect_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.effect_effect_id_seq OWNED BY arsahub_dev.action.effect_id;


--
-- Name: external_system; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.external_system (
    external_system_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    api_key uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arsahub_dev.external_system OWNER TO postgres;

--
-- Name: external_system_external_system_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.external_system_external_system_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.external_system_external_system_id_seq OWNER TO postgres;

--
-- Name: external_system_external_system_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.external_system_external_system_id_seq OWNED BY arsahub_dev.external_system.external_system_id;


--
-- Name: rule; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.rule (
    rule_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    activity_id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    trigger_id bigint NOT NULL,
    trigger_type_id bigint,
    effect_id bigint NOT NULL,
    trigger_type_params jsonb,
    effect_params jsonb,
    trigger_params jsonb
);


ALTER TABLE arsahub_dev.rule OWNER TO postgres;

--
-- Name: rule_progress_streak; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.rule_progress_streak (
    rule_progress_streak_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    current_streak integer NOT NULL,
    last_interaction_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arsahub_dev.rule_progress_streak OWNER TO postgres;

--
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq OWNER TO postgres;

--
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq OWNED BY arsahub_dev.rule_progress_streak.rule_progress_streak_id;


--
-- Name: rule_progress_times; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.rule_progress_times (
    rule_progress_single_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    progress integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at timestamp with time zone
);


ALTER TABLE arsahub_dev.rule_progress_times OWNER TO postgres;

--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq OWNER TO postgres;

--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq OWNED BY arsahub_dev.rule_progress_times.rule_progress_single_id;


--
-- Name: rule_rule_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.rule_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.rule_rule_id_seq OWNER TO postgres;

--
-- Name: rule_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.rule_rule_id_seq OWNED BY arsahub_dev.rule.rule_id;


--
-- Name: trigger; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.trigger (
    trigger_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL,
    json_schema jsonb
);


ALTER TABLE arsahub_dev.trigger OWNER TO postgres;

--
-- Name: trigger_trigger_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.trigger_trigger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.trigger_trigger_id_seq OWNER TO postgres;

--
-- Name: trigger_trigger_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.trigger_trigger_id_seq OWNED BY arsahub_dev.trigger.trigger_id;


--
-- Name: trigger_type; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.trigger_type (
    trigger_type_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    json_schema jsonb,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL
);


ALTER TABLE arsahub_dev.trigger_type OWNER TO postgres;

--
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq OWNER TO postgres;

--
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq OWNED BY arsahub_dev.trigger_type.trigger_type_id;


--
-- Name: unit_unit_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.unit_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.unit_unit_id_seq OWNER TO postgres;

--
-- Name: unit_unit_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.unit_unit_id_seq OWNED BY arsahub_dev.custom_unit.unit_id;


--
-- Name: user; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev."user" (
    user_id bigint NOT NULL,
    username character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    external_user_id character varying(255) NOT NULL,
    external_system_id bigint,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arsahub_dev."user" OWNER TO postgres;

--
-- Name: user_activity; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.user_activity (
    user_activity_id bigint NOT NULL,
    user_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    points integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arsahub_dev.user_activity OWNER TO postgres;

--
-- Name: user_activity_achievement; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.user_activity_achievement (
    user_activity_achievement_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    completed_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arsahub_dev.user_activity_achievement OWNER TO postgres;

--
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq OWNER TO postgres;

--
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq OWNED BY arsahub_dev.user_activity_achievement.user_activity_achievement_id;


--
-- Name: user_activity_point_history; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.user_activity_point_history (
    user_activity_point_history_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    points integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description character varying(255)
);


ALTER TABLE arsahub_dev.user_activity_point_history OWNER TO postgres;

--
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq OWNER TO postgres;

--
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq OWNED BY arsahub_dev.user_activity_point_history.user_activity_point_history_id;


--
-- Name: user_activity_progress; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.user_activity_progress (
    user_activity_progress_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    user_activity_id integer NOT NULL,
    unit_id integer NOT NULL,
    progress_value integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE arsahub_dev.user_activity_progress OWNER TO postgres;

--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.user_activity_progress_user_activity_progress_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.user_activity_progress_user_activity_progress_id_seq OWNER TO postgres;

--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_progress_user_activity_progress_id_seq OWNED BY arsahub_dev.user_activity_progress.user_activity_progress_id;


--
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.user_activity_user_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.user_activity_user_activity_id_seq OWNER TO postgres;

--
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_user_activity_id_seq OWNED BY arsahub_dev.user_activity.user_activity_id;


--
-- Name: user_user_id_seq; Type: SEQUENCE; Schema: arsahub_dev; Owner: postgres
--

CREATE SEQUENCE arsahub_dev.user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arsahub_dev.user_user_id_seq OWNER TO postgres;

--
-- Name: user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_user_id_seq OWNED BY arsahub_dev."user".user_id;


--
-- Name: achievement achievement_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.achievement ALTER COLUMN achievement_id SET DEFAULT nextval('arsahub_dev.achievement_achievement_id_seq'::regclass);


--
-- Name: action effect_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.action ALTER COLUMN effect_id SET DEFAULT nextval('arsahub_dev.effect_effect_id_seq'::regclass);


--
-- Name: activity activity_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.activity ALTER COLUMN activity_id SET DEFAULT nextval('arsahub_dev.activity_activity_id_seq'::regclass);


--
-- Name: custom_unit unit_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.custom_unit ALTER COLUMN unit_id SET DEFAULT nextval('arsahub_dev.unit_unit_id_seq'::regclass);


--
-- Name: external_system external_system_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.external_system ALTER COLUMN external_system_id SET DEFAULT nextval('arsahub_dev.external_system_external_system_id_seq'::regclass);


--
-- Name: rule rule_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule ALTER COLUMN rule_id SET DEFAULT nextval('arsahub_dev.rule_rule_id_seq'::regclass);


--
-- Name: rule_progress_streak rule_progress_streak_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak ALTER COLUMN rule_progress_streak_id SET DEFAULT nextval('arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq'::regclass);


--
-- Name: rule_progress_times rule_progress_single_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times ALTER COLUMN rule_progress_single_id SET DEFAULT nextval('arsahub_dev.rule_progress_times_rule_progress_single_id_seq'::regclass);


--
-- Name: trigger trigger_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger ALTER COLUMN trigger_id SET DEFAULT nextval('arsahub_dev.trigger_trigger_id_seq'::regclass);


--
-- Name: trigger_type trigger_type_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger_type ALTER COLUMN trigger_type_id SET DEFAULT nextval('arsahub_dev.trigger_type_trigger_type_id_seq'::regclass);


--
-- Name: user user_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev."user" ALTER COLUMN user_id SET DEFAULT nextval('arsahub_dev.user_user_id_seq'::regclass);


--
-- Name: user_activity user_activity_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity ALTER COLUMN user_activity_id SET DEFAULT nextval('arsahub_dev.user_activity_user_activity_id_seq'::regclass);


--
-- Name: user_activity_achievement user_activity_achievement_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement ALTER COLUMN user_activity_achievement_id SET DEFAULT nextval('arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq'::regclass);


--
-- Name: user_activity_point_history user_activity_point_history_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history ALTER COLUMN user_activity_point_history_id SET DEFAULT nextval('arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq'::regclass);


--
-- Name: user_activity_progress user_activity_progress_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_progress ALTER COLUMN user_activity_progress_id SET DEFAULT nextval('arsahub_dev.user_activity_progress_user_activity_progress_id_seq'::regclass);


--
-- Data for Name: achievement; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.achievement (achievement_id, title, description, created_at, updated_at, image_url, activity_id) FROM stdin;
1	Achievement 1	\N	2023-11-03 00:54:51.690334+07	2023-11-03 00:54:51.690334+07	\N	1
2	I Made This Achievement :)	\N	2023-11-03 18:35:58.757865+07	2023-11-03 18:35:58.757865+07	\N	1
\.


--
-- Data for Name: action; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.action (effect_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Add points	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	2023-10-31 20:54:49.958514+07	2023-10-31 20:54:49.958514+07	add_points
2	Unlock achievement	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}	2023-10-31 21:08:24.064419+07	2023-10-31 21:08:24.064419+07	unlock_achievement
\.


--
-- Data for Name: activity; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.activity (activity_id, title, description, created_at, updated_at, external_system_id) FROM stdin;
1	Look at him go	\N	2023-10-31 21:38:58.519792+07	2023-10-31 21:38:58.519792+07	\N
3	Activity Bing Bong	we are bing bonging with this one	2023-11-05 22:41:14.738463+07	2023-11-05 22:41:14.738463+07	\N
4	Xaa	X	2023-11-05 22:51:48.209876+07	2023-11-05 22:51:48.209876+07	\N
5	What	X	2023-11-05 22:52:13.943558+07	2023-11-05 22:52:13.943558+07	\N
6	xxd	xx	2023-11-05 22:55:23.623227+07	2023-11-05 22:55:23.623227+07	\N
7	xxd	xx	2023-11-05 22:55:34.356229+07	2023-11-05 22:55:34.356229+07	\N
8	dsdsd	ddd	2023-11-05 22:57:16.759871+07	2023-11-05 22:57:16.759871+07	\N
9	223	33	2023-11-05 23:11:11.058644+07	2023-11-05 23:11:11.058644+07	\N
10	dwqdqwdqw	dqwdqwqd	2023-11-06 00:59:37.605306+07	2023-11-06 00:59:37.605306+07	\N
11	FFF	F	2023-11-07 10:45:47.133377+07	2023-11-07 10:45:47.133377+07	\N
\.


--
-- Data for Name: custom_unit; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.custom_unit (unit_id, name, key, created_at, updated_at) FROM stdin;
4	Steps	steps2	2023-11-18 21:13:00.913441+07	2023-11-18 21:13:00.913441+07
5	Steps2	steps23	2023-11-18 21:14:43.406721+07	2023-11-18 21:14:43.406721+07
6	Steps2	steps233	2023-11-18 21:21:41.765821+07	2023-11-18 21:21:41.765821+07
7	Steps	steps	2023-11-19 17:34:16.893283+07	2023-11-19 17:34:16.893283+07
8	Steps 55	steps55	2023-11-19 21:57:01.788833+07	2023-11-19 21:57:01.788833+07
\.


--
-- Data for Name: external_system; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.external_system (external_system_id, title, description, api_key, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: rule; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.rule (rule_id, title, description, activity_id, created_at, updated_at, trigger_id, trigger_type_id, effect_id, trigger_type_params, effect_params, trigger_params) FROM stdin;
25	Give 1 point when points reached 1000	\N	1	2023-11-05 00:15:29.781097+07	2023-11-05 00:15:29.781097+07	8	\N	1	\N	{"value": "1"}	{"value": "1000"}
31	New rule	New rule	1	2023-11-07 14:55:51.25185+07	2023-11-07 14:55:51.25185+07	3	\N	1	\N	{"value": "32"}	\N
46	New rule	New rule	1	2023-11-18 22:06:25.669851+07	2023-11-18 22:06:25.669851+07	12	\N	1	\N	{"value": "1"}	{"value": "32"}
47	New rule	New rule	1	2023-11-18 22:14:37.52776+07	2023-11-18 22:14:37.52776+07	12	\N	1	\N	{"value": "2"}	{"value": "32"}
48	New rule	New rule	1	2023-11-18 22:19:52.827381+07	2023-11-18 22:19:52.827381+07	12	\N	1	\N	{"value": "50"}	{"value": "1"}
32	New rule	New rule	1	2023-11-07 16:30:39.868254+07	2023-11-07 16:30:39.868254+07	10	\N	2	\N	{"achievementId": "1"}	\N
33	New rule	New rule	1	2023-11-07 21:40:52.268958+07	2023-11-07 21:40:52.268958+07	3	\N	1	\N	{"value": "32"}	\N
49	Give 50 points when reaching 100 Steps	New rule	1	2023-11-19 17:36:14.787078+07	2023-11-19 17:36:14.787078+07	14	\N	1	\N	{"value": "50"}	{"value": "100"}
1	Give points on joining activity	\N	1	2023-10-31 21:30:00.541204+07	2023-10-31 21:30:00.541204+07	1	\N	1	\N	{"value": 100}	\N
35	rule1	rule1	1	2023-11-18 11:46:35.155974+07	2023-11-18 11:46:35.155974+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
36	rule1	rule1	1	2023-11-18 11:46:43.508359+07	2023-11-18 11:46:43.508359+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
37	rule1	rule1	1	2023-11-18 12:07:35.36285+07	2023-11-18 12:07:35.36285+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
38	rule1	rule1	1	2023-11-18 12:10:25.107447+07	2023-11-18 12:10:25.107447+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
39	rule1	rule1	1	2023-11-18 13:04:55.972889+07	2023-11-18 13:04:55.972889+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
40	rule1	rule1	1	2023-11-18 13:08:14.810363+07	2023-11-18 13:08:14.810363+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
41	rule1	rule1	1	2023-11-18 13:10:34.349667+07	2023-11-18 13:10:34.349667+07	1	\N	1	{"count": "2", "period": "day"}	{"value": "100"}	\N
44	New rule	New rule	1	2023-11-18 14:50:48.14933+07	2023-11-18 14:50:48.14933+07	1	\N	1	\N	{"value": "2"}	\N
45	New rule	New rule	1	2023-11-18 22:00:41.306757+07	2023-11-18 22:00:41.306757+07	12	\N	1	\N	{"value": "1"}	{"value": "4"}
\.


--
-- Data for Name: rule_progress_streak; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.rule_progress_streak (rule_progress_streak_id, rule_id, user_activity_id, current_streak, last_interaction_at, created_at) FROM stdin;
\.


--
-- Data for Name: rule_progress_times; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.rule_progress_times (rule_progress_single_id, rule_id, user_activity_id, progress, created_at, updated_at, completed_at) FROM stdin;
67	25	3	1	2023-11-05 00:31:07.135253+07	2023-11-05 00:31:07.135253+07	2023-11-05 00:31:07.132252+07
68	49	3	1	2023-11-19 17:38:32.870899+07	2023-11-19 17:38:32.870899+07	2023-11-19 17:38:32.865899+07
\.


--
-- Data for Name: trigger; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.trigger (trigger_id, title, description, created_at, updated_at, key, json_schema) FROM stdin;
3	Share activity	ddd	2023-11-01 23:19:49.489555+07	2023-11-01 23:19:49.489555+07	share_activity	\N
10	Don't know	ddd	2023-11-01 23:19:49.489555+07	2023-11-01 23:19:49.489555+07	ayo	\N
11	22	ddd	2023-11-01 23:19:49.489555+07	2023-11-01 23:19:49.489555+07	ayo2	\N
1	Join activity	\N	2023-10-31 21:12:50.354516+07	2023-10-31 21:12:50.354516+07	join_activity	\N
12	Steps2 reached	Triggered when Steps2 reached a certain value	2023-11-18 21:14:43.44072+07	2023-11-18 21:14:43.44072+07	steps23_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}
13	Steps2 reached	Triggered when Steps2 reached a certain value	2023-11-18 21:21:41.837821+07	2023-11-18 21:21:41.837821+07	steps233_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}
8	Points reached	\N	2023-11-04 23:14:46.747363+07	2023-11-04 23:14:46.747363+07	points_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}
14	Steps reached	Triggered when Steps reached a certain value	2023-11-19 17:34:16.922284+07	2023-11-19 17:34:16.922284+07	steps_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}
17	Share activity	ddd	2023-11-19 21:54:53.360143+07	2023-11-19 21:54:53.360143+07	share_activity2	\N
19	Steps 55 reached	Triggered when Steps 55 reached a certain value	2023-11-19 21:57:01.80983+07	2023-11-19 21:57:01.80983+07	steps55_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}
\.


--
-- Data for Name: trigger_type; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.trigger_type (trigger_type_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Times 	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["count"], "properties": {"count": {"type": "number"}}}	2023-11-02 00:32:54.776542+07	2023-11-02 00:32:54.776542+07	times
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev."user" (user_id, username, name, external_user_id, external_system_id, created_at, updated_at) FROM stdin;
1	user1	User One	i_am_user1	\N	2023-10-31 22:01:20.604671+07	2023-10-31 22:01:20.604671+07
\.


--
-- Data for Name: user_activity; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity (user_activity_id, user_id, activity_id, points, created_at, updated_at) FROM stdin;
3	1	1	75785	2023-10-31 23:14:30.47859+07	2023-11-19 17:38:32.8489+07
\.


--
-- Data for Name: user_activity_achievement; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity_achievement (user_activity_achievement_id, achievement_id, user_activity_id, completed_at, created_at, updated_at) FROM stdin;
2	2	3	\N	2023-11-04 21:48:37.59253+07	2023-11-04 21:48:37.59253+07
3	1	3	\N	2023-11-12 22:43:14.786375+07	2023-11-12 22:43:14.786375+07
\.


--
-- Data for Name: user_activity_point_history; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity_point_history (user_activity_point_history_id, user_activity_id, activity_id, points, created_at, description) FROM stdin;
\.


--
-- Data for Name: user_activity_progress; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity_progress (user_activity_progress_id, activity_id, user_activity_id, unit_id, progress_value, created_at, updated_at) FROM stdin;
2	1	3	7	130	2023-11-19 10:36:32.224317	2023-11-19 10:38:44.907928
\.


--
-- Name: achievement_achievement_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.achievement_achievement_id_seq', 2, true);


--
-- Name: achievement_activity_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.achievement_activity_id_seq', 1, false);


--
-- Name: activity_activity_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.activity_activity_id_seq', 11, true);


--
-- Name: effect_effect_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.effect_effect_id_seq', 2, true);


--
-- Name: external_system_external_system_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.external_system_external_system_id_seq', 1, false);


--
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq', 1, false);


--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.rule_progress_times_rule_progress_single_id_seq', 68, true);


--
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.rule_rule_id_seq', 49, true);


--
-- Name: trigger_trigger_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.trigger_trigger_id_seq', 19, true);


--
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.trigger_type_trigger_type_id_seq', 1, true);


--
-- Name: unit_unit_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.unit_unit_id_seq', 8, true);


--
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq', 3, true);


--
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq', 1, false);


--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_progress_user_activity_progress_id_seq', 2, true);


--
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_user_activity_id_seq', 3, true);


--
-- Name: user_user_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_user_id_seq', 1, true);


--
-- Name: action action_un; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.action
    ADD CONSTRAINT action_un UNIQUE (key);


--
-- Name: achievement idx_16391_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.achievement
    ADD CONSTRAINT idx_16391_primary PRIMARY KEY (achievement_id);


--
-- Name: user_activity_achievement idx_16400_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT idx_16400_primary PRIMARY KEY (user_activity_achievement_id);


--
-- Name: activity idx_16407_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.activity
    ADD CONSTRAINT idx_16407_primary PRIMARY KEY (activity_id);


--
-- Name: action idx_16416_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.action
    ADD CONSTRAINT idx_16416_primary PRIMARY KEY (effect_id);


--
-- Name: user_activity idx_16425_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT idx_16425_primary PRIMARY KEY (user_activity_id);


--
-- Name: user_activity_point_history idx_16432_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT idx_16432_primary PRIMARY KEY (user_activity_point_history_id);


--
-- Name: rule idx_16438_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT idx_16438_primary PRIMARY KEY (rule_id);


--
-- Name: rule_progress_streak idx_16456_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT idx_16456_primary PRIMARY KEY (rule_progress_streak_id);


--
-- Name: rule_progress_times idx_16462_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT idx_16462_primary PRIMARY KEY (rule_progress_single_id);


--
-- Name: trigger idx_16468_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT idx_16468_primary PRIMARY KEY (trigger_id);


--
-- Name: trigger_type idx_16483_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger_type
    ADD CONSTRAINT idx_16483_primary PRIMARY KEY (trigger_type_id);


--
-- Name: user idx_16492_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev."user"
    ADD CONSTRAINT idx_16492_primary PRIMARY KEY (user_id);


--
-- Name: external_system idx_16500_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.external_system
    ADD CONSTRAINT idx_16500_primary PRIMARY KEY (external_system_id);


--
-- Name: trigger_type trigger_type_un; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger_type
    ADD CONSTRAINT trigger_type_un UNIQUE (key);


--
-- Name: trigger trigger_un; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT trigger_un UNIQUE (key);


--
-- Name: custom_unit unit_pkey; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.custom_unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_id);


--
-- Name: user_activity_progress user_activity_progress_pkey; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_progress
    ADD CONSTRAINT user_activity_progress_pkey PRIMARY KEY (user_activity_progress_id);


--
-- Name: idx_16400_achievement_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16400_achievement_id ON arsahub_dev.user_activity_achievement USING btree (achievement_id);


--
-- Name: idx_16425_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16425_activity_id ON arsahub_dev.user_activity USING btree (activity_id);


--
-- Name: idx_16425_user_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16425_user_id ON arsahub_dev.user_activity USING btree (user_id);


--
-- Name: idx_16432_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16432_activity_id ON arsahub_dev.user_activity_point_history USING btree (activity_id);


--
-- Name: idx_16432_user_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16432_user_activity_id ON arsahub_dev.user_activity_point_history USING btree (user_activity_id);


--
-- Name: idx_16438_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_activity_id ON arsahub_dev.rule USING btree (activity_id);


--
-- Name: idx_16438_effect_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_effect_id ON arsahub_dev.rule USING btree (effect_id);


--
-- Name: idx_16438_trigger_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_trigger_id ON arsahub_dev.rule USING btree (trigger_id);


--
-- Name: idx_16438_trigger_type_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_trigger_type_id ON arsahub_dev.rule USING btree (trigger_type_id);


--
-- Name: idx_16456_rule_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16456_rule_id ON arsahub_dev.rule_progress_streak USING btree (rule_id);


--
-- Name: idx_16456_user_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16456_user_activity_id ON arsahub_dev.rule_progress_streak USING btree (user_activity_id);


--
-- Name: idx_16462_rule_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16462_rule_id ON arsahub_dev.rule_progress_times USING btree (rule_id);


--
-- Name: idx_16462_user_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16462_user_activity_id ON arsahub_dev.rule_progress_times USING btree (user_activity_id);


--
-- Name: idx_16492_external_user_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE UNIQUE INDEX idx_16492_external_user_id ON arsahub_dev."user" USING btree (external_user_id);


--
-- Name: achievement achievement_fk; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.achievement
    ADD CONSTRAINT achievement_fk FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- Name: activity activity_fk; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.activity
    ADD CONSTRAINT activity_fk FOREIGN KEY (external_system_id) REFERENCES arsahub_dev.external_system(external_system_id);


--
-- Name: rule rule_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_1 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- Name: rule rule_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_2 FOREIGN KEY (trigger_id) REFERENCES arsahub_dev.trigger(trigger_id);


--
-- Name: rule rule_ibfk_3; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_3 FOREIGN KEY (trigger_type_id) REFERENCES arsahub_dev.trigger_type(trigger_type_id);


--
-- Name: rule rule_ibfk_4; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_4 FOREIGN KEY (effect_id) REFERENCES arsahub_dev.action(effect_id);


--
-- Name: rule_progress_streak rule_progress_streak_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_1 FOREIGN KEY (rule_id) REFERENCES arsahub_dev.rule(rule_id);


--
-- Name: rule_progress_streak rule_progress_streak_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- Name: rule_progress_times rule_progress_times_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_1 FOREIGN KEY (rule_id) REFERENCES arsahub_dev.rule(rule_id);


--
-- Name: rule_progress_times rule_progress_times_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- Name: user_activity_achievement user_activity_achievement_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_1 FOREIGN KEY (achievement_id) REFERENCES arsahub_dev.achievement(achievement_id);


--
-- Name: user_activity_achievement user_activity_achievement_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- Name: user_activity user_activity_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT user_activity_ibfk_1 FOREIGN KEY (user_id) REFERENCES arsahub_dev."user"(user_id);


--
-- Name: user_activity user_activity_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT user_activity_ibfk_2 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- Name: user_activity_point_history user_activity_point_history_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_1 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- Name: user_activity_point_history user_activity_point_history_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_2 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- Name: user_activity_progress user_activity_progress_activity_id_fkey; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_progress
    ADD CONSTRAINT user_activity_progress_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- Name: user_activity_progress user_activity_progress_unit_id_fkey; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_progress
    ADD CONSTRAINT user_activity_progress_unit_id_fkey FOREIGN KEY (unit_id) REFERENCES arsahub_dev.custom_unit(unit_id);


--
-- Name: user_activity_progress user_activity_progress_user_activity_id_fkey; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_progress
    ADD CONSTRAINT user_activity_progress_user_activity_id_fkey FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- Name: user user_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev."user"
    ADD CONSTRAINT user_ibfk_1 FOREIGN KEY (external_system_id) REFERENCES arsahub_dev.external_system(external_system_id);


--
-- PostgreSQL database dump complete
--

