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

ALTER TABLE IF EXISTS ONLY public.app_user_progress DROP CONSTRAINT IF EXISTS user_activity_progress_unit_id_fkey;
ALTER TABLE IF EXISTS ONLY public.trigger_log DROP CONSTRAINT IF EXISTS trigger_log_trigger_trigger_id_fk;
ALTER TABLE IF EXISTS ONLY public.trigger_log DROP CONSTRAINT IF EXISTS trigger_log_app_user_app_user_id_fk;
ALTER TABLE IF EXISTS ONLY public.trigger_log DROP CONSTRAINT IF EXISTS trigger_log_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.trigger DROP CONSTRAINT IF EXISTS trigger_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_rule_activation_type_rule_type_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule_progress_times DROP CONSTRAINT IF EXISTS rule_progress_times_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.rule_progress_times DROP CONSTRAINT IF EXISTS rule_progress_times_app_user_app_user_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_ibfk_4;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.app_user_achievement DROP CONSTRAINT IF EXISTS fk_app_user_achievement_on_app_user;
ALTER TABLE IF EXISTS ONLY public.app_user_achievement DROP CONSTRAINT IF EXISTS fk_app_user_achievement_on_achievement;
ALTER TABLE IF EXISTS ONLY public.custom_unit DROP CONSTRAINT IF EXISTS custom_unit_custom_unit_type_custom_unit_type_id_fk;
ALTER TABLE IF EXISTS ONLY public.custom_unit DROP CONSTRAINT IF EXISTS custom_unit___fk;
ALTER TABLE IF EXISTS ONLY public.app DROP CONSTRAINT IF EXISTS app_user_user_id_fk;
ALTER TABLE IF EXISTS ONLY public.app_user_progress DROP CONSTRAINT IF EXISTS app_user_progress_app_user_app_user_id_fk;
ALTER TABLE IF EXISTS ONLY public.app_user DROP CONSTRAINT IF EXISTS app_user_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.achievement DROP CONSTRAINT IF EXISTS achievement_app_app_id_fk;
DROP INDEX IF EXISTS public.idx_16462_rule_id;
DROP INDEX IF EXISTS public.idx_16438_trigger_id;
DROP INDEX IF EXISTS public.idx_16438_action_id;
ALTER TABLE IF EXISTS ONLY public.app_user_progress DROP CONSTRAINT IF EXISTS user_activity_progress_pkey;
ALTER TABLE IF EXISTS ONLY public.custom_unit DROP CONSTRAINT IF EXISTS unit_pkey;
ALTER TABLE IF EXISTS ONLY public.trigger_log DROP CONSTRAINT IF EXISTS trigger_log_pk;
ALTER TABLE IF EXISTS ONLY public.rule_activation_type DROP CONSTRAINT IF EXISTS rule_type_pk;
ALTER TABLE IF EXISTS ONLY public.app_user_achievement DROP CONSTRAINT IF EXISTS pk_app_user_achievement;
ALTER TABLE IF EXISTS ONLY public.app DROP CONSTRAINT IF EXISTS idx_16500_primary;
ALTER TABLE IF EXISTS ONLY public."user" DROP CONSTRAINT IF EXISTS idx_16492_primary;
ALTER TABLE IF EXISTS ONLY public.trigger DROP CONSTRAINT IF EXISTS idx_16468_primary;
ALTER TABLE IF EXISTS ONLY public.rule_progress_times DROP CONSTRAINT IF EXISTS idx_16462_primary;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS idx_16438_primary;
ALTER TABLE IF EXISTS ONLY public.action DROP CONSTRAINT IF EXISTS idx_16416_primary;
ALTER TABLE IF EXISTS ONLY public.achievement DROP CONSTRAINT IF EXISTS idx_16391_primary;
ALTER TABLE IF EXISTS ONLY public.custom_unit_type DROP CONSTRAINT IF EXISTS custom_unit_type_pk;
ALTER TABLE IF EXISTS ONLY public.app_user DROP CONSTRAINT IF EXISTS app_user_pkey;
ALTER TABLE IF EXISTS ONLY public.action DROP CONSTRAINT IF EXISTS action_un;
ALTER TABLE IF EXISTS public.trigger_log ALTER COLUMN trigger_log_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.rule_activation_type ALTER COLUMN rule_activation_type_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.custom_unit_type ALTER COLUMN custom_unit_type_id DROP DEFAULT;
DROP TABLE IF EXISTS public."user";
DROP SEQUENCE IF EXISTS public.trigger_log_trigger_log_id_seq;
DROP TABLE IF EXISTS public.trigger_log;
DROP TABLE IF EXISTS public.trigger;
DROP SEQUENCE IF EXISTS public.rule_type_rule_type_id_seq;
DROP TABLE IF EXISTS public.rule_progress_times;
DROP TABLE IF EXISTS public.rule_activation_type;
DROP TABLE IF EXISTS public.rule;
DROP SEQUENCE IF EXISTS public.custom_unit_type_custom_unit_type_id_seq;
DROP TABLE IF EXISTS public.custom_unit_type;
DROP TABLE IF EXISTS public.custom_unit;
DROP TABLE IF EXISTS public.app_user_progress;
DROP TABLE IF EXISTS public.app_user_achievement;
DROP TABLE IF EXISTS public.app_user;
DROP TABLE IF EXISTS public.app;
DROP TABLE IF EXISTS public.action;
DROP SEQUENCE IF EXISTS public.achievement_activity_id_seq;
DROP TABLE IF EXISTS public.achievement;
DROP SCHEMA IF EXISTS public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: achievement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.achievement (
    achievement_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    image_url text,
    app_id bigint
);


ALTER TABLE public.achievement OWNER TO postgres;

--
-- Name: achievement_achievement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.achievement ALTER COLUMN achievement_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.achievement_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: achievement_activity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.achievement_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.achievement_activity_id_seq OWNER TO postgres;

--
-- Name: action; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.action (
    action_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    json_schema jsonb,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    key text NOT NULL
);


ALTER TABLE public.action OWNER TO postgres;

--
-- Name: action_action_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.action ALTER COLUMN action_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.action_action_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: app; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app (
    app_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    api_key text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    owner_id bigint NOT NULL
);


ALTER TABLE public.app OWNER TO postgres;

--
-- Name: app_app_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app ALTER COLUMN app_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.app_app_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    app_user_id bigint NOT NULL,
    unique_id text NOT NULL,
    display_name text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    app_id bigint NOT NULL,
    points integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- Name: app_user_achievement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_achievement (
    app_user_achievement_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    app_user_id bigint NOT NULL,
    completed_at timestamp without time zone
);


ALTER TABLE public.app_user_achievement OWNER TO postgres;

--
-- Name: app_user_achievement_app_user_achievement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user_achievement ALTER COLUMN app_user_achievement_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.app_user_achievement_app_user_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: app_user_app_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user ALTER COLUMN app_user_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.app_user_app_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: app_user_progress; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_progress (
    app_user_activity_progress_id bigint NOT NULL,
    unit_id integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    value_int integer,
    value_int_array integer[],
    app_user_id bigint
);


ALTER TABLE public.app_user_progress OWNER TO postgres;

--
-- Name: custom_unit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.custom_unit (
    unit_id bigint NOT NULL,
    name text NOT NULL,
    key text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    app_id bigint NOT NULL,
    custom_unit_type_id integer NOT NULL
);


ALTER TABLE public.custom_unit OWNER TO postgres;

--
-- Name: custom_unit_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.custom_unit_type (
    custom_unit_type_id bigint NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.custom_unit_type OWNER TO postgres;

--
-- Name: custom_unit_type_custom_unit_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.custom_unit_type_custom_unit_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.custom_unit_type_custom_unit_type_id_seq OWNER TO postgres;

--
-- Name: custom_unit_type_custom_unit_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.custom_unit_type_custom_unit_type_id_seq OWNED BY public.custom_unit_type.custom_unit_type_id;


--
-- Name: custom_unit_unit_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.custom_unit ALTER COLUMN unit_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.custom_unit_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: rule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rule (
    rule_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    trigger_id bigint NOT NULL,
    action_id bigint NOT NULL,
    action_params jsonb,
    trigger_params jsonb,
    rule_activation_type_id bigint NOT NULL,
    app_id bigint
);


ALTER TABLE public.rule OWNER TO postgres;

--
-- Name: rule_activation_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rule_activation_type (
    rule_activation_type_id bigint NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.rule_activation_type OWNER TO postgres;

--
-- Name: rule_progress_times; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rule_progress_times (
    rule_progress_times_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    progress integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    completed_at timestamp with time zone,
    app_user_id bigint
);


ALTER TABLE public.rule_progress_times OWNER TO postgres;

--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.rule_progress_times ALTER COLUMN rule_progress_times_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.rule_progress_times_rule_progress_single_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: rule_rule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.rule ALTER COLUMN rule_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.rule_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: rule_type_rule_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rule_type_rule_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rule_type_rule_type_id_seq OWNER TO postgres;

--
-- Name: rule_type_rule_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rule_type_rule_type_id_seq OWNED BY public.rule_activation_type.rule_activation_type_id;


--
-- Name: trigger; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trigger (
    trigger_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    key text NOT NULL,
    json_schema jsonb,
    app_id bigint NOT NULL
);


ALTER TABLE public.trigger OWNER TO postgres;

--
-- Name: trigger_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trigger_log (
    trigger_log_id bigint NOT NULL,
    trigger_id integer,
    request_body jsonb NOT NULL,
    app_id bigint NOT NULL,
    app_user_id bigint NOT NULL
);


ALTER TABLE public.trigger_log OWNER TO postgres;

--
-- Name: trigger_log_trigger_log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trigger_log_trigger_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.trigger_log_trigger_log_id_seq OWNER TO postgres;

--
-- Name: trigger_log_trigger_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_log_trigger_log_id_seq OWNED BY public.trigger_log.trigger_log_id;


--
-- Name: trigger_trigger_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.trigger ALTER COLUMN trigger_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.trigger_trigger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."user" (
    user_id bigint NOT NULL,
    username text NOT NULL,
    name text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    uuid uuid DEFAULT gen_random_uuid() NOT NULL
);


ALTER TABLE public."user" OWNER TO postgres;

--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user_progress ALTER COLUMN app_user_activity_progress_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_activity_progress_user_activity_progress_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public."user" ALTER COLUMN user_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: custom_unit_type custom_unit_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit_type ALTER COLUMN custom_unit_type_id SET DEFAULT nextval('public.custom_unit_type_custom_unit_type_id_seq'::regclass);


--
-- Name: rule_activation_type rule_activation_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_activation_type ALTER COLUMN rule_activation_type_id SET DEFAULT nextval('public.rule_type_rule_type_id_seq'::regclass);


--
-- Name: trigger_log trigger_log_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_log ALTER COLUMN trigger_log_id SET DEFAULT nextval('public.trigger_log_trigger_log_id_seq'::regclass);


--
-- Data for Name: achievement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.achievement (achievement_id, title, description, created_at, updated_at, image_url, app_id) FROM stdin;
11	Look at me	\N	2023-12-10 15:45:04.526743+07	2023-12-10 15:45:04.526743+07	\N	1
7	XXXXXXXXXXX	\N	2023-12-10 15:43:01.692746+07	2023-12-10 15:43:01.692746+07	\N	1
5	dwqdqwdqw	\N	2023-12-10 15:42:08.016198+07	2023-12-10 15:42:08.016198+07	\N	1
9	Look at me	\N	2023-12-10 15:44:58.165742+07	2023-12-10 15:44:58.165742+07	\N	1
10	Look at me	\N	2023-12-10 15:44:59.686747+07	2023-12-10 15:44:59.686747+07	\N	1
1	Look at me	You looked at me	2023-11-28 14:35:25.352576+07	2023-11-28 14:35:25.352576+07	https://cdn.7tv.app/emote/60abf171870d317bef23d399/4x.webp	1
3	ia mdethi s	\N	2023-12-10 15:41:36.662198+07	2023-12-10 15:41:36.662198+07	\N	1
2	I'm a Sharer	You shared an activity	2023-11-28 15:04:18.531351+07	2023-11-28 15:04:18.531351+07	https://cdn.7tv.app/emote/652272cc84f69a4c93b07a0c/4x.webp	1
4	12334123421	\N	2023-12-10 15:42:00.237196+07	2023-12-10 15:42:00.237196+07	\N	1
6	dqwdqwdqwdqw	\N	2023-12-10 15:42:53.635743+07	2023-12-10 15:42:53.635743+07	\N	1
8	Look at me	\N	2023-12-10 15:44:55.789744+07	2023-12-10 15:44:55.789744+07	\N	1
\.


--
-- Data for Name: action; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.action (action_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Add points	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	2023-10-31 20:54:49.958514+07	2023-10-31 20:54:49.958514+07	add_points
2	Unlock achievement	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}	2023-10-31 21:08:24.064419+07	2023-10-31 21:08:24.064419+07	unlock_achievement
\.


--
-- Data for Name: app; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app (app_id, title, description, api_key, created_at, updated_at, owner_id) FROM stdin;
18	4984894892	\N	15891ec0-1566-493e-b9de-6d7d66fc4ebd	2023-11-26 20:22:15.412635+07	2023-11-26 20:22:15.412635+07	1
22	WWWW	\N	8aae9b1b-bc1c-4f81-9083-5a3f6d278dcd	2023-11-26 20:40:49.071811+07	2023-11-26 20:40:49.071811+07	1
14	Not Activity Platform 3	\N	8bcc4a20-fc5e-4823-a06b-5ba4300f5083	2023-11-26 19:39:04.859496+07	2023-11-26 19:39:04.859496+07	1
2	Fitbit	\N	0838c6ed-86bd-4dc6-9ea4-a1f29da99ebf	2023-11-26 11:19:16.049852+07	2023-11-26 11:19:16.049852+07	1
7	232321	\N	95d246eb-6a74-4b37-b7c5-f9e61fb886ae	2023-11-26 11:41:40.100741+07	2023-11-26 11:41:40.100741+07	1
6	223232312	\N	0fbfbc02-81b0-4aad-ab55-37e9bac653fd	2023-11-26 11:40:35.67782+07	2023-11-26 11:40:35.67782+07	1
15	Not Activity Platform 33	\N	a686a540-40cd-45ba-86cb-081932f36b93	2023-11-26 19:39:08.06883+07	2023-11-26 19:39:08.06883+07	1
32	aaaaaaaaaaa	\N	5948ad9a-10e7-4c2e-a768-bb13e5797df5	2023-12-08 19:27:13.070775+07	2023-12-08 19:27:13.070775+07	1
3	323231	\N	3648bd41-bd75-46ea-80fb-4e61a0e426a0	2023-11-26 11:19:30.529472+07	2023-11-26 11:19:30.529472+07	1
19	No template	\N	f0e5f568-4caa-46c4-bec1-52fe54ed9464	2023-11-26 20:23:44.824807+07	2023-11-26 20:23:44.824807+07	1
21	With template 2	\N	4da4679c-82e9-4a04-adfe-a3f15031a91c	2023-11-26 20:25:47.688335+07	2023-11-26 20:25:47.688335+07	1
23	TEST	\N	17cbe6c8-fa81-4b65-ade5-3a93df9e284f	2023-11-27 00:05:19.099246+07	2023-11-27 00:05:19.099246+07	1
17	23Not Activity Platform 333	\N	d78224f4-8288-4304-8d6a-fa555156f85c	2023-11-26 19:40:09.565458+07	2023-11-26 19:40:09.565458+07	1
9	dsadas	\N	6a357ed4-6449-40e6-b660-818f3a2e38a2	2023-11-26 13:55:22.221258+07	2023-11-26 13:55:22.221258+07	1
13	Not Activity Platform 2	\N	8592d75a-9531-4705-87e2-85f0b802c73d	2023-11-26 19:38:54.521592+07	2023-11-26 19:38:54.521592+07	1
28	DWDWDQd	\N	$argon2id$v=19$m=16384,t=2,p=1$jdOZ+nJ51NOL9MBPpgz+/A$3hrrGKNvoQDXuoVEtuLENmQO2KFPZ668yOLTqPpXpXA	2023-12-04 15:40:03.253172+07	2023-12-04 15:40:03.253172+07	1
29	eweqw	\N	$argon2id$v=19$m=16384,t=2,p=1$S88KQD/QdtByxAp+DZU2eA$5bzPPgfXfP6Rfv8AEz1LcLh2ZXenRpzN+SF12gWocqo	2023-12-04 15:40:52.462921+07	2023-12-04 15:40:52.462921+07	1
30	eweqwdd	\N	$argon2id$v=19$m=16384,t=2,p=1$xGSDZnIrQFlNt+Cj4imIHg$E+uFGirFzCC3wVsUH5pNBXpjMmuMp6y2aK8v69UWAyk	2023-12-04 15:41:09.044687+07	2023-12-04 15:41:09.044687+07	1
31	actual app	\N	$argon2id$v=19$m=16384,t=2,p=1$UpazeoOidRkzPkR9OKGMLg$AjEtzvOPuEv3Z/wBpXHYMe49cGx8ZUtpBK9/AZ/lTls	2023-12-05 11:40:25.175532+07	2023-12-05 11:40:25.175532+07	1
24	TEST2	\N	9c8c8557-9b99-495d-a4c1-1f89fcf5f96f	2023-11-27 00:05:51.139683+07	2023-11-27 00:05:51.139683+07	1
26	321312	\N	$argon2id$v=19$m=16384,t=2,p=1$PvPfd1khemz2kwptS2WmPg$d9iGGFPYR3/hctACzKpZiWBReGssZGEhIchk3ZSTvHY	2023-12-04 15:39:28.031926+07	2023-12-04 15:39:28.031926+07	1
11	dwdd	\N	c129ede9-90fc-4ef4-9258-4e0fd81b89aa	2023-11-26 16:09:04.833521+07	2023-11-26 16:09:04.833521+07	1
10	dwdqwdqw	\N	8bfd12d7-6e2a-4f28-8c48-71fc5f714eb3	2023-11-26 14:03:20.650477+07	2023-11-26 14:03:20.650477+07	1
8	ewww	\N	f0b0ab94-ee93-4bd8-b307-d659692d4aa1	2023-11-26 13:55:00.972037+07	2023-11-26 13:55:00.972037+07	1
27	DWDWDQ	\N	$argon2id$v=19$m=16384,t=2,p=1$Ou+5CI7NCzQRBHO9cmfRew$WVYpUK/ODZjtNoOr0MCXyVb7kPMMbd3zO1z5BXsf3cE	2023-12-04 15:39:45.087596+07	2023-12-04 15:39:45.087596+07	1
20	With template	\N	770d36dc-b7de-4ce8-8db7-d650446b63c9	2023-11-26 20:23:57.036857+07	2023-11-26 20:23:57.036857+07	1
16	Not Activity Platform 333	\N	3b7d58df-f543-400c-b605-b384860aac88	2023-11-26 19:39:21.650771+07	2023-11-26 19:39:21.650771+07	1
12	Not Activity Platform	\N	cf796741-ce51-49bc-b93e-67f10ff362bb	2023-11-26 19:36:56.267097+07	2023-11-26 19:36:56.267097+07	1
1	app1	For testing	15cfada4-eadd-4087-9e85-1aaf92a8d476	2023-11-25 15:23:56.54536+07	2023-11-25 15:23:56.54536+07	1
\.


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user (app_user_id, unique_id, display_name, created_at, updated_at, app_id, points) FROM stdin;
2	user1	Hoo Man	2023-12-10 04:58:49.011581	2023-12-10 04:58:49.011581	1	0
4	user2	I'm literally me	2023-12-10 09:53:59.771423	2023-12-10 09:53:59.771423	1	0
5	ewqeqweqwe	qweqwewq	2023-12-10 16:43:02.752244	2023-12-10 16:43:02.752244	1	0
6	me made this whoaowhowtwad	NOT ME	2023-12-10 16:46:33.638715	2023-12-10 16:46:33.638715	1	0
7	new user who dis	3333333	2023-12-10 16:47:51.012843	2023-12-10 16:47:51.012843	1	0
8	ewewewewe	wewewewe	2023-12-10 18:24:28.734841	2023-12-10 18:24:28.734841	1	0
9	new user ja 	ok ja	2023-12-10 18:35:44.900512	2023-12-10 18:35:44.900512	1	0
10	im new hoo you	i dont know	2023-12-11 04:10:03.23164	2023-12-11 04:10:03.23164	1	0
11	newuserhoodis	NEWUSERHOODIS	2023-12-11 05:28:31.718607	2023-12-11 05:28:31.718607	1	0
12	dsdasd	EEEE	2023-12-11 05:53:48.292062	2023-12-11 05:53:48.292062	1	0
\.


--
-- Data for Name: app_user_achievement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_achievement (app_user_achievement_id, achievement_id, app_user_id, completed_at) FROM stdin;
\.


--
-- Data for Name: app_user_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_progress (app_user_activity_progress_id, unit_id, created_at, updated_at, value_int, value_int_array, app_user_id) FROM stdin;
3	3	2023-12-15 09:46:58.974791	2023-12-15 09:47:19.144739	0	{1,3}	2
4	4	2023-12-16 07:58:37.852386	2023-12-24 09:02:47.667293	140	\N	2
\.


--
-- Data for Name: custom_unit; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.custom_unit (unit_id, name, key, created_at, updated_at, app_id, custom_unit_type_id) FROM stdin;
2	Steps 55	steps55	2023-11-26 11:19:09.035188+07	2023-11-26 11:19:09.035188+07	1	1
1	<string>	<string>	2023-11-25 15:41:47.212526+07	2023-11-25 15:41:47.212526+07	1	1
3	Workshop	workshopId	2023-12-15 14:01:08.584398+07	2023-12-15 14:01:08.584398+07	1	2
4	Workshop Score	score	2023-12-16 14:31:11.48628+07	2023-12-16 14:31:11.48628+07	1	1
\.


--
-- Data for Name: custom_unit_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.custom_unit_type (custom_unit_type_id, name) FROM stdin;
1	Integer
2	Integer Set
\.


--
-- Data for Name: rule; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule (rule_id, title, description, created_at, updated_at, trigger_id, action_id, action_params, trigger_params, rule_activation_type_id, app_id) FROM stdin;
7	Workshop Score reached 50 then Give 1000 point	\N	2023-12-16 14:42:59.346971+07	2023-12-16 14:42:59.346971+07	43	1	{"value": "0"}	{"value": "50"}	2	1
10	When Workshop Score Reached 50, Give 100 points	\N	2023-12-16 15:22:20.461599+07	2023-12-16 15:22:20.461599+07	44	1	{"value": "100"}	{"value": "50"}	2	1
\.


--
-- Data for Name: rule_activation_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_activation_type (rule_activation_type_id, name) FROM stdin;
1	Single
2	Repeated
\.


--
-- Data for Name: rule_progress_times; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_progress_times (rule_progress_times_id, rule_id, progress, created_at, updated_at, completed_at, app_user_id) FROM stdin;
\.


--
-- Data for Name: trigger; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger (trigger_id, title, description, created_at, updated_at, key, json_schema, app_id) FROM stdin;
3	Points reached	Points reached	2023-11-25 16:15:26.439378+07	2023-11-25 16:15:26.439378+07	points_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	1
31	TRIGGER ME NOW	\N	2023-11-28 22:30:32.850449+07	2023-11-28 22:30:32.850449+07	trigger_me_now	\N	1
43	Workshop Completed	\N	2023-12-15 13:58:28.381994+07	2023-12-15 13:58:28.381994+07	workshop_completed	{"type": "object", "$schema": "http://json-schema.org/draft-07/schema#", "required": ["workshopId"], "properties": {"workshopId": {"type": "integer"}}}	1
44	Workshop Score reached	\N	2023-12-16 14:30:13.733637+07	2023-12-16 14:30:13.733637+07	score_reached	{"type": "object", "$schema": "http://json-schema.org/draft-07/schema#", "required": ["value"], "properties": {"value": {"type": "integer"}}}	1
61	title	\N	2023-12-24 19:13:30.05858+07	2023-12-24 19:13:30.05858+07	title	{"type": "object", "$schema": "http://json-schema.org/draft-07/schema#", "required": ["workshopId", "score", "timeSpent"], "properties": {"score": {"type": "integer"}, "timeSpent": {"type": "string", "format": "duration"}, "workshopId": {"type": "integer"}}}	1
62	ed3232	ddwdw	2023-12-24 21:26:33.386542+07	2023-12-24 21:26:33.386542+07	ed3232	\N	1
\.


--
-- Data for Name: trigger_log; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger_log (trigger_log_id, trigger_id, request_body, app_id, app_user_id) FROM stdin;
1	43	{"score": "10", "timeSpent": "P1M", "workshopId": "1"}	1	2
2	43	{"score": "10", "timeSpent": "P1M", "workshopId": "1"}	1	2
3	43	{"score": "10", "timeSpent": "P1M", "workshopId": "1"}	1	2
4	43	{"score": "10", "timeSpent": "P1M", "workshopId": "1"}	1	2
5	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
6	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
7	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshdopId": 1}, "userId": "user1"}	1	2
8	43	{"key": "workshop_completed", "userId": "user1", "padrams": {"score": 10, "timeSpent": "P1M", "workshopId": 1}}	1	2
9	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
11	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
10	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
12	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
13	43	{"key": "workshop_completed", "params": {"score": 10, "timeSpent": "P1M", "workshopId": 1}, "userId": "user1"}	1	2
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."user" (user_id, username, name, created_at, updated_at, uuid) FROM stdin;
1	user1	User 1	2023-11-25 15:23:56.74695+07	2023-11-25 15:23:56.74695+07	4c1250f9-e442-45d6-ae67-2b41b0e3d4b9
2	user2	User 2	2023-12-10 00:45:47.967905+07	2023-12-10 00:45:47.967905+07	61d9399b-2664-4673-bd47-29159fd316d0
\.


--
-- Name: achievement_achievement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.achievement_achievement_id_seq', 11, true);


--
-- Name: achievement_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.achievement_activity_id_seq', 1, false);


--
-- Name: action_action_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.action_action_id_seq', 1, false);


--
-- Name: app_app_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_app_id_seq', 32, true);


--
-- Name: app_user_achievement_app_user_achievement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_user_achievement_app_user_achievement_id_seq', 1, false);


--
-- Name: app_user_app_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_user_app_user_id_seq', 12, true);


--
-- Name: custom_unit_type_custom_unit_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.custom_unit_type_custom_unit_type_id_seq', 2, true);


--
-- Name: custom_unit_unit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.custom_unit_unit_id_seq', 4, true);


--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_progress_times_rule_progress_single_id_seq', 1, true);


--
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_rule_id_seq', 10, true);


--
-- Name: rule_type_rule_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_type_rule_type_id_seq', 2, true);


--
-- Name: trigger_log_trigger_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_log_trigger_log_id_seq', 13, true);


--
-- Name: trigger_trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_trigger_id_seq', 62, true);


--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_progress_user_activity_progress_id_seq', 4, true);


--
-- Name: user_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_user_id_seq', 2, true);


--
-- Name: action action_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.action
    ADD CONSTRAINT action_un UNIQUE (key);


--
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (app_user_id);


--
-- Name: custom_unit_type custom_unit_type_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit_type
    ADD CONSTRAINT custom_unit_type_pk PRIMARY KEY (custom_unit_type_id);


--
-- Name: achievement idx_16391_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.achievement
    ADD CONSTRAINT idx_16391_primary PRIMARY KEY (achievement_id);


--
-- Name: action idx_16416_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.action
    ADD CONSTRAINT idx_16416_primary PRIMARY KEY (action_id);


--
-- Name: rule idx_16438_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT idx_16438_primary PRIMARY KEY (rule_id);


--
-- Name: rule_progress_times idx_16462_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT idx_16462_primary PRIMARY KEY (rule_progress_times_id);


--
-- Name: trigger idx_16468_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT idx_16468_primary PRIMARY KEY (trigger_id);


--
-- Name: user idx_16492_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT idx_16492_primary PRIMARY KEY (user_id);


--
-- Name: app idx_16500_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app
    ADD CONSTRAINT idx_16500_primary PRIMARY KEY (app_id);


--
-- Name: app_user_achievement pk_app_user_achievement; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_achievement
    ADD CONSTRAINT pk_app_user_achievement PRIMARY KEY (app_user_achievement_id);


--
-- Name: rule_activation_type rule_type_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_activation_type
    ADD CONSTRAINT rule_type_pk PRIMARY KEY (rule_activation_type_id);


--
-- Name: trigger_log trigger_log_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_log
    ADD CONSTRAINT trigger_log_pk PRIMARY KEY (trigger_log_id);


--
-- Name: custom_unit unit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_id);


--
-- Name: app_user_progress user_activity_progress_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_progress
    ADD CONSTRAINT user_activity_progress_pkey PRIMARY KEY (app_user_activity_progress_id);


--
-- Name: idx_16438_action_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_action_id ON public.rule USING btree (action_id);


--
-- Name: idx_16438_trigger_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_trigger_id ON public.rule USING btree (trigger_id);


--
-- Name: idx_16462_rule_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16462_rule_id ON public.rule_progress_times USING btree (rule_id);


--
-- Name: achievement achievement_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.achievement
    ADD CONSTRAINT achievement_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: app_user app_user_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: app_user_progress app_user_progress_app_user_app_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_progress
    ADD CONSTRAINT app_user_progress_app_user_app_user_id_fk FOREIGN KEY (app_user_id) REFERENCES public.app_user(app_user_id);


--
-- Name: app app_user_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app
    ADD CONSTRAINT app_user_user_id_fk FOREIGN KEY (owner_id) REFERENCES public."user"(user_id);


--
-- Name: custom_unit custom_unit___fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit
    ADD CONSTRAINT custom_unit___fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: custom_unit custom_unit_custom_unit_type_custom_unit_type_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit
    ADD CONSTRAINT custom_unit_custom_unit_type_custom_unit_type_id_fk FOREIGN KEY (custom_unit_type_id) REFERENCES public.custom_unit_type(custom_unit_type_id);


--
-- Name: app_user_achievement fk_app_user_achievement_on_achievement; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_achievement
    ADD CONSTRAINT fk_app_user_achievement_on_achievement FOREIGN KEY (achievement_id) REFERENCES public.achievement(achievement_id);


--
-- Name: app_user_achievement fk_app_user_achievement_on_app_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_achievement
    ADD CONSTRAINT fk_app_user_achievement_on_app_user FOREIGN KEY (app_user_id) REFERENCES public.app_user(app_user_id);


--
-- Name: rule rule_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: rule rule_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_2 FOREIGN KEY (trigger_id) REFERENCES public.trigger(trigger_id);


--
-- Name: rule rule_ibfk_4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_4 FOREIGN KEY (action_id) REFERENCES public.action(action_id);


--
-- Name: rule_progress_times rule_progress_times_app_user_app_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT rule_progress_times_app_user_app_user_id_fk FOREIGN KEY (app_user_id) REFERENCES public.app_user(app_user_id);


--
-- Name: rule_progress_times rule_progress_times_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_1 FOREIGN KEY (rule_id) REFERENCES public.rule(rule_id);


--
-- Name: rule rule_rule_activation_type_rule_type_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_rule_activation_type_rule_type_id_fk FOREIGN KEY (rule_activation_type_id) REFERENCES public.rule_activation_type(rule_activation_type_id);


--
-- Name: trigger trigger_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT trigger_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: trigger_log trigger_log_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_log
    ADD CONSTRAINT trigger_log_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: trigger_log trigger_log_app_user_app_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_log
    ADD CONSTRAINT trigger_log_app_user_app_user_id_fk FOREIGN KEY (app_user_id) REFERENCES public.app_user(app_user_id);


--
-- Name: trigger_log trigger_log_trigger_trigger_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_log
    ADD CONSTRAINT trigger_log_trigger_trigger_id_fk FOREIGN KEY (trigger_id) REFERENCES public.trigger(trigger_id);


--
-- Name: app_user_progress user_activity_progress_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_progress
    ADD CONSTRAINT user_activity_progress_unit_id_fkey FOREIGN KEY (unit_id) REFERENCES public.custom_unit(unit_id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;


--
-- PostgreSQL database dump complete
--

