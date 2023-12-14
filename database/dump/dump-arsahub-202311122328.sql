--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

-- Started on 2023-11-12 23:28:59

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
-- TOC entry 6 (class 2615 OID 27170)
-- Name: arsahub_dev; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA arsahub_dev;


ALTER SCHEMA arsahub_dev OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 242 (class 1259 OID 27171)
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
-- TOC entry 243 (class 1259 OID 27178)
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
-- TOC entry 5025 (class 0 OID 0)
-- Dependencies: 243
-- Name: achievement_achievement_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.achievement_achievement_id_seq OWNED BY arsahub_dev.achievement.achievement_id;


--
-- TOC entry 268 (class 1259 OID 32768)
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
-- TOC entry 5026 (class 0 OID 0)
-- Dependencies: 268
-- Name: achievement_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.achievement_activity_id_seq OWNED BY arsahub_dev.achievement.activity_id;


--
-- TOC entry 244 (class 1259 OID 27179)
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
-- TOC entry 245 (class 1259 OID 27186)
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
-- TOC entry 246 (class 1259 OID 27193)
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
-- TOC entry 5027 (class 0 OID 0)
-- Dependencies: 246
-- Name: activity_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.activity_activity_id_seq OWNED BY arsahub_dev.activity.activity_id;


--
-- TOC entry 247 (class 1259 OID 27194)
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
-- TOC entry 5028 (class 0 OID 0)
-- Dependencies: 247
-- Name: effect_effect_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.effect_effect_id_seq OWNED BY arsahub_dev.action.effect_id;


--
-- TOC entry 248 (class 1259 OID 27195)
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
-- TOC entry 249 (class 1259 OID 27202)
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
-- TOC entry 5029 (class 0 OID 0)
-- Dependencies: 249
-- Name: external_system_external_system_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.external_system_external_system_id_seq OWNED BY arsahub_dev.external_system.external_system_id;


--
-- TOC entry 250 (class 1259 OID 27203)
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
-- TOC entry 251 (class 1259 OID 27210)
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
-- TOC entry 252 (class 1259 OID 27214)
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
-- TOC entry 5030 (class 0 OID 0)
-- Dependencies: 252
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq OWNED BY arsahub_dev.rule_progress_streak.rule_progress_streak_id;


--
-- TOC entry 253 (class 1259 OID 27215)
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
-- TOC entry 254 (class 1259 OID 27219)
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
-- TOC entry 5031 (class 0 OID 0)
-- Dependencies: 254
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq OWNED BY arsahub_dev.rule_progress_times.rule_progress_single_id;


--
-- TOC entry 255 (class 1259 OID 27220)
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
-- TOC entry 5032 (class 0 OID 0)
-- Dependencies: 255
-- Name: rule_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.rule_rule_id_seq OWNED BY arsahub_dev.rule.rule_id;


--
-- TOC entry 256 (class 1259 OID 27221)
-- Name: trigger; Type: TABLE; Schema: arsahub_dev; Owner: postgres
--

CREATE TABLE arsahub_dev.trigger (
    trigger_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    activity_id bigint,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL,
    json_schema jsonb
);


ALTER TABLE arsahub_dev.trigger OWNER TO postgres;

--
-- TOC entry 257 (class 1259 OID 27228)
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
-- TOC entry 5033 (class 0 OID 0)
-- Dependencies: 257
-- Name: trigger_trigger_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.trigger_trigger_id_seq OWNED BY arsahub_dev.trigger.trigger_id;


--
-- TOC entry 258 (class 1259 OID 27229)
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
-- TOC entry 259 (class 1259 OID 27236)
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
-- TOC entry 5034 (class 0 OID 0)
-- Dependencies: 259
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq OWNED BY arsahub_dev.trigger_type.trigger_type_id;


--
-- TOC entry 260 (class 1259 OID 27237)
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
-- TOC entry 261 (class 1259 OID 27244)
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
-- TOC entry 262 (class 1259 OID 27249)
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
-- TOC entry 263 (class 1259 OID 27254)
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
-- TOC entry 5035 (class 0 OID 0)
-- Dependencies: 263
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq OWNED BY arsahub_dev.user_activity_achievement.user_activity_achievement_id;


--
-- TOC entry 264 (class 1259 OID 27255)
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
-- TOC entry 265 (class 1259 OID 27259)
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
-- TOC entry 5036 (class 0 OID 0)
-- Dependencies: 265
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq OWNED BY arsahub_dev.user_activity_point_history.user_activity_point_history_id;


--
-- TOC entry 266 (class 1259 OID 27260)
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
-- TOC entry 5037 (class 0 OID 0)
-- Dependencies: 266
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_activity_user_activity_id_seq OWNED BY arsahub_dev.user_activity.user_activity_id;


--
-- TOC entry 267 (class 1259 OID 27261)
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
-- TOC entry 5038 (class 0 OID 0)
-- Dependencies: 267
-- Name: user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: arsahub_dev; Owner: postgres
--

ALTER SEQUENCE arsahub_dev.user_user_id_seq OWNED BY arsahub_dev."user".user_id;


--
-- TOC entry 4748 (class 2604 OID 27262)
-- Name: achievement achievement_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.achievement ALTER COLUMN achievement_id SET DEFAULT nextval('arsahub_dev.achievement_achievement_id_seq'::regclass);


--
-- TOC entry 4751 (class 2604 OID 27263)
-- Name: action effect_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.action ALTER COLUMN effect_id SET DEFAULT nextval('arsahub_dev.effect_effect_id_seq'::regclass);


--
-- TOC entry 4754 (class 2604 OID 27264)
-- Name: activity activity_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.activity ALTER COLUMN activity_id SET DEFAULT nextval('arsahub_dev.activity_activity_id_seq'::regclass);


--
-- TOC entry 4757 (class 2604 OID 27265)
-- Name: external_system external_system_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.external_system ALTER COLUMN external_system_id SET DEFAULT nextval('arsahub_dev.external_system_external_system_id_seq'::regclass);


--
-- TOC entry 4760 (class 2604 OID 27266)
-- Name: rule rule_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule ALTER COLUMN rule_id SET DEFAULT nextval('arsahub_dev.rule_rule_id_seq'::regclass);


--
-- TOC entry 4763 (class 2604 OID 27267)
-- Name: rule_progress_streak rule_progress_streak_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak ALTER COLUMN rule_progress_streak_id SET DEFAULT nextval('arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq'::regclass);


--
-- TOC entry 4765 (class 2604 OID 27268)
-- Name: rule_progress_times rule_progress_single_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times ALTER COLUMN rule_progress_single_id SET DEFAULT nextval('arsahub_dev.rule_progress_times_rule_progress_single_id_seq'::regclass);


--
-- TOC entry 4768 (class 2604 OID 27269)
-- Name: trigger trigger_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger ALTER COLUMN trigger_id SET DEFAULT nextval('arsahub_dev.trigger_trigger_id_seq'::regclass);


--
-- TOC entry 4771 (class 2604 OID 27270)
-- Name: trigger_type trigger_type_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger_type ALTER COLUMN trigger_type_id SET DEFAULT nextval('arsahub_dev.trigger_type_trigger_type_id_seq'::regclass);


--
-- TOC entry 4774 (class 2604 OID 27271)
-- Name: user user_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev."user" ALTER COLUMN user_id SET DEFAULT nextval('arsahub_dev.user_user_id_seq'::regclass);


--
-- TOC entry 4777 (class 2604 OID 27272)
-- Name: user_activity user_activity_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity ALTER COLUMN user_activity_id SET DEFAULT nextval('arsahub_dev.user_activity_user_activity_id_seq'::regclass);


--
-- TOC entry 4780 (class 2604 OID 27273)
-- Name: user_activity_achievement user_activity_achievement_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement ALTER COLUMN user_activity_achievement_id SET DEFAULT nextval('arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq'::regclass);


--
-- TOC entry 4783 (class 2604 OID 27274)
-- Name: user_activity_point_history user_activity_point_history_id; Type: DEFAULT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history ALTER COLUMN user_activity_point_history_id SET DEFAULT nextval('arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq'::regclass);


--
-- TOC entry 4993 (class 0 OID 27171)
-- Dependencies: 242
-- Data for Name: achievement; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.achievement (achievement_id, title, description, created_at, updated_at, image_url, activity_id) FROM stdin;
1	Achievement 1	\N	2023-11-03 00:54:51.690334+07	2023-11-03 00:54:51.690334+07	\N	1
2	I Made This Achievement :)	\N	2023-11-03 18:35:58.757865+07	2023-11-03 18:35:58.757865+07	\N	1
\.


--
-- TOC entry 4995 (class 0 OID 27179)
-- Dependencies: 244
-- Data for Name: action; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.action (effect_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Add points	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	2023-10-31 20:54:49.958514+07	2023-10-31 20:54:49.958514+07	add_points
2	Unlock achievement	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}	2023-10-31 21:08:24.064419+07	2023-10-31 21:08:24.064419+07	unlock_achievement
\.


--
-- TOC entry 4996 (class 0 OID 27186)
-- Dependencies: 245
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
-- TOC entry 4999 (class 0 OID 27195)
-- Dependencies: 248
-- Data for Name: external_system; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.external_system (external_system_id, title, description, api_key, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5001 (class 0 OID 27203)
-- Dependencies: 250
-- Data for Name: rule; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.rule (rule_id, title, description, activity_id, created_at, updated_at, trigger_id, trigger_type_id, effect_id, trigger_type_params, effect_params, trigger_params) FROM stdin;
25	Give 1 point when points reached 1000	\N	1	2023-11-05 00:15:29.781097+07	2023-11-05 00:15:29.781097+07	8	\N	1	\N	{"value": "1"}	{"value": "1000"}
31	New rule	New rule	1	2023-11-07 14:55:51.25185+07	2023-11-07 14:55:51.25185+07	3	\N	1	\N	{"value": "32"}	\N
32	New rule	New rule	1	2023-11-07 16:30:39.868254+07	2023-11-07 16:30:39.868254+07	10	\N	2	\N	{"achievementId": "1"}	\N
33	New rule	New rule	1	2023-11-07 21:40:52.268958+07	2023-11-07 21:40:52.268958+07	3	\N	1	\N	{"value": "32"}	\N
1	Give points on joining activity	\N	1	2023-10-31 21:30:00.541204+07	2023-10-31 21:30:00.541204+07	1	\N	1	\N	{"value": 100}	\N
\.


--
-- TOC entry 5002 (class 0 OID 27210)
-- Dependencies: 251
-- Data for Name: rule_progress_streak; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.rule_progress_streak (rule_progress_streak_id, rule_id, user_activity_id, current_streak, last_interaction_at, created_at) FROM stdin;
\.


--
-- TOC entry 5004 (class 0 OID 27215)
-- Dependencies: 253
-- Data for Name: rule_progress_times; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.rule_progress_times (rule_progress_single_id, rule_id, user_activity_id, progress, created_at, updated_at, completed_at) FROM stdin;
67	25	3	1	2023-11-05 00:31:07.135253+07	2023-11-05 00:31:07.135253+07	2023-11-05 00:31:07.132252+07
\.


--
-- TOC entry 5007 (class 0 OID 27221)
-- Dependencies: 256
-- Data for Name: trigger; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.trigger (trigger_id, title, description, activity_id, created_at, updated_at, key, json_schema) FROM stdin;
3	Share activity	ddd	1	2023-11-01 23:19:49.489555+07	2023-11-01 23:19:49.489555+07	share_activity	\N
8	Points reached	\N	1	2023-11-04 23:14:46.747363+07	2023-11-04 23:14:46.747363+07	points_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "string"}}}
10	Don't know	ddd	1	2023-11-01 23:19:49.489555+07	2023-11-01 23:19:49.489555+07	ayo	\N
11	22	ddd	1	2023-11-01 23:19:49.489555+07	2023-11-01 23:19:49.489555+07	ayo2	\N
1	Join activity	\N	1	2023-10-31 21:12:50.354516+07	2023-10-31 21:12:50.354516+07	join_activity	\N
\.


--
-- TOC entry 5009 (class 0 OID 27229)
-- Dependencies: 258
-- Data for Name: trigger_type; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.trigger_type (trigger_type_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Times 	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["count"], "properties": {"count": {"type": "number"}}}	2023-11-02 00:32:54.776542+07	2023-11-02 00:32:54.776542+07	times
\.


--
-- TOC entry 5011 (class 0 OID 27237)
-- Dependencies: 260
-- Data for Name: user; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev."user" (user_id, username, name, external_user_id, external_system_id, created_at, updated_at) FROM stdin;
1	user1	User One	i_am_user1	\N	2023-10-31 22:01:20.604671+07	2023-10-31 22:01:20.604671+07
\.


--
-- TOC entry 5012 (class 0 OID 27244)
-- Dependencies: 261
-- Data for Name: user_activity; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity (user_activity_id, user_id, activity_id, points, created_at, updated_at) FROM stdin;
3	1	1	63705	2023-10-31 23:14:30.47859+07	2023-11-12 23:27:18.920915+07
\.


--
-- TOC entry 5013 (class 0 OID 27249)
-- Dependencies: 262
-- Data for Name: user_activity_achievement; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity_achievement (user_activity_achievement_id, achievement_id, user_activity_id, completed_at, created_at, updated_at) FROM stdin;
2	2	3	\N	2023-11-04 21:48:37.59253+07	2023-11-04 21:48:37.59253+07
3	1	3	\N	2023-11-12 22:43:14.786375+07	2023-11-12 22:43:14.786375+07
\.


--
-- TOC entry 5015 (class 0 OID 27255)
-- Dependencies: 264
-- Data for Name: user_activity_point_history; Type: TABLE DATA; Schema: arsahub_dev; Owner: postgres
--

COPY arsahub_dev.user_activity_point_history (user_activity_point_history_id, user_activity_id, activity_id, points, created_at, description) FROM stdin;
\.


--
-- TOC entry 5039 (class 0 OID 0)
-- Dependencies: 243
-- Name: achievement_achievement_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.achievement_achievement_id_seq', 2, true);


--
-- TOC entry 5040 (class 0 OID 0)
-- Dependencies: 268
-- Name: achievement_activity_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.achievement_activity_id_seq', 1, false);


--
-- TOC entry 5041 (class 0 OID 0)
-- Dependencies: 246
-- Name: activity_activity_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.activity_activity_id_seq', 11, true);


--
-- TOC entry 5042 (class 0 OID 0)
-- Dependencies: 247
-- Name: effect_effect_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.effect_effect_id_seq', 2, true);


--
-- TOC entry 5043 (class 0 OID 0)
-- Dependencies: 249
-- Name: external_system_external_system_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.external_system_external_system_id_seq', 1, false);


--
-- TOC entry 5044 (class 0 OID 0)
-- Dependencies: 252
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq', 1, false);


--
-- TOC entry 5045 (class 0 OID 0)
-- Dependencies: 254
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.rule_progress_times_rule_progress_single_id_seq', 67, true);


--
-- TOC entry 5046 (class 0 OID 0)
-- Dependencies: 255
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.rule_rule_id_seq', 34, true);


--
-- TOC entry 5047 (class 0 OID 0)
-- Dependencies: 257
-- Name: trigger_trigger_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.trigger_trigger_id_seq', 11, true);


--
-- TOC entry 5048 (class 0 OID 0)
-- Dependencies: 259
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.trigger_type_trigger_type_id_seq', 1, true);


--
-- TOC entry 5049 (class 0 OID 0)
-- Dependencies: 263
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq', 3, true);


--
-- TOC entry 5050 (class 0 OID 0)
-- Dependencies: 265
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq', 1, false);


--
-- TOC entry 5051 (class 0 OID 0)
-- Dependencies: 266
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_activity_user_activity_id_seq', 3, true);


--
-- TOC entry 5052 (class 0 OID 0)
-- Dependencies: 267
-- Name: user_user_id_seq; Type: SEQUENCE SET; Schema: arsahub_dev; Owner: postgres
--

SELECT pg_catalog.setval('arsahub_dev.user_user_id_seq', 1, true);


--
-- TOC entry 4788 (class 2606 OID 27407)
-- Name: action action_un; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.action
    ADD CONSTRAINT action_un UNIQUE (key);


--
-- TOC entry 4786 (class 2606 OID 27276)
-- Name: achievement idx_16391_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.achievement
    ADD CONSTRAINT idx_16391_primary PRIMARY KEY (achievement_id);


--
-- TOC entry 4827 (class 2606 OID 27278)
-- Name: user_activity_achievement idx_16400_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT idx_16400_primary PRIMARY KEY (user_activity_achievement_id);


--
-- TOC entry 4792 (class 2606 OID 27280)
-- Name: activity idx_16407_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.activity
    ADD CONSTRAINT idx_16407_primary PRIMARY KEY (activity_id);


--
-- TOC entry 4790 (class 2606 OID 27282)
-- Name: action idx_16416_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.action
    ADD CONSTRAINT idx_16416_primary PRIMARY KEY (effect_id);


--
-- TOC entry 4823 (class 2606 OID 27284)
-- Name: user_activity idx_16425_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT idx_16425_primary PRIMARY KEY (user_activity_id);


--
-- TOC entry 4830 (class 2606 OID 27286)
-- Name: user_activity_point_history idx_16432_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT idx_16432_primary PRIMARY KEY (user_activity_point_history_id);


--
-- TOC entry 4798 (class 2606 OID 27288)
-- Name: rule idx_16438_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT idx_16438_primary PRIMARY KEY (rule_id);


--
-- TOC entry 4802 (class 2606 OID 27290)
-- Name: rule_progress_streak idx_16456_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT idx_16456_primary PRIMARY KEY (rule_progress_streak_id);


--
-- TOC entry 4806 (class 2606 OID 27292)
-- Name: rule_progress_times idx_16462_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT idx_16462_primary PRIMARY KEY (rule_progress_single_id);


--
-- TOC entry 4811 (class 2606 OID 27294)
-- Name: trigger idx_16468_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT idx_16468_primary PRIMARY KEY (trigger_id);


--
-- TOC entry 4815 (class 2606 OID 27296)
-- Name: trigger_type idx_16483_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger_type
    ADD CONSTRAINT idx_16483_primary PRIMARY KEY (trigger_type_id);


--
-- TOC entry 4820 (class 2606 OID 27298)
-- Name: user idx_16492_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev."user"
    ADD CONSTRAINT idx_16492_primary PRIMARY KEY (user_id);


--
-- TOC entry 4794 (class 2606 OID 27300)
-- Name: external_system idx_16500_primary; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.external_system
    ADD CONSTRAINT idx_16500_primary PRIMARY KEY (external_system_id);


--
-- TOC entry 4817 (class 2606 OID 27409)
-- Name: trigger_type trigger_type_un; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger_type
    ADD CONSTRAINT trigger_type_un UNIQUE (key);


--
-- TOC entry 4813 (class 2606 OID 27405)
-- Name: trigger trigger_un; Type: CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT trigger_un UNIQUE (key);


--
-- TOC entry 4825 (class 1259 OID 27301)
-- Name: idx_16400_achievement_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16400_achievement_id ON arsahub_dev.user_activity_achievement USING btree (achievement_id);


--
-- TOC entry 4821 (class 1259 OID 27302)
-- Name: idx_16425_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16425_activity_id ON arsahub_dev.user_activity USING btree (activity_id);


--
-- TOC entry 4824 (class 1259 OID 27303)
-- Name: idx_16425_user_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16425_user_id ON arsahub_dev.user_activity USING btree (user_id);


--
-- TOC entry 4828 (class 1259 OID 27304)
-- Name: idx_16432_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16432_activity_id ON arsahub_dev.user_activity_point_history USING btree (activity_id);


--
-- TOC entry 4831 (class 1259 OID 27305)
-- Name: idx_16432_user_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16432_user_activity_id ON arsahub_dev.user_activity_point_history USING btree (user_activity_id);


--
-- TOC entry 4795 (class 1259 OID 27306)
-- Name: idx_16438_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_activity_id ON arsahub_dev.rule USING btree (activity_id);


--
-- TOC entry 4796 (class 1259 OID 27307)
-- Name: idx_16438_effect_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_effect_id ON arsahub_dev.rule USING btree (effect_id);


--
-- TOC entry 4799 (class 1259 OID 27308)
-- Name: idx_16438_trigger_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_trigger_id ON arsahub_dev.rule USING btree (trigger_id);


--
-- TOC entry 4800 (class 1259 OID 27309)
-- Name: idx_16438_trigger_type_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16438_trigger_type_id ON arsahub_dev.rule USING btree (trigger_type_id);


--
-- TOC entry 4803 (class 1259 OID 27310)
-- Name: idx_16456_rule_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16456_rule_id ON arsahub_dev.rule_progress_streak USING btree (rule_id);


--
-- TOC entry 4804 (class 1259 OID 27311)
-- Name: idx_16456_user_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16456_user_activity_id ON arsahub_dev.rule_progress_streak USING btree (user_activity_id);


--
-- TOC entry 4807 (class 1259 OID 27312)
-- Name: idx_16462_rule_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16462_rule_id ON arsahub_dev.rule_progress_times USING btree (rule_id);


--
-- TOC entry 4808 (class 1259 OID 27313)
-- Name: idx_16462_user_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16462_user_activity_id ON arsahub_dev.rule_progress_times USING btree (user_activity_id);


--
-- TOC entry 4809 (class 1259 OID 27314)
-- Name: idx_16468_activity_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE INDEX idx_16468_activity_id ON arsahub_dev.trigger USING btree (activity_id);


--
-- TOC entry 4818 (class 1259 OID 27315)
-- Name: idx_16492_external_user_id; Type: INDEX; Schema: arsahub_dev; Owner: postgres
--

CREATE UNIQUE INDEX idx_16492_external_user_id ON arsahub_dev."user" USING btree (external_user_id);


--
-- TOC entry 4832 (class 2606 OID 32778)
-- Name: achievement achievement_fk; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.achievement
    ADD CONSTRAINT achievement_fk FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- TOC entry 4833 (class 2606 OID 27316)
-- Name: activity activity_fk; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.activity
    ADD CONSTRAINT activity_fk FOREIGN KEY (external_system_id) REFERENCES arsahub_dev.external_system(external_system_id);


--
-- TOC entry 4834 (class 2606 OID 27321)
-- Name: rule rule_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_1 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- TOC entry 4835 (class 2606 OID 27326)
-- Name: rule rule_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_2 FOREIGN KEY (trigger_id) REFERENCES arsahub_dev.trigger(trigger_id);


--
-- TOC entry 4836 (class 2606 OID 27331)
-- Name: rule rule_ibfk_3; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_3 FOREIGN KEY (trigger_type_id) REFERENCES arsahub_dev.trigger_type(trigger_type_id);


--
-- TOC entry 4837 (class 2606 OID 27336)
-- Name: rule rule_ibfk_4; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_4 FOREIGN KEY (effect_id) REFERENCES arsahub_dev.action(effect_id);


--
-- TOC entry 4838 (class 2606 OID 27341)
-- Name: rule_progress_streak rule_progress_streak_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_1 FOREIGN KEY (rule_id) REFERENCES arsahub_dev.rule(rule_id);


--
-- TOC entry 4839 (class 2606 OID 27346)
-- Name: rule_progress_streak rule_progress_streak_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- TOC entry 4840 (class 2606 OID 27351)
-- Name: rule_progress_times rule_progress_times_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_1 FOREIGN KEY (rule_id) REFERENCES arsahub_dev.rule(rule_id);


--
-- TOC entry 4841 (class 2606 OID 27356)
-- Name: rule_progress_times rule_progress_times_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- TOC entry 4842 (class 2606 OID 27361)
-- Name: trigger trigger_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT trigger_ibfk_1 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- TOC entry 4846 (class 2606 OID 27366)
-- Name: user_activity_achievement user_activity_achievement_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_1 FOREIGN KEY (achievement_id) REFERENCES arsahub_dev.achievement(achievement_id);


--
-- TOC entry 4847 (class 2606 OID 27371)
-- Name: user_activity_achievement user_activity_achievement_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- TOC entry 4844 (class 2606 OID 27376)
-- Name: user_activity user_activity_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT user_activity_ibfk_1 FOREIGN KEY (user_id) REFERENCES arsahub_dev."user"(user_id);


--
-- TOC entry 4845 (class 2606 OID 27381)
-- Name: user_activity user_activity_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT user_activity_ibfk_2 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- TOC entry 4848 (class 2606 OID 27386)
-- Name: user_activity_point_history user_activity_point_history_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_1 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);


--
-- TOC entry 4849 (class 2606 OID 27391)
-- Name: user_activity_point_history user_activity_point_history_ibfk_2; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_2 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);


--
-- TOC entry 4843 (class 2606 OID 27396)
-- Name: user user_ibfk_1; Type: FK CONSTRAINT; Schema: arsahub_dev; Owner: postgres
--

ALTER TABLE ONLY arsahub_dev."user"
    ADD CONSTRAINT user_ibfk_1 FOREIGN KEY (external_system_id) REFERENCES arsahub_dev.external_system(external_system_id);


-- Completed on 2023-11-12 23:28:59

--
-- PostgreSQL database dump complete
--

