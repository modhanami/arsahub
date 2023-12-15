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

ALTER TABLE IF EXISTS ONLY public.app_user_activity_progress DROP CONSTRAINT IF EXISTS user_activity_progress_user_activity_id_fkey;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_progress DROP CONSTRAINT IF EXISTS user_activity_progress_unit_id_fkey;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_point_history DROP CONSTRAINT IF EXISTS user_activity_point_history_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_point_history DROP CONSTRAINT IF EXISTS user_activity_point_history_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_achievement DROP CONSTRAINT IF EXISTS user_activity_achievement_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_achievement DROP CONSTRAINT IF EXISTS user_activity_achievement_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.trigger_template DROP CONSTRAINT IF EXISTS trigger_template_app_template_app_template_id_fk;
ALTER TABLE IF EXISTS ONLY public.trigger_custom_unit DROP CONSTRAINT IF EXISTS trigger_custom_unit_trigger_trigger_id_fk;
ALTER TABLE IF EXISTS ONLY public.trigger_custom_unit DROP CONSTRAINT IF EXISTS trigger_custom_unit_custom_unit_unit_id_fk;
ALTER TABLE IF EXISTS ONLY public.trigger DROP CONSTRAINT IF EXISTS trigger_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule_template DROP CONSTRAINT IF EXISTS rule_template_trigger_trigger_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule_template DROP CONSTRAINT IF EXISTS rule_template_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule_template DROP CONSTRAINT IF EXISTS rule_template_action_action_id_fk;
ALTER TABLE IF EXISTS ONLY public.rule_progress_times DROP CONSTRAINT IF EXISTS rule_progress_times_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.rule_progress_times DROP CONSTRAINT IF EXISTS rule_progress_times_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.rule_progress_streak DROP CONSTRAINT IF EXISTS rule_progress_streak_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.rule_progress_streak DROP CONSTRAINT IF EXISTS rule_progress_streak_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_ibfk_4;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_ibfk_3;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS rule_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.custom_unit DROP CONSTRAINT IF EXISTS custom_unit_custom_unit_type_custom_unit_type_id_fk;
ALTER TABLE IF EXISTS ONLY public.custom_unit DROP CONSTRAINT IF EXISTS custom_unit___fk;
ALTER TABLE IF EXISTS ONLY public.app DROP CONSTRAINT IF EXISTS app_user_user_id_fk;
ALTER TABLE IF EXISTS ONLY public.app_user DROP CONSTRAINT IF EXISTS app_user_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.app_user_activity DROP CONSTRAINT IF EXISTS app_user_activity_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.app_user_activity DROP CONSTRAINT IF EXISTS app_user_activity_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.app_template DROP CONSTRAINT IF EXISTS app_template_app_app_id_fk;
ALTER TABLE IF EXISTS ONLY public.activity DROP CONSTRAINT IF EXISTS activity_fk;
ALTER TABLE IF EXISTS ONLY public.achievement DROP CONSTRAINT IF EXISTS achievement_fk;
DROP INDEX IF EXISTS public.idx_16462_user_activity_id;
DROP INDEX IF EXISTS public.idx_16462_rule_id;
DROP INDEX IF EXISTS public.idx_16456_user_activity_id;
DROP INDEX IF EXISTS public.idx_16456_rule_id;
DROP INDEX IF EXISTS public.idx_16438_trigger_type_id;
DROP INDEX IF EXISTS public.idx_16438_trigger_id;
DROP INDEX IF EXISTS public.idx_16438_activity_id;
DROP INDEX IF EXISTS public.idx_16438_action_id;
DROP INDEX IF EXISTS public.idx_16432_user_activity_id;
DROP INDEX IF EXISTS public.idx_16432_activity_id;
DROP INDEX IF EXISTS public.idx_16425_user_id;
DROP INDEX IF EXISTS public.idx_16425_activity_id;
DROP INDEX IF EXISTS public.idx_16400_achievement_id;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_progress DROP CONSTRAINT IF EXISTS user_activity_progress_pkey;
ALTER TABLE IF EXISTS ONLY public.custom_unit DROP CONSTRAINT IF EXISTS unit_pkey;
ALTER TABLE IF EXISTS ONLY public.trigger_type DROP CONSTRAINT IF EXISTS trigger_type_un;
ALTER TABLE IF EXISTS ONLY public.trigger_template DROP CONSTRAINT IF EXISTS trigger_template_pk;
ALTER TABLE IF EXISTS ONLY public.trigger_custom_unit DROP CONSTRAINT IF EXISTS trigger_custom_unit_pk;
ALTER TABLE IF EXISTS ONLY public.rule_template DROP CONSTRAINT IF EXISTS rule_template_pk;
ALTER TABLE IF EXISTS ONLY public.app DROP CONSTRAINT IF EXISTS idx_16500_primary;
ALTER TABLE IF EXISTS ONLY public."user" DROP CONSTRAINT IF EXISTS idx_16492_primary;
ALTER TABLE IF EXISTS ONLY public.trigger_type DROP CONSTRAINT IF EXISTS idx_16483_primary;
ALTER TABLE IF EXISTS ONLY public.trigger DROP CONSTRAINT IF EXISTS idx_16468_primary;
ALTER TABLE IF EXISTS ONLY public.rule_progress_times DROP CONSTRAINT IF EXISTS idx_16462_primary;
ALTER TABLE IF EXISTS ONLY public.rule_progress_streak DROP CONSTRAINT IF EXISTS idx_16456_primary;
ALTER TABLE IF EXISTS ONLY public.rule DROP CONSTRAINT IF EXISTS idx_16438_primary;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_point_history DROP CONSTRAINT IF EXISTS idx_16432_primary;
ALTER TABLE IF EXISTS ONLY public.app_user_activity DROP CONSTRAINT IF EXISTS idx_16425_primary;
ALTER TABLE IF EXISTS ONLY public.action DROP CONSTRAINT IF EXISTS idx_16416_primary;
ALTER TABLE IF EXISTS ONLY public.activity DROP CONSTRAINT IF EXISTS idx_16407_primary;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_achievement DROP CONSTRAINT IF EXISTS idx_16400_primary;
ALTER TABLE IF EXISTS ONLY public.achievement DROP CONSTRAINT IF EXISTS idx_16391_primary;
ALTER TABLE IF EXISTS ONLY public.custom_unit_type DROP CONSTRAINT IF EXISTS custom_unit_type_pk;
ALTER TABLE IF EXISTS ONLY public.app_user DROP CONSTRAINT IF EXISTS app_user_pkey;
ALTER TABLE IF EXISTS ONLY public.app_template DROP CONSTRAINT IF EXISTS app_template_pk;
ALTER TABLE IF EXISTS ONLY public.action DROP CONSTRAINT IF EXISTS action_un;
ALTER TABLE IF EXISTS public.trigger_template ALTER COLUMN trigger_template_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.trigger_custom_unit ALTER COLUMN custom_unit_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.trigger_custom_unit ALTER COLUMN trigger_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.trigger_custom_unit ALTER COLUMN trigger_custom_unit_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.rule_template ALTER COLUMN rule_template_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.custom_unit_type ALTER COLUMN custom_unit_type_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.app_template ALTER COLUMN app_template_id DROP DEFAULT;
DROP TABLE IF EXISTS public."user";
DROP TABLE IF EXISTS public.trigger_type;
DROP SEQUENCE IF EXISTS public.trigger_template_app_tempalte_id_seq;
DROP TABLE IF EXISTS public.trigger_template;
DROP SEQUENCE IF EXISTS public.trigger_custom_unit_trigger_id_seq;
DROP SEQUENCE IF EXISTS public.trigger_custom_unit_trigger_custom_unit_seq;
DROP SEQUENCE IF EXISTS public.trigger_custom_unit_custom_unit_id_seq;
DROP TABLE IF EXISTS public.trigger_custom_unit;
DROP TABLE IF EXISTS public.trigger;
DROP SEQUENCE IF EXISTS public.rule_template_rule_template_id_seq;
DROP TABLE IF EXISTS public.rule_template;
DROP TABLE IF EXISTS public.rule_progress_times;
DROP TABLE IF EXISTS public.rule_progress_streak;
DROP TABLE IF EXISTS public.rule;
DROP SEQUENCE IF EXISTS public.custom_unit_type_custom_unit_type_id_seq;
DROP TABLE IF EXISTS public.custom_unit_type;
DROP TABLE IF EXISTS public.custom_unit;
DROP TABLE IF EXISTS public.app_user_activity_progress;
DROP TABLE IF EXISTS public.app_user_activity_point_history;
DROP TABLE IF EXISTS public.app_user_activity_achievement;
DROP TABLE IF EXISTS public.app_user_activity;
DROP TABLE IF EXISTS public.app_user;
DROP SEQUENCE IF EXISTS public.app_template_app_template_id_seq;
DROP TABLE IF EXISTS public.app_template;
DROP TABLE IF EXISTS public.app;
DROP TABLE IF EXISTS public.activity;
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
    activity_id bigint NOT NULL
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
-- Name: activity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.activity (
    activity_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    app_id bigint
);


ALTER TABLE public.activity OWNER TO postgres;

--
-- Name: activity_activity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.activity ALTER COLUMN activity_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.activity_activity_id_seq
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
    created_by bigint NOT NULL
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
-- Name: app_template; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_template (
    name text NOT NULL,
    description text,
    app_id bigint NOT NULL,
    app_template_id bigint NOT NULL
);


ALTER TABLE public.app_template OWNER TO postgres;

--
-- Name: app_template_app_template_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_template_app_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_template_app_template_id_seq OWNER TO postgres;

--
-- Name: app_template_app_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_template_app_template_id_seq OWNED BY public.app_template.app_template_id;


--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    app_user_id bigint NOT NULL,
    unique_id text NOT NULL,
    display_name text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    app_id bigint NOT NULL
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- Name: app_user_activity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_activity (
    app_user_activity_id bigint NOT NULL,
    app_user_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    points integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.app_user_activity OWNER TO postgres;

--
-- Name: app_user_activity_achievement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_activity_achievement (
    app_user_activity_achievement_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    app_user_activity_id bigint NOT NULL,
    completed_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.app_user_activity_achievement OWNER TO postgres;

--
-- Name: app_user_activity_point_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_activity_point_history (
    app_user_activity_point_history_id bigint NOT NULL,
    app_user_activity_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    points integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    description text
);


ALTER TABLE public.app_user_activity_point_history OWNER TO postgres;

--
-- Name: app_user_activity_progress; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_activity_progress (
    app_user_activity_progress_id bigint NOT NULL,
    app_user_activity_id integer NOT NULL,
    unit_id integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    value_int integer,
    value_int_array integer[]
);


ALTER TABLE public.app_user_activity_progress OWNER TO postgres;

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
    activity_id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    trigger_id bigint NOT NULL,
    trigger_type_id bigint,
    action_id bigint NOT NULL,
    trigger_type_params jsonb,
    action_params jsonb,
    trigger_params jsonb
);


ALTER TABLE public.rule OWNER TO postgres;

--
-- Name: rule_progress_streak; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rule_progress_streak (
    rule_progress_streak_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    current_streak integer NOT NULL,
    last_interaction_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.rule_progress_streak OWNER TO postgres;

--
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.rule_progress_streak ALTER COLUMN rule_progress_streak_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.rule_progress_streak_rule_progress_streak_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: rule_progress_times; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rule_progress_times (
    rule_progress_single_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    progress integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    completed_at timestamp with time zone
);


ALTER TABLE public.rule_progress_times OWNER TO postgres;

--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.rule_progress_times ALTER COLUMN rule_progress_single_id ADD GENERATED BY DEFAULT AS IDENTITY (
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
-- Name: rule_template; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rule_template (
    rule_template_id bigint NOT NULL,
    name text NOT NULL,
    description text,
    trigger_template_id bigint NOT NULL,
    action_id bigint NOT NULL,
    app_id integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    action_params jsonb,
    trigger_params jsonb
);


ALTER TABLE public.rule_template OWNER TO postgres;

--
-- Name: rule_template_rule_template_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rule_template_rule_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rule_template_rule_template_id_seq OWNER TO postgres;

--
-- Name: rule_template_rule_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rule_template_rule_template_id_seq OWNED BY public.rule_template.rule_template_id;


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
-- Name: trigger_custom_unit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trigger_custom_unit (
    trigger_custom_unit_id bigint NOT NULL,
    trigger_id bigint NOT NULL,
    custom_unit_id bigint NOT NULL
);


ALTER TABLE public.trigger_custom_unit OWNER TO postgres;

--
-- Name: trigger_custom_unit_custom_unit_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trigger_custom_unit_custom_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.trigger_custom_unit_custom_unit_id_seq OWNER TO postgres;

--
-- Name: trigger_custom_unit_custom_unit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_custom_unit_custom_unit_id_seq OWNED BY public.trigger_custom_unit.custom_unit_id;


--
-- Name: trigger_custom_unit_trigger_custom_unit_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trigger_custom_unit_trigger_custom_unit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.trigger_custom_unit_trigger_custom_unit_seq OWNER TO postgres;

--
-- Name: trigger_custom_unit_trigger_custom_unit_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_custom_unit_trigger_custom_unit_seq OWNED BY public.trigger_custom_unit.trigger_custom_unit_id;


--
-- Name: trigger_custom_unit_trigger_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trigger_custom_unit_trigger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.trigger_custom_unit_trigger_id_seq OWNER TO postgres;

--
-- Name: trigger_custom_unit_trigger_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_custom_unit_trigger_id_seq OWNED BY public.trigger_custom_unit.trigger_id;


--
-- Name: trigger_template; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trigger_template (
    title text NOT NULL,
    description text,
    key text NOT NULL,
    json_schema jsonb,
    trigger_template_id bigint NOT NULL,
    app_template_id bigint NOT NULL
);


ALTER TABLE public.trigger_template OWNER TO postgres;

--
-- Name: trigger_template_app_tempalte_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trigger_template_app_tempalte_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.trigger_template_app_tempalte_id_seq OWNER TO postgres;

--
-- Name: trigger_template_app_tempalte_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_template_app_tempalte_id_seq OWNED BY public.trigger_template.trigger_template_id;


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
-- Name: trigger_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trigger_type (
    trigger_type_id bigint NOT NULL,
    title text NOT NULL,
    description text,
    json_schema jsonb,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    key text NOT NULL
);


ALTER TABLE public.trigger_type OWNER TO postgres;

--
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.trigger_type ALTER COLUMN trigger_type_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.trigger_type_trigger_type_id_seq
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
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user_activity_achievement ALTER COLUMN app_user_activity_achievement_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_activity_achievement_user_activity_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user_activity_point_history ALTER COLUMN app_user_activity_point_history_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_activity_point_history_user_activity_point_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user_activity_progress ALTER COLUMN app_user_activity_progress_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_activity_progress_user_activity_progress_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.app_user_activity ALTER COLUMN app_user_activity_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_activity_user_activity_id_seq
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
-- Name: app_template app_template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_template ALTER COLUMN app_template_id SET DEFAULT nextval('public.app_template_app_template_id_seq'::regclass);


--
-- Name: custom_unit_type custom_unit_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit_type ALTER COLUMN custom_unit_type_id SET DEFAULT nextval('public.custom_unit_type_custom_unit_type_id_seq'::regclass);


--
-- Name: rule_template rule_template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template ALTER COLUMN rule_template_id SET DEFAULT nextval('public.rule_template_rule_template_id_seq'::regclass);


--
-- Name: trigger_custom_unit trigger_custom_unit_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_custom_unit ALTER COLUMN trigger_custom_unit_id SET DEFAULT nextval('public.trigger_custom_unit_trigger_custom_unit_seq'::regclass);


--
-- Name: trigger_custom_unit trigger_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_custom_unit ALTER COLUMN trigger_id SET DEFAULT nextval('public.trigger_custom_unit_trigger_id_seq'::regclass);


--
-- Name: trigger_custom_unit custom_unit_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_custom_unit ALTER COLUMN custom_unit_id SET DEFAULT nextval('public.trigger_custom_unit_custom_unit_id_seq'::regclass);


--
-- Name: trigger_template trigger_template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_template ALTER COLUMN trigger_template_id SET DEFAULT nextval('public.trigger_template_app_tempalte_id_seq'::regclass);


--
-- Data for Name: achievement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.achievement (achievement_id, title, description, created_at, updated_at, image_url, activity_id) FROM stdin;
1	Look at me	You looked at me	2023-11-28 14:35:25.352576+07	2023-11-28 14:35:25.352576+07	https://cdn.7tv.app/emote/60abf171870d317bef23d399/4x.webp	1
2	I'm a Sharer	You shared an activity	2023-11-28 15:04:18.531351+07	2023-11-28 15:04:18.531351+07	https://cdn.7tv.app/emote/652272cc84f69a4c93b07a0c/4x.webp	1
3	ia mdethi s	\N	2023-12-10 15:41:36.662198+07	2023-12-10 15:41:36.662198+07	\N	1
4	12334123421	\N	2023-12-10 15:42:00.237196+07	2023-12-10 15:42:00.237196+07	\N	1
5	dwqdqwdqw	\N	2023-12-10 15:42:08.016198+07	2023-12-10 15:42:08.016198+07	\N	1
6	dqwdqwdqwdqw	\N	2023-12-10 15:42:53.635743+07	2023-12-10 15:42:53.635743+07	\N	1
7	XXXXXXXXXXX	\N	2023-12-10 15:43:01.692746+07	2023-12-10 15:43:01.692746+07	\N	1
8	Look at me	\N	2023-12-10 15:44:55.789744+07	2023-12-10 15:44:55.789744+07	\N	1
9	Look at me	\N	2023-12-10 15:44:58.165742+07	2023-12-10 15:44:58.165742+07	\N	1
10	Look at me	\N	2023-12-10 15:44:59.686747+07	2023-12-10 15:44:59.686747+07	\N	1
11	Look at me	\N	2023-12-10 15:45:04.526743+07	2023-12-10 15:45:04.526743+07	\N	1
\.


--
-- Data for Name: action; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.action (action_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Add points	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	2023-10-31 20:54:49.958514+07	2023-10-31 20:54:49.958514+07	add_points
2	Unlock achievement	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}	2023-10-31 21:08:24.064419+07	2023-10-31 21:08:24.064419+07	unlock_achievement
\.


--
-- Data for Name: activity; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.activity (activity_id, title, description, created_at, updated_at, app_id) FROM stdin;
1	Activity 1	This is for app1	2023-11-25 15:23:56.959286+07	2023-11-25 15:23:56.959286+07	1
2	<string>	<string>	2023-11-25 15:41:46.820529+07	2023-11-25 15:41:46.820529+07	\N
3	XXDWQDQW	dWDQWDQW	2023-11-26 21:06:02.623301+07	2023-11-26 21:06:02.623301+07	\N
4	ewww's Activity	i made this	2023-11-27 10:34:52.431233+07	2023-11-27 10:34:52.431233+07	8
5	3213	\N	2023-11-27 10:40:20.489605+07	2023-11-27 10:40:20.489605+07	8
6	ewewew	nothung	2023-11-27 11:02:00.908178+07	2023-11-27 11:02:00.908178+07	8
7	-wqddqwd	23232	2023-11-27 11:02:37.211179+07	2023-11-27 11:02:37.211179+07	9
8	WWewqeqw	321	2023-12-01 20:46:52.124156+07	2023-12-01 20:46:52.124156+07	1
9	3232	3213	2023-12-09 10:57:57.73145+07	2023-12-09 10:57:57.73145+07	15
10	dddddddddddddd	\N	2023-12-10 23:16:14.329398+07	2023-12-10 23:16:14.329398+07	1
11	dWDWDW	\N	2023-12-10 23:17:17.075789+07	2023-12-10 23:17:17.075789+07	1
12	eeeweqw	\N	2023-12-10 23:17:23.717789+07	2023-12-10 23:17:23.717789+07	1
13	dwdwwdqwdqw	dqwdqwdqw	2023-12-11 01:35:30.599985+07	2023-12-11 01:35:30.599985+07	1
14	dddddddddddd	wwww	2023-12-11 11:56:02.774924+07	2023-12-11 11:56:02.774924+07	1
15	dsddwdwd	dwdwdw	2023-12-11 12:28:17.767469+07	2023-12-11 12:28:17.767469+07	1
16	eeee	\N	2023-12-11 12:53:40.790066+07	2023-12-11 12:53:40.790066+07	1
17	eeeeee	\N	2023-12-11 21:49:37.793047+07	2023-12-11 21:49:37.793047+07	1
18	2222222222	\N	2023-12-11 21:49:41.927868+07	2023-12-11 21:49:41.927868+07	1
19	dogThe quick brown fox jumps over the	lazy d	2019-12-12 06:25:14.954+07	1992-01-31 00:47:31.039+07	7
20	lazy dogThe quick brown f	\N	2002-05-01 07:09:33.191+07	2019-11-26 12:10:10.701+07	32
21	brown fox jumps 	brown fox ju	1988-07-15 07:47:24.694+07	2007-02-19 20:20:50.518+07	19
22	fox jumps over the lazy dogThe	brown fox jumps over the lazy d	1983-07-06 07:18:13.119+07	2007-07-03 10:49:35.568+07	26
23	fox j	lazy dogThe qui	2004-11-09 18:38:07.808+07	1990-05-29 15:23:56.317+07	16
24	quick brown fox jumps over the 	fox jumps over	1991-03-24 17:49:48.029+07	2016-07-30 22:00:22.154+07	12
25	lazy dogThe quick brown fox jumps	dogThe quick b	2001-08-14 21:03:14.481+07	2000-07-05 11:50:33.339+07	7
26	lazy dogThe quick brown fox jumps over the	d	1984-08-18 05:07:41.286+07	1987-12-28 18:42:37.927+07	19
27	lazy dogThe quick brown fox jumps ov	dogT	2020-09-11 14:11:49.608+07	1987-12-08 01:58:42.005+07	9
28	over the	over the lazy	2007-04-06 08:04:18.259+07	2007-06-01 06:27:21.776+07	26
29	brown fox ju	fox jumps over the lazy dog	1998-08-29 15:01:08.914+07	1984-02-14 02:58:44.649+07	6
30	the lazy dogThe quick brown fox	over the lazy dogThe quic	2013-10-22 14:01:59.121+07	2006-12-29 20:10:01.159+07	12
31	quick brown	over the lazy dogThe quick brown	2015-02-23 11:56:12.431+07	1996-05-08 15:43:52.77+07	16
32	brown f	\N	1990-10-26 02:47:50.456+07	2019-06-05 02:46:19.897+07	24
33	fox jump	fox jumps over the	1996-07-25 20:58:50.516+07	2001-11-13 18:42:46.792+07	15
34	the lazy dogThe quick brown fox jumps	\N	2004-09-12 20:33:10.368+07	1987-03-13 15:39:25.465+07	16
35	dogThe quick b	\N	1987-03-29 02:42:07.02+07	1991-03-29 17:58:39.993+07	31
36	over the lazy dogThe quick b	\N	1993-10-11 12:52:27.432+07	2013-10-04 12:17:21.736+07	29
37	quick brown fox jum	the lazy do	1990-08-14 12:57:34.309+07	1981-10-15 11:55:18.667+07	22
38	the lazy dogThe qu	The quick brown fox jumps over the lazy 	2013-12-11 02:04:44.122+07	2006-08-30 16:39:59.338+07	16
39	lazy dogThe quick brown fox	brown fo	2018-06-27 18:05:45.181+07	1995-02-18 23:54:13.77+07	8
40	the lazy dogThe quick brown fox jumps	dogThe quick brow	2008-03-13 06:46:51.905+07	2015-10-15 04:02:00.618+07	16
41	the 	quick brown fox jumps over the lazy	2012-06-08 11:07:49.237+07	1991-11-06 09:40:44.515+07	13
42	lazy d	dogTh	2009-09-15 05:50:23.325+07	1990-03-18 14:52:20.561+07	11
43	the lazy dogThe quick brown fox jumps ove	the lazy dogThe quick brown	2008-03-26 23:23:27.373+07	2008-10-08 09:50:20.211+07	2
44	brown fox jump	over the lazy dogThe qu	1986-08-08 17:39:27.614+07	2000-06-26 23:49:53.629+07	28
45	quick brown fox jumps over the lazy dogThe	over the lazy dogThe quick brown	2012-11-29 15:00:02.412+07	2010-10-16 21:15:18.043+07	31
46	jumps over the lazy dogThe quick brow	lazy dogThe quick brow	2004-04-09 07:34:45.165+07	2015-10-25 20:57:34.953+07	10
47	dogThe quick brown fox jumps over the lazy	\N	1998-08-03 13:19:01.194+07	1987-06-01 19:46:42.357+07	8
48	jumps over the lazy dogThe quick brown fo	ogThe quick brown fox jumps o	1985-07-07 03:50:15.193+07	1996-03-25 04:06:15.746+07	18
49	brown fox jumps over the lazy do	fox jumps o	1986-03-21 09:59:24.891+07	2000-06-07 06:38:59.857+07	31
50	quick brown fox jumps over the lazy 	\N	2019-12-28 01:51:14.183+07	2013-01-18 18:20:34.851+07	27
51	brown fox jumps over the lazy dogThe quick	ogThe quick brown	1985-04-10 05:31:45.026+07	2006-08-13 05:10:33.387+07	24
52	jumps over the lazy dogThe quick brown	the lazy dogThe qu	1989-08-22 09:37:13.577+07	2023-11-17 12:09:00.029+07	10
53	fox jumps o	\N	1986-03-07 07:41:22.752+07	1989-12-03 20:59:50.055+07	18
54	quick brown fox j	\N	2011-10-13 12:40:01.889+07	1991-10-11 21:40:49.175+07	31
55	the la	\N	1989-05-25 17:43:06.51+07	2018-09-15 14:18:13.959+07	19
56	fox jumps ov	\N	2017-02-09 08:22:00.616+07	1987-11-09 00:20:42.427+07	23
57	dogThe quick brown	lazy dogThe quick brown fox jumps	1993-09-10 13:37:29.83+07	1980-04-08 08:40:56.23+07	1
58	over the lazy dogThe quick brown fox ju	\N	1996-07-24 21:41:45.501+07	2000-07-01 04:27:13.438+07	19
59	brown	\N	1986-05-31 00:04:57.544+07	2006-01-07 02:37:25.217+07	14
60	fox j	dogThe quick brown fox	1998-06-09 15:27:21.752+07	2021-02-20 01:58:30.331+07	26
61	fox jumps ove	dogThe quick brow	2022-08-25 10:11:46.781+07	1999-07-05 19:59:13.13+07	26
62	fox jumps over the lazy 	dogThe quick brown fox j	1986-10-13 23:05:33.159+07	2016-01-24 14:52:00.21+07	11
63	over the lazy dogT	\N	1983-01-05 23:49:35.506+07	2005-10-30 18:45:17.601+07	18
64	dogThe quick b	brown f	2003-04-22 17:31:20.84+07	2000-02-14 12:28:17.591+07	31
65	lazy dogThe quick brown fox jumps over	over the lazy dogThe quick br	1996-12-27 18:29:54.235+07	1983-07-30 02:35:07.399+07	14
66	dogThe quick brown fox j	quick brown fox jumps over the l	2013-11-25 10:10:09.264+07	1992-09-23 12:05:41.138+07	32
67	dogThe quick brown fox jumps ov	over the lazy dogThe quic	2001-04-28 18:50:46.713+07	2007-09-27 07:28:21.214+07	27
68	brown fox ju	\N	2001-10-23 22:42:33.576+07	1980-07-24 18:18:41.387+07	1
69	Thawagicstewnion dingolielsed ingimapsieng pant awetens neipant thans smedarrob thaung?	\N	1986-02-05 14:22:00.993+07	2021-03-01 12:31:39.297+07	15
70	Wrounselfos, bellungemproe, claffats earle mite.	fox ju	1996-07-06 20:30:19.795+07	1987-01-01 02:41:18.606+07	2
71	Rindas ungrib relf hemp gagsepsece.	brown fox jumps ov	2012-10-28 20:19:04.635+07	2016-10-30 03:12:51.733+07	24
72	Vemboostiomoy wrelker mepply lup oddemmerm empraicer.	lazy dogThe quick bro	2013-03-18 14:29:10.115+07	1981-03-11 06:29:13.831+07	14
73	Engle hickied rielly py mont lerstem.	the lazy dogThe quick brown fox jumps	2002-06-24 15:09:02.919+07	2012-03-06 04:01:29.356+07	13
74	Half arbeetob fal, baipa red; slelsagurg.	\N	1991-01-19 22:48:04.585+07	1996-06-08 07:37:21.308+07	32
75	Eber gack viexes braff wrimebist elloungond...	jumps over the lazy dogThe quick brown fo	2019-03-26 01:47:45.861+07	1983-10-24 18:35:35.447+07	1
76	Eala gosorch thedd grambleenk hewnin engist?	the laz	2014-08-11 04:44:04.241+07	1980-08-30 00:47:02.014+07	29
77	Ned kistigs troolmerpation drengref littark yog droe med erbafist gorgalses!	brown fo	1990-07-12 08:15:24.997+07	1990-08-17 13:59:05.779+07	30
78	Enniory gladdition, bangaf otcheal ezzomied brance snerriess?	\N	1985-05-07 12:23:16.159+07	1996-12-16 00:40:44.636+07	21
79	Bloffint sly dralmay parebbecstarb vied orralgo.	brown fox jumps over the lazy dog	2012-04-22 02:44:01.935+07	1980-12-28 11:20:33.546+07	9
80	Ubawiolye gnosas erpoags mite gebsadoorly book uborn...	ogThe qui	1995-03-10 18:01:00.974+07	2000-12-09 05:26:27.771+07	32
81	Lenserkees yezaught pou sobbickibed issarc mickiedly; ornur...	brown fox jumps ove	1999-12-13 05:29:36.566+07	2003-03-20 14:05:46.399+07	28
82	Roownip osceerm elut sebbest smebsemer spals mes reigate...	over the lazy d	1985-10-29 22:31:15.51+07	1981-03-21 06:00:19.702+07	23
83	Ortnaly rientiedly vipplelleff lalpitch iorferm ceffuss.	lazy dogThe	2008-06-11 00:51:00.498+07	1992-03-27 02:26:06.84+07	6
84	Nompots thau hatampition, ebelp flemblubant, breb kniolsism.	quick brown fox jumps	1982-02-19 02:42:21.741+07	1996-09-14 08:15:31.77+07	18
85	Hech aru hek husemiedly aimblipadly eadsaugs, rebsew; rans ny...	d	1987-08-26 14:05:57.084+07	2014-11-22 21:14:09.498+07	3
86	Loffut trais etsounglien modd crengeppurn, tax thaissurt gleegsunsation angep.		1997-03-05 14:57:16.205+07	2017-02-11 15:47:33.484+07	31
87	Wrinsuts flimpink ornollism rist gratsoots ineddib snem der!	\N	1987-09-13 23:41:03.532+07	1984-10-06 21:43:36.091+07	9
88	Bated ebsat ally teartnib thip, quay, ingedd urpelsiedly gudisee sli.	gThe quick brown fox jumps over the	1996-03-28 22:09:31.37+07	2012-03-20 04:47:30.702+07	29
89	Garrelch olmedderrub rengoogs angreen freafab jend...	dogThe quick	2015-04-05 00:42:16.319+07	1982-04-02 14:31:27.702+07	9
90	Eingench boondoongross magutch insy knosurt urpimu roased porrinnietch.	dogTh	2001-03-26 15:20:54.783+07	2018-01-18 07:33:16.484+07	17
91	Elmeidd eldas lubite, roo; agetsist.	the lazy d	2020-05-26 09:22:55.297+07	1988-01-06 09:23:38.196+07	31
92	Erbio egsont knisif gexa darpourceal stell thons ibos.	\N	1991-05-12 10:43:51.737+07	1989-07-13 01:46:28.368+07	12
93	Ewomp nay asis treimneep ernerrough nerberbate?	brown fox jumps over the lazy dogThe qui	2021-11-25 10:19:39.763+07	2012-07-03 22:35:33.489+07	9
94	Spi nafippem bee gawied mauser trus leeker.	jumps over the lazy 	2006-07-23 23:27:56.123+07	2011-10-13 18:19:36.502+07	23
95	Nuni raingiolalown hiss cleelmown noupsate.	\N	2010-11-05 13:23:37.176+07	1993-04-08 08:32:56.763+07	11
96	Deetoac ellyal gew, mewingis upely.	fox jumps over the lazy d	1985-12-26 01:51:43.936+07	2010-07-02 12:34:13.315+07	31
97	Ateizut; errafalpoa huzzelm, croy, ibsec taded lingeds; tals.	\N	2001-10-05 05:22:50.694+07	1982-01-23 02:44:57.155+07	8
98	Osest nemed autsedd thow; enkell?	jumps ov	2021-03-31 10:18:03.741+07	2013-12-25 04:23:36.557+07	10
99	Jorneffay tuzeimner thred, fly; baidism ty yos plertnosion?	\N	2022-02-21 17:10:28.218+07	1999-07-07 08:52:19.762+07	30
100	Golk, ossist faidd elism fiff...	\N	1983-04-20 15:14:50.763+07	2014-09-09 22:40:50.997+07	32
101	Noombist rolfabiginsy siom stax trasoarkap ded sourrus...	dogThe quick brown fox jumps ove	2016-08-29 16:40:44.714+07	1988-08-07 06:34:34.803+07	11
102	Link; smaurpingite dizatsion slindate snieroa.	\N	1995-09-16 03:44:17.144+07	1981-03-24 19:29:29.66+07	26
103	Maib bester, quoll, yogsas flay!	lazy dogThe quic	2018-01-17 21:59:14.208+07	2018-06-01 01:52:16.233+07	2
104	Azzoar drads crensiec knannounsay sescengy!	brown fox jumps over the lazy dogThe quick	1988-03-07 21:43:45.247+07	2021-02-28 12:40:39.043+07	12
105	Kneelinsy taxintaser gnoser cleedsiep spigsely.	lazy dogThe	1995-07-03 00:47:57.228+07	2007-04-18 15:09:47.163+07	12
106	Ausision utaing dridsant, hoser bonim rolmoy ange ambinses...	fox j	2001-07-27 06:25:08.92+07	1986-06-10 20:53:54.923+07	11
107	Dertnaly sotu coostis gy esain purt nesiddes stale vambats, koof.	dogThe quick brown fox jumps over the	2012-12-13 01:36:40.027+07	1989-05-30 23:08:37.995+07	14
108	Lee clait damblorongoe bradsern derfaint thimarn reengell sacesey.	the l	1986-11-29 03:14:26.821+07	1999-10-19 10:52:47.394+07	28
109	Thaught joomnenut snes teelk slation aldol sanch smataup brickation onuck...	\N	1999-11-14 03:18:18.266+07	1995-06-05 16:18:30.755+07	2
110	Grut pres seelmifest gneppof arcion opses?	the lazy dogThe	2009-12-20 05:38:28.02+07	1980-01-01 19:45:30.443+07	29
111	Deimni feidsation clalmec flary algunch mod, tap, neippleis chi sloownant...	jumps over t	1993-03-06 21:31:49.746+07	2000-08-11 15:00:35.871+07	12
112	Ixec gebance fofion hotchos flaw utecer?	\N	1997-04-11 06:13:10.156+07	1980-03-03 21:17:13.168+07	12
113	Helm loxay insangokep brunglossab clur?	\N	2016-08-29 05:39:35.998+07	2017-06-17 11:34:25.312+07	17
114	Genas; blo creterribb, stewont baiddosition ife amogs?	\N	2005-10-08 11:58:17.55+07	1994-12-21 22:56:47.249+07	27
115	Unsation awnelf nicstanant smakoa pan egalpay cramaicky prossabsas.	b	2018-11-10 16:42:55.958+07	2018-03-17 06:41:28.413+07	20
116	Slanoorlow hesand, diess brant grac dauz witsa adip inkup.	fox jumps over the lazy	1981-03-17 02:10:00.7+07	2012-05-05 12:25:34.252+07	12
117	Utter gesoungisa turfollawed effe fleldalmald toe, tront saip...	dogThe quick brown fox jum	2008-02-03 21:31:48.628+07	2022-12-04 14:01:03.894+07	3
118	Snerigs bication rits cerf smer.	\N	1992-02-02 03:31:38.767+07	2001-07-11 22:06:40.388+07	3
119	pursy curb woo cleft worser purer assume monster habits	brown fox jumps 	2006-09-14 13:23:24.658+07	1982-03-07 18:52:16.358+07	10
120	strew dangerous conjectures breeding minds amiss artless	lazy dogThe quick brown fox jumps over t	2016-03-31 13:42:09.945+07	1981-12-14 08:50:48.102+07	21
121	purer assume monster habits likewise frock	jumps over t	2000-04-08 22:09:34.082+07	1993-12-02 23:13:20.46+07	16
122	bold speechless orb below hush	over the lazy dogThe quick brow	1990-07-30 21:55:51.225+07	1994-10-11 09:57:04.202+07	11
123	sprinkle cool glares conjoin preaching stones	gThe quick brown fox jum	2020-04-24 20:30:53.405+07	2023-08-01 19:40:14.004+07	18
124	propose ending enactures destroy revels	over the lazy dogThe quick bro	1997-08-17 23:03:43.847+07	1997-08-16 18:05:48.616+07	23
125	request never swear sword already beneath swear	lazy dogThe quick brow	1996-03-28 00:07:43.236+07	2023-09-08 20:49:31.342+07	22
126	painting passages time qualifies spark wick	\N	2007-12-08 06:57:50.866+07	2012-07-06 08:18:21.339+07	16
127	exact several sorts bugs goblins supervise bated	\N	1992-09-08 12:36:30.459+07	2022-03-27 07:31:13.464+07	17
128	courteous action waves removed set pin fee immortal		2021-11-26 00:25:38.164+07	2011-03-09 14:29:25.642+07	2
129	engaged help angels bow stubborn strings	over the lazy dogThe	2011-11-11 15:06:23.863+07	1989-03-25 18:01:18.2+07	10
130	wore beaver frowningly countenance anger pale red constantly	lazy dogThe quick brown fox jum	1997-04-02 19:35:04.906+07	1987-12-24 11:43:35.715+07	3
131	signet model folded subscribed impression safely changeling	ogThe quick bro	1982-02-18 16:35:00.718+07	2015-04-12 01:15:45.798+07	22
132	gather occasion glean unknown afflicts remedy living	lazy dogThe quick	2007-10-17 20:08:10.839+07	1983-04-18 21:19:10.889+07	14
133	extinct even promise making somewhat	\N	1999-01-06 23:19:57.412+07	1999-04-14 01:36:39.294+07	3
134	my dread lord leave favour france from whence willingly	brown fox jumps over the laz	2004-10-04 23:13:58.794+07	2006-06-19 16:17:10.707+07	21
135	thumb eloquent stops utterance harmony	\N	2011-12-12 13:52:47.582+07	1989-05-01 19:56:36.789+07	10
136	devil pleasing yea perhaps melancholy potent	jumps over the lazy dogThe quick brown f	2023-05-07 00:24:25.342+07	1988-11-28 14:02:32.967+07	26
137	fust unused bestial oblivion craven scruple precisely event	over the lazy do	1987-04-12 18:58:08.126+07	1988-09-06 01:53:53.661+07	27
138	walks o er dew yon eastward hill break advice	dogThe quick brow	1996-08-23 00:56:35.118+07	1998-12-31 15:53:48.944+07	28
139	messenger letters claudio high mighty naked	dogThe quick brown fox	1984-11-27 11:15:31.558+07	2012-02-24 00:24:50.325+07	11
140	rims hung kissed gibes gambols flashes	quick brown fox jumps 	2010-01-02 13:31:36.226+07	2019-04-24 17:14:14.595+07	6
141	friend harping suffered extremity words	qu	1998-08-15 20:11:47.139+07	2022-12-10 01:11:05.862+07	12
142	seems nay alone inky cloak mother nor customary	\N	1995-06-24 01:32:53.253+07	1992-10-14 05:59:39.398+07	26
143	might believe without sensible true avouch	quick brown fox jumps ove	2010-02-06 22:28:01.621+07	2009-07-07 11:14:50.088+07	3
144	virgin crants strewments bringing profane requiem parted	quick brow	1995-10-30 00:03:33.099+07	1999-09-09 17:49:05.937+07	21
145	tunes incapable distress indued element garments pull	ogThe quick brown fox jumps over the lazy	1988-12-23 01:17:36.197+07	2023-06-13 13:31:36.234+07	14
146	jester e abhorred gorge rims hung	quick brown fox jumps ove	1994-10-24 06:46:58.881+07	2004-03-02 20:03:50.232+07	2
147	outlives tenants built unyoke mass distance cudgel mend	brown fox j	2018-01-24 11:42:34.343+07	1980-03-01 07:19:41.788+07	1
148	cast brazen cannon foreign mart for implements war	\N	1984-08-21 05:16:02.899+07	1989-11-23 13:55:19.56+07	1
149	affrighted sewing closet doublet unbraced hat stockings ungarter	dogThe quick brown	2000-12-24 11:41:49.189+07	2013-08-18 09:59:38.386+07	6
150	burthen withdraw nobler suffer slings arrows outrageous	jumps over the la	1981-06-30 17:33:12.9+07	2005-07-19 04:20:21.918+07	26
151	dozen sixteen insert peasant slave monstrous fiction could force	dogThe quick b	1990-08-04 22:42:24.71+07	1992-11-15 09:02:06.095+07	9
152	count space dreams shadow truly	dogThe quick brown fox jumps over	2002-12-17 14:59:49.171+07	2001-07-11 07:43:17.253+07	21
153	proof eterne remorse bleeding fortune	the lazy	1996-02-18 20:50:28.094+07	1994-11-04 09:35:47.376+07	30
154	gaged return to inheritance been	\N	1981-04-07 21:02:23.811+07	1997-06-29 19:21:39.968+07	16
155	sum envy unworthiest siege riband careless graveness normandy	the lazy dogThe quick bro	2018-01-01 04:41:52.864+07	2006-05-23 23:26:33.768+07	2
156	sides nation tarre controversy argument unless poet player cuffs	\N	2005-07-06 02:45:02.863+07	1993-04-05 16:55:54.651+07	21
157	promontory canopy brave erhanging firmament	brown fox jumps over the la	2014-06-19 13:02:42.347+07	1992-06-29 01:28:24.705+07	13
158	francisco at his post enter to him bernardo	\N	1988-12-14 11:37:09.875+07	2018-11-26 04:15:05.469+07	14
159	lights stricken deer hart ungalled runs	\N	2017-04-27 03:57:08.384+07	2015-06-07 04:16:42.316+07	9
160	blastments imminent wary lies youth rebels near effect	fox jumps over the lazy dogThe quick	2000-01-13 05:52:06.35+07	2000-12-08 18:34:28.144+07	27
161	think crescent does grow thews bulk temple	jumps over the la	1982-05-07 06:33:23.475+07	2009-01-15 23:37:53.9+07	12
162	expense finding encompassment drift nearer demands touch	\N	1989-09-08 18:06:28.026+07	1999-09-08 18:36:23.739+07	14
163	move hide hate utter rosencrantz guildenstern	quick brown fox 	1991-02-27 03:19:21.974+07	1996-04-07 20:14:17.77+07	27
164	pickers stealers surely bar door	the lazy d	1988-11-02 19:08:22.121+07	2003-03-18 11:28:16.475+07	18
165	hatchment rite formal ostentation cry	dogThe quick brown fox	1996-07-09 03:35:22.203+07	2005-07-05 00:47:12.023+07	16
166	passing am lot wot row chanson	fox jumps over the lazy dogThe quic	1989-01-20 10:27:55.39+07	2023-10-04 16:01:31.678+07	2
167	school wittenberg retrograde desire remain here cheer comfort chiefest	over the laz	1982-04-05 07:46:36.649+07	1998-12-01 22:26:12.787+07	20
168	steward master rosemary pansies document fitted fennel columbines rue	the lazy dogThe quick br	2009-10-12 13:03:16.391+07	1992-05-18 08:47:00.383+07	24
169	Quensaunker.	Quensaunker ry?	1991-06-15 05:41:21.871+07	2018-01-24 04:34:24.174+07	2
170	Ese vo coat peppren!	Ese vo coat peppren blengids arfes.	2022-04-25 21:44:51.883+07	1985-02-11 12:06:49.965+07	31
171	Treansux satiempreempant thameloom izzay!	Treansux satiempreempant thameloom izzay ingins sallong criossed.	1990-05-07 16:51:40.534+07	2009-10-25 08:06:22.88+07	24
172	Andorg ribb florn teerb bo arpalm, alded anty gessin.	Andorg ribb...	1987-03-27 00:45:02.343+07	2019-03-15 16:36:02.287+07	23
173	Fodess trid sesigs seigy, trollied sesy igsosey, hiosion.	Fodess trid sesigs seigy, trollied sesy.	2002-11-05 18:27:03.911+07	2013-08-01 09:19:34.552+07	28
174	Breiteppraled, ruscippler.	Breiteppraled, ruscippler, lis yarket, thraimprei flessed cling.	2019-12-08 22:27:26.835+07	1996-12-23 00:01:49.969+07	11
175	Triffaffy wac clerred...	Triffaffy wac clerred...	1998-02-08 07:38:29.842+07	2012-01-21 12:16:03.05+07	26
176	Zixeati rucion toolyabunk!	Zixeati rucion toolyabunk noxont driemprasoa nombion etsi.	1984-09-08 18:21:57.603+07	1983-07-07 08:33:09.529+07	2
177	Dres, flals spiged thiogoorm fossed erbuc fets croff?	Dres, flals spiged thiogoorm fossed erbuc fets croff hut yorstiom bral nicstaint; ermurt.	2005-09-16 14:50:05.896+07	2022-03-24 14:25:36.032+07	11
178	Crampaupprough flidsibb use ninseal!	Crampaupprough flidsibb use ninseal!	2001-01-18 19:11:04.556+07	2009-12-22 06:46:27.246+07	9
179	Sision?	Sision do aupeff apsif.	2020-02-14 21:51:25.616+07	1989-05-19 08:19:17.306+07	2
180	Prombly mef, sler sals blibeang nittas slindufed?	Prombly mef, sler sals blibeang nittas?	2009-04-25 06:38:41.354+07	2010-04-13 23:16:36.089+07	10
181	Aizidser; cosorlurg; elyossy iollyel; slourkay plaup erber.	Aizidser; cosorlurg; elyossy iollyel.	2000-05-01 15:52:36.935+07	2005-02-05 14:54:42.487+07	26
182	Lortnainsobay jiernoown, slorlorb.	Lortnainsobay jiernoown, slorlorb iornoty eanter, engreideldy bely ornont...	1983-03-04 01:39:05.735+07	1992-08-08 10:11:05.964+07	12
183	Fetal moolfef gits thrindely lallimals!	Fetal moolfef gits thrindely...	2005-07-01 02:26:54.293+07	1985-07-09 23:58:05.6+07	24
184	Slin globby dagy hintos seewned...	Slin globby dagy hintos seewned...	1980-06-11 09:58:54.746+07	1984-03-08 01:10:24.872+07	30
185	Ollarell angru craitoged; floppabbite!	Ollarell angru craitoged; floppabbite ossangredd acky lallac opsout sy craidseengisy; weemproz.	2023-03-06 16:56:57.402+07	2005-05-29 11:35:07.21+07	10
186	Sertnidsaust abalkeg.	Sertnidsaust abalkeg, baught; nerk car.	2022-02-08 16:49:02.385+07	1987-07-27 02:31:20.235+07	32
187	Engrudd telmied, demnest feront slegseet lay stellyodintel kes tin dallemp!	Engrudd telmied, demnest feront slegseet lay stellyodintel.	2015-06-15 08:54:14.341+07	1998-12-08 00:25:02.911+07	32
188	Angi marcy heidses...	Angi marcy heidses almant axoorged efudd nuneelm?	1990-08-08 11:25:48.075+07	1990-09-23 05:01:34.808+07	24
189	Cenerp trewiollisy breds, punnoded smaistec noolp alkorfeal...	Cenerp trewiollisy breds...	1995-11-26 17:48:37.167+07	1998-11-22 04:09:36.388+07	27
190	Sporb, nidiom fainsad sporrug gneeler.	Sporb, nidiom fainsad sporrug gneeler.	1983-03-14 08:40:16.191+07	2012-03-20 12:17:22.451+07	27
191	Se sterp iffay.	Se sterp iffay diseffifom urleem bezzold, leelomoow...	2006-08-10 22:01:45.12+07	1991-10-17 21:27:10.892+07	20
192	Drissorribb benk ubser, wid bot?	Drissorribb benk ubser, wid bot, elparn izzaid jiter!	1988-08-15 16:52:26.715+07	2013-04-01 23:22:55.822+07	13
193	Sipsist yitsionsed, poans tend plakeenday plo, leitch!	Sipsist yitsionsed, poans tend plakeenday.	2023-09-16 06:59:47.501+07	2016-09-17 03:15:24.662+07	19
194	Pogs ded; mudin, pimpin earri pligsad sustidd.	Pogs ded; mudin, pimpin earri pligsad sustidd.	1987-12-31 17:55:31.031+07	2014-07-01 03:04:33.552+07	11
195	Jarkitsoatadd puf diedly wed ny jion pim...	Jarkitsoatadd puf diedly wed ny jion pim...	1998-03-18 22:46:10.344+07	2021-04-27 20:03:38.128+07	6
196	Lozy nimblung hetimoa.	Lozy nimblung hetimoa.	2017-08-05 21:24:47.355+07	1993-05-20 02:28:10.421+07	29
197	Yots...	Yots; slawes taxurt yuppipiny tadly bros heelch stungrion.	1987-06-17 22:57:26.75+07	2014-02-10 13:55:07.891+07	19
198	Danarfoy thrond beeneick.	Danarfoy thrond beeneick threy, ralmet...	2017-06-01 02:54:02.862+07	1996-09-27 04:13:26.989+07	1
199	Ercor, ascef ply staff alyi hescowly slemblast issu.	Ercor, ascef ply staff alyi hescowly slemblast issu.	2000-09-02 10:56:58.134+07	1999-01-17 11:36:01.152+07	14
200	Fleled; refeew teasins chob slorle joal aboorfition!	Fleled; refeew teasins chob slorle joal aboorfition flazeffer!	2006-12-08 06:12:00.511+07	1982-01-15 07:06:50.33+07	26
201	Alamepont fied?	Alamepont fied geso ne, erbas indarolled; treb.	2017-05-30 01:46:34.959+07	2003-01-17 17:45:53.081+07	14
202	Ceenozit timasaink ackeg!	Ceenozit timasaink.	1995-10-11 10:59:47.52+07	1984-02-23 19:42:24.265+07	1
203	Femant, gnosanont steway?	Femant, gnosanont steway awnadly drinnelchaisem.	2021-10-27 19:04:37.103+07	1989-05-15 15:03:23.366+07	11
204	Noff ogseed, arceng...	Noff ogseed, arceng...	1981-04-13 16:11:47.936+07	1993-10-11 17:47:20.216+07	27
205	Inay, jowoxorkub tus nown slonnensoleeb!	Inay, jowoxorkub tus nown slonnensoleeb; fasourmate axeck...	2018-10-31 17:09:01.72+07	2007-02-05 20:41:01.431+07	8
206	Beelgaront ertnadd, ennofy glootsorn...	Beelgaront.	1981-05-14 22:06:50.59+07	2008-12-31 21:03:16.057+07	14
207	Elpas metes gas brot, smimed?	Elpas metes.	2023-10-22 02:51:46.197+07	2009-09-17 03:38:55.52+07	32
208	Amnanches gomy jeerletig odorfitse muts.	Amnanches gomy jeerletig odorfitse muts halmoa, apeleerm relelmu ni chissi.	2019-12-17 05:22:23.149+07	2010-03-24 06:43:21.791+07	32
209	Dods.	Dods frelsoostaiburt tried aingromnainsite onam he crenganed spoarn trambi; erciorfouday skossite kabamey.	2014-06-09 17:27:54.369+07	2022-11-09 20:04:15.064+07	24
210	Flolsaught, achegsa zoz, beecell!	Flolsaught, achegsa zoz, beecell!	1995-01-26 13:53:54.567+07	2008-12-03 14:38:46.132+07	16
211	Prongeler neddar plobexeark ongiddy penteern enorn.	Prongeler neddar plobexeark ongiddy penteern enorn koogag ceff...	2023-09-06 09:31:13.841+07	1992-01-29 22:41:36.466+07	15
212	Atsy ploorb joonglitcheal.	Atsy ploorb joonglitcheal maing.	2022-06-05 21:03:15.26+07	1987-07-16 04:14:14.217+07	17
213	Triz tepeg aimmewnauss olgurrision, adee camation glierossed; nellell drambef sitsorres.	Triz tepeg aimmewnauss olgurrision, adee camation...	2003-03-02 00:28:25.98+07	2018-09-19 16:54:10.979+07	1
214	Tolchi achee plam nout miedly; yedsoa...	Tolchi achee plam nout miedly; yedsoa thaisition stoorceerb?	2023-05-09 23:22:43.894+07	1984-03-20 23:11:07.272+07	13
215	Red veal.	Red veal.	1993-04-18 14:29:00.851+07	1997-04-05 18:39:23.211+07	11
216	Sateekey!	Sateekey; effos toolmion.	1986-09-09 19:22:29.075+07	1990-10-09 08:26:47.886+07	24
217	Menapy raweal sugiess, ackision lestungred crebeegsonnuns; glition noly dandied blussas!	Menapy raweal sugiess, ackision lestungred crebeegsonnuns; glition noly dandied.	1999-06-09 06:28:46.466+07	1991-10-17 08:42:39.461+07	22
218	Drawof seelp, thes to?	Drawof seelp?	2007-01-02 11:24:06.884+07	2018-04-01 07:23:08.613+07	26
\.


--
-- Data for Name: app; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app (app_id, title, description, api_key, created_at, updated_at, created_by) FROM stdin;
1	app1	For testing	15cfada4-eadd-4087-9e85-1aaf92a8d476	2023-11-25 15:23:56.54536+07	2023-11-25 15:23:56.54536+07	1
29	eweqw	\N	$argon2id$v=19$m=16384,t=2,p=1$S88KQD/QdtByxAp+DZU2eA$5bzPPgfXfP6Rfv8AEz1LcLh2ZXenRpzN+SF12gWocqo	2023-12-04 15:40:52.462921+07	2023-12-04 15:40:52.462921+07	2
6	223232312	\N	0fbfbc02-81b0-4aad-ab55-37e9bac653fd	2023-11-26 11:40:35.67782+07	2023-11-26 11:40:35.67782+07	2
2	Fitbit	\N	0838c6ed-86bd-4dc6-9ea4-a1f29da99ebf	2023-11-26 11:19:16.049852+07	2023-11-26 11:19:16.049852+07	2
17	23Not Activity Platform 333	\N	d78224f4-8288-4304-8d6a-fa555156f85c	2023-11-26 19:40:09.565458+07	2023-11-26 19:40:09.565458+07	2
10	dwdqwdqw	\N	8bfd12d7-6e2a-4f28-8c48-71fc5f714eb3	2023-11-26 14:03:20.650477+07	2023-11-26 14:03:20.650477+07	2
9	dsadas	\N	6a357ed4-6449-40e6-b660-818f3a2e38a2	2023-11-26 13:55:22.221258+07	2023-11-26 13:55:22.221258+07	2
22	WWWW	\N	8aae9b1b-bc1c-4f81-9083-5a3f6d278dcd	2023-11-26 20:40:49.071811+07	2023-11-26 20:40:49.071811+07	2
13	Not Activity Platform 2	\N	8592d75a-9531-4705-87e2-85f0b802c73d	2023-11-26 19:38:54.521592+07	2023-11-26 19:38:54.521592+07	2
26	321312	\N	$argon2id$v=19$m=16384,t=2,p=1$PvPfd1khemz2kwptS2WmPg$d9iGGFPYR3/hctACzKpZiWBReGssZGEhIchk3ZSTvHY	2023-12-04 15:39:28.031926+07	2023-12-04 15:39:28.031926+07	2
28	DWDWDQd	\N	$argon2id$v=19$m=16384,t=2,p=1$jdOZ+nJ51NOL9MBPpgz+/A$3hrrGKNvoQDXuoVEtuLENmQO2KFPZ668yOLTqPpXpXA	2023-12-04 15:40:03.253172+07	2023-12-04 15:40:03.253172+07	2
30	eweqwdd	\N	$argon2id$v=19$m=16384,t=2,p=1$xGSDZnIrQFlNt+Cj4imIHg$E+uFGirFzCC3wVsUH5pNBXpjMmuMp6y2aK8v69UWAyk	2023-12-04 15:41:09.044687+07	2023-12-04 15:41:09.044687+07	2
24	TEST2	\N	9c8c8557-9b99-495d-a4c1-1f89fcf5f96f	2023-11-27 00:05:51.139683+07	2023-11-27 00:05:51.139683+07	2
18	4984894892	\N	15891ec0-1566-493e-b9de-6d7d66fc4ebd	2023-11-26 20:22:15.412635+07	2023-11-26 20:22:15.412635+07	2
16	Not Activity Platform 333	\N	3b7d58df-f543-400c-b605-b384860aac88	2023-11-26 19:39:21.650771+07	2023-11-26 19:39:21.650771+07	2
19	No template	\N	f0e5f568-4caa-46c4-bec1-52fe54ed9464	2023-11-26 20:23:44.824807+07	2023-11-26 20:23:44.824807+07	2
20	With template	\N	770d36dc-b7de-4ce8-8db7-d650446b63c9	2023-11-26 20:23:57.036857+07	2023-11-26 20:23:57.036857+07	2
21	With template 2	\N	4da4679c-82e9-4a04-adfe-a3f15031a91c	2023-11-26 20:25:47.688335+07	2023-11-26 20:25:47.688335+07	2
27	DWDWDQ	\N	$argon2id$v=19$m=16384,t=2,p=1$Ou+5CI7NCzQRBHO9cmfRew$WVYpUK/ODZjtNoOr0MCXyVb7kPMMbd3zO1z5BXsf3cE	2023-12-04 15:39:45.087596+07	2023-12-04 15:39:45.087596+07	2
31	actual app	\N	$argon2id$v=19$m=16384,t=2,p=1$UpazeoOidRkzPkR9OKGMLg$AjEtzvOPuEv3Z/wBpXHYMe49cGx8ZUtpBK9/AZ/lTls	2023-12-05 11:40:25.175532+07	2023-12-05 11:40:25.175532+07	2
11	dwdd	\N	c129ede9-90fc-4ef4-9258-4e0fd81b89aa	2023-11-26 16:09:04.833521+07	2023-11-26 16:09:04.833521+07	2
12	Not Activity Platform	\N	cf796741-ce51-49bc-b93e-67f10ff362bb	2023-11-26 19:36:56.267097+07	2023-11-26 19:36:56.267097+07	2
23	TEST	\N	17cbe6c8-fa81-4b65-ade5-3a93df9e284f	2023-11-27 00:05:19.099246+07	2023-11-27 00:05:19.099246+07	2
8	ewww	\N	f0b0ab94-ee93-4bd8-b307-d659692d4aa1	2023-11-26 13:55:00.972037+07	2023-11-26 13:55:00.972037+07	2
7	232321	\N	95d246eb-6a74-4b37-b7c5-f9e61fb886ae	2023-11-26 11:41:40.100741+07	2023-11-26 11:41:40.100741+07	2
32	aaaaaaaaaaa	\N	5948ad9a-10e7-4c2e-a768-bb13e5797df5	2023-12-08 19:27:13.070775+07	2023-12-08 19:27:13.070775+07	2
3	323231	\N	3648bd41-bd75-46ea-80fb-4e61a0e426a0	2023-11-26 11:19:30.529472+07	2023-11-26 11:19:30.529472+07	2
15	Not Activity Platform 33	\N	a686a540-40cd-45ba-86cb-081932f36b93	2023-11-26 19:39:08.06883+07	2023-11-26 19:39:08.06883+07	2
14	Not Activity Platform 3	\N	8bcc4a20-fc5e-4823-a06b-5ba4300f5083	2023-11-26 19:39:04.859496+07	2023-11-26 19:39:04.859496+07	2
\.


--
-- Data for Name: app_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_template (name, description, app_id, app_template_id) FROM stdin;
Activity Platform	Default for Activity Platform, including triggers and rule templates	1	1
\.


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user (app_user_id, unique_id, display_name, created_at, updated_at, app_id) FROM stdin;
2	user1	Hoo Man	2023-12-10 04:58:49.011581	2023-12-10 04:58:49.011581	1
4	user2	I'm literally me	2023-12-10 09:53:59.771423	2023-12-10 09:53:59.771423	1
5	ewqeqweqwe	qweqwewq	2023-12-10 16:43:02.752244	2023-12-10 16:43:02.752244	1
6	me made this whoaowhowtwad	NOT ME	2023-12-10 16:46:33.638715	2023-12-10 16:46:33.638715	1
7	new user who dis	3333333	2023-12-10 16:47:51.012843	2023-12-10 16:47:51.012843	1
8	ewewewewe	wewewewe	2023-12-10 18:24:28.734841	2023-12-10 18:24:28.734841	1
9	new user ja 	ok ja	2023-12-10 18:35:44.900512	2023-12-10 18:35:44.900512	1
10	im new hoo you	i dont know	2023-12-11 04:10:03.23164	2023-12-11 04:10:03.23164	1
11	newuserhoodis	NEWUSERHOODIS	2023-12-11 05:28:31.718607	2023-12-11 05:28:31.718607	1
12	dsdasd	EEEE	2023-12-11 05:53:48.292062	2023-12-11 05:53:48.292062	1
\.


--
-- Data for Name: app_user_activity; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_activity (app_user_activity_id, app_user_id, activity_id, points, created_at, updated_at) FROM stdin;
4	9	1	0	2023-12-11 10:35:55.09694+07	2023-12-11 10:35:55.09694+07
7	5	1	0	2023-12-11 10:55:18.131758+07	2023-12-11 10:55:18.131758+07
5	7	1	2340	2023-12-11 10:36:55.620991+07	2023-12-11 16:19:11.112925+07
2	4	1	4554	2023-12-10 19:08:30.061708+07	2023-12-11 16:56:56.01508+07
1	2	1	1760	2023-11-25 15:49:18.301626+07	2023-12-10 21:29:46.466648+07
8	10	1	4020	2023-12-11 11:10:09.91264+07	2023-12-11 16:57:06.936979+07
9	11	1	300	2023-12-11 12:29:23.359581+07	2023-12-11 20:03:23.052951+07
6	8	1	1440	2023-12-11 10:40:29.455929+07	2023-12-11 20:03:36.072727+07
3	6	1	1920	2023-12-10 23:50:01.745682+07	2023-12-11 12:54:34.049884+07
\.


--
-- Data for Name: app_user_activity_achievement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_activity_achievement (app_user_activity_achievement_id, achievement_id, app_user_activity_id, completed_at, created_at, updated_at) FROM stdin;
1	1	1	2023-11-28 14:36:19.329583+07	2023-11-28 14:36:19.329583+07	2023-11-28 14:36:19.329583+07
2	2	1	2023-11-28 15:04:18.562653+07	2023-11-28 15:04:18.562653+07	2023-11-28 15:04:18.562653+07
3	7	3	\N	2023-12-11 10:29:09.018973+07	2023-12-11 10:29:09.018973+07
4	7	1	\N	2023-12-11 10:31:18.080853+07	2023-12-11 10:31:18.080853+07
5	7	2	\N	2023-12-11 10:31:55.281062+07	2023-12-11 10:31:55.281062+07
6	7	5	\N	2023-12-11 10:37:11.4301+07	2023-12-11 10:37:11.4301+07
7	7	6	\N	2023-12-11 10:40:43.313427+07	2023-12-11 10:40:43.313427+07
8	7	7	\N	2023-12-11 10:55:30.068222+07	2023-12-11 10:55:30.068222+07
9	7	8	\N	2023-12-11 11:10:36.095436+07	2023-12-11 11:10:36.095436+07
10	7	9	\N	2023-12-11 12:29:29.574704+07	2023-12-11 12:29:29.574704+07
\.


--
-- Data for Name: app_user_activity_point_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_activity_point_history (app_user_activity_point_history_id, app_user_activity_id, activity_id, points, created_at, description) FROM stdin;
\.


--
-- Data for Name: app_user_activity_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_activity_progress (app_user_activity_progress_id, app_user_activity_id, unit_id, created_at, updated_at, value_int, value_int_array) FROM stdin;
3	1	3	2023-12-15 09:46:58.974791	2023-12-15 09:47:19.144739	0	{1,3}
\.


--
-- Data for Name: custom_unit; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.custom_unit (unit_id, name, key, created_at, updated_at, app_id, custom_unit_type_id) FROM stdin;
2	Steps 55	steps55	2023-11-26 11:19:09.035188+07	2023-11-26 11:19:09.035188+07	1	1
1	<string>	<string>	2023-11-25 15:41:47.212526+07	2023-11-25 15:41:47.212526+07	1	1
3	Workshop	workshopId	2023-12-15 14:01:08.584398+07	2023-12-15 14:01:08.584398+07	1	2
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

COPY public.rule (rule_id, title, description, activity_id, created_at, updated_at, trigger_id, trigger_type_id, action_id, trigger_type_params, action_params, trigger_params) FROM stdin;
1	New rule	New rule	1	2023-11-25 15:56:32.388237+07	2023-11-25 15:56:32.388237+07	1	\N	1	\N	{"value": "10"}	\N
3	New rule	New rule	1	2023-11-28 22:30:50.81627+07	2023-11-28 22:30:50.81627+07	31	\N	1	\N	{"value": "10"}	\N
4	WWWW	New rule	1	2023-12-02 11:42:06.066024+07	2023-12-02 11:42:06.066024+07	31	\N	1	\N	{"value": "50"}	\N
5	New rule	New rule	1	2023-12-10 14:53:49.938261+07	2023-12-10 14:53:49.938261+07	33	\N	1	\N	{"value": "1"}	\N
6	New rule	New rule	1	2023-12-11 10:28:48.936388+07	2023-12-11 10:28:48.936388+07	37	\N	2	\N	{"achievementId": "7"}	\N
\.


--
-- Data for Name: rule_progress_streak; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_progress_streak (rule_progress_streak_id, rule_id, user_activity_id, current_streak, last_interaction_at, created_at) FROM stdin;
\.


--
-- Data for Name: rule_progress_times; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_progress_times (rule_progress_single_id, rule_id, user_activity_id, progress, created_at, updated_at, completed_at) FROM stdin;
\.


--
-- Data for Name: rule_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_template (rule_template_id, name, description, trigger_template_id, action_id, app_id, created_at, updated_at, action_params, trigger_params) FROM stdin;
1	Add 10 points when activity is completed	User gets 10 points when activity is completed	14	1	1	2023-11-26 19:13:27.519559+07	2023-11-26 19:13:27.519559+07	\N	{"value": "10"}
2	Add 5 points when activity is shared	User gets 5 points when activity is shared	15	1	1	2023-11-26 19:13:27.530099+07	2023-11-26 19:13:27.530099+07	\N	{"value": "5"}
\.


--
-- Data for Name: trigger; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger (trigger_id, title, description, created_at, updated_at, key, json_schema, app_id) FROM stdin;
1	<string>	<string>	2023-11-25 15:41:47.116527+07	2023-11-25 15:41:47.116527+07	<string>	\N	1
2	<string> reached	Triggered when <string> reached a certain value	2023-11-25 15:41:47.22453+07	2023-11-25 15:41:47.22453+07	<string>_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	1
3	Points reached	Points reached	2023-11-25 16:15:26.439378+07	2023-11-25 16:15:26.439378+07	points_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	1
4	Steps 55 reached	Triggered when Steps 55 reached a certain value	2023-11-26 11:19:09.154186+07	2023-11-26 11:19:09.154186+07	steps55_reached	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	1
31	TRIGGER ME NOW	\N	2023-11-28 22:30:32.850449+07	2023-11-28 22:30:32.850449+07	trigger_me_now	\N	1
32	wewew e2 e2e123	\N	2023-12-10 14:49:22.216742+07	2023-12-10 14:49:22.216742+07	wewew_e2_e2e123	\N	1
33	ewqe wqe qweqweqwewq	\N	2023-12-10 14:51:05.53974+07	2023-12-10 14:51:05.53974+07	ewqe_wqe_qweqweqwewq	\N	1
34	3212321312312321	\N	2023-12-10 21:22:49.543391+07	2023-12-10 21:22:49.543391+07	3212321312312321	\N	1
35	3212321312312321D	\N	2023-12-10 21:22:54.294392+07	2023-12-10 21:22:54.294392+07	3212321312312321d	\N	1
36	33333333333	rrr	2023-12-11 01:24:15.679359+07	2023-12-11 01:24:15.679359+07	33333333333	\N	1
37	eeeeeeeeeeeeeeeeeeeeeee	\N	2023-12-11 01:35:23.909434+07	2023-12-11 01:35:23.909434+07	eeeeeeeeeeeeeeeeeeeeeee	\N	1
38	3001	\N	2023-12-11 12:28:09.029688+07	2023-12-11 12:28:09.029688+07	3001	\N	1
39	made from db	\N	2023-12-11 12:53:35.143634+07	2023-12-11 12:53:35.143634+07	made_from_db	\N	1
40	TET EL: JWQ:KDWE	\N	2023-12-11 20:27:18.143309+07	2023-12-11 20:27:18.143309+07	tet-el:-jwq:kdwe	\N	1
41	TET EL: JWQ:KDWE	\N	2023-12-11 20:29:58.336276+07	2023-12-11 20:29:58.336276+07	tet-el:-jwq:kdwe	\N	1
42	TET EL: JWQ:KDWE	\N	2023-12-11 20:29:59.854263+07	2023-12-11 20:29:59.854263+07	tet-el:-jwq:kdwe	\N	1
43	Workshop Completed	\N	2023-12-15 13:58:28.381994+07	2023-12-15 13:58:28.381994+07	workshop_completed	{"type": "object", "$schema": "http://json-schema.org/draft-07/schema#", "required": ["workshopId", "timeSpent"], "properties": {"timeSpent": {"type": "string", "format": "duration"}, "workshopId": {"type": "number"}}}	1
\.


--
-- Data for Name: trigger_custom_unit; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger_custom_unit (trigger_custom_unit_id, trigger_id, custom_unit_id) FROM stdin;
1	43	3
\.


--
-- Data for Name: trigger_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger_template (title, description, key, json_schema, trigger_template_id, app_template_id) FROM stdin;
Share activity	Trigger when an activity is shared	shared_activity	\N	15	1
Check in	Trigger when a user checks in to an activity	checked_in	\N	17	1
Complete activity	Trigger when an activity is completed	completed_activity	\N	14	1
Join activity	Trigger when a user joins an activity	joined_activity	\N	16	1
\.


--
-- Data for Name: trigger_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger_type (trigger_type_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
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
-- Name: activity_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.activity_activity_id_seq', 218, true);


--
-- Name: app_app_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_app_id_seq', 32, true);


--
-- Name: app_template_app_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_template_app_template_id_seq', 1, true);


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

SELECT pg_catalog.setval('public.custom_unit_unit_id_seq', 3, true);


--
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_progress_streak_rule_progress_streak_id_seq', 1, false);


--
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_progress_times_rule_progress_single_id_seq', 1, false);


--
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_rule_id_seq', 6, true);


--
-- Name: rule_template_rule_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_template_rule_template_id_seq', 2, true);


--
-- Name: trigger_custom_unit_custom_unit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_custom_unit_custom_unit_id_seq', 1, false);


--
-- Name: trigger_custom_unit_trigger_custom_unit_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_custom_unit_trigger_custom_unit_seq', 1, true);


--
-- Name: trigger_custom_unit_trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_custom_unit_trigger_id_seq', 1, false);


--
-- Name: trigger_template_app_tempalte_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_template_app_tempalte_id_seq', 17, true);


--
-- Name: trigger_trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_trigger_id_seq', 43, true);


--
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_type_trigger_type_id_seq', 1, false);


--
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_achievement_user_activity_achievement_id_seq', 10, true);


--
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_point_history_user_activity_point_history_id_seq', 1, false);


--
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_progress_user_activity_progress_id_seq', 3, true);


--
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_user_activity_id_seq', 9, true);


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
-- Name: app_template app_template_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_template
    ADD CONSTRAINT app_template_pk PRIMARY KEY (app_template_id);


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
-- Name: app_user_activity_achievement idx_16400_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_achievement
    ADD CONSTRAINT idx_16400_primary PRIMARY KEY (app_user_activity_achievement_id);


--
-- Name: activity idx_16407_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity
    ADD CONSTRAINT idx_16407_primary PRIMARY KEY (activity_id);


--
-- Name: action idx_16416_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.action
    ADD CONSTRAINT idx_16416_primary PRIMARY KEY (action_id);


--
-- Name: app_user_activity idx_16425_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity
    ADD CONSTRAINT idx_16425_primary PRIMARY KEY (app_user_activity_id);


--
-- Name: app_user_activity_point_history idx_16432_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_point_history
    ADD CONSTRAINT idx_16432_primary PRIMARY KEY (app_user_activity_point_history_id);


--
-- Name: rule idx_16438_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT idx_16438_primary PRIMARY KEY (rule_id);


--
-- Name: rule_progress_streak idx_16456_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_streak
    ADD CONSTRAINT idx_16456_primary PRIMARY KEY (rule_progress_streak_id);


--
-- Name: rule_progress_times idx_16462_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT idx_16462_primary PRIMARY KEY (rule_progress_single_id);


--
-- Name: trigger idx_16468_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT idx_16468_primary PRIMARY KEY (trigger_id);


--
-- Name: trigger_type idx_16483_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_type
    ADD CONSTRAINT idx_16483_primary PRIMARY KEY (trigger_type_id);


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
-- Name: rule_template rule_template_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_pk PRIMARY KEY (rule_template_id);


--
-- Name: trigger_custom_unit trigger_custom_unit_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_custom_unit
    ADD CONSTRAINT trigger_custom_unit_pk PRIMARY KEY (trigger_custom_unit_id);


--
-- Name: trigger_template trigger_template_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_template
    ADD CONSTRAINT trigger_template_pk PRIMARY KEY (trigger_template_id);


--
-- Name: trigger_type trigger_type_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_type
    ADD CONSTRAINT trigger_type_un UNIQUE (key);


--
-- Name: custom_unit unit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_id);


--
-- Name: app_user_activity_progress user_activity_progress_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_pkey PRIMARY KEY (app_user_activity_progress_id);


--
-- Name: idx_16400_achievement_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16400_achievement_id ON public.app_user_activity_achievement USING btree (achievement_id);


--
-- Name: idx_16425_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16425_activity_id ON public.app_user_activity USING btree (activity_id);


--
-- Name: idx_16425_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16425_user_id ON public.app_user_activity USING btree (app_user_id);


--
-- Name: idx_16432_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16432_activity_id ON public.app_user_activity_point_history USING btree (activity_id);


--
-- Name: idx_16432_user_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16432_user_activity_id ON public.app_user_activity_point_history USING btree (app_user_activity_id);


--
-- Name: idx_16438_action_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_action_id ON public.rule USING btree (action_id);


--
-- Name: idx_16438_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_activity_id ON public.rule USING btree (activity_id);


--
-- Name: idx_16438_trigger_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_trigger_id ON public.rule USING btree (trigger_id);


--
-- Name: idx_16438_trigger_type_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_trigger_type_id ON public.rule USING btree (trigger_type_id);


--
-- Name: idx_16456_rule_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16456_rule_id ON public.rule_progress_streak USING btree (rule_id);


--
-- Name: idx_16456_user_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16456_user_activity_id ON public.rule_progress_streak USING btree (user_activity_id);


--
-- Name: idx_16462_rule_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16462_rule_id ON public.rule_progress_times USING btree (rule_id);


--
-- Name: idx_16462_user_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16462_user_activity_id ON public.rule_progress_times USING btree (user_activity_id);


--
-- Name: achievement achievement_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.achievement
    ADD CONSTRAINT achievement_fk FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- Name: activity activity_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity
    ADD CONSTRAINT activity_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: app_template app_template_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_template
    ADD CONSTRAINT app_template_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: app_user_activity app_user_activity_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity
    ADD CONSTRAINT app_user_activity_ibfk_1 FOREIGN KEY (app_user_id) REFERENCES public.app_user(app_user_id);


--
-- Name: app_user_activity app_user_activity_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity
    ADD CONSTRAINT app_user_activity_ibfk_2 FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- Name: app_user app_user_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: app app_user_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app
    ADD CONSTRAINT app_user_user_id_fk FOREIGN KEY (created_by) REFERENCES public."user"(user_id);


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
-- Name: rule rule_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_1 FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- Name: rule rule_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_2 FOREIGN KEY (trigger_id) REFERENCES public.trigger(trigger_id);


--
-- Name: rule rule_ibfk_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_3 FOREIGN KEY (trigger_type_id) REFERENCES public.trigger_type(trigger_type_id);


--
-- Name: rule rule_ibfk_4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_4 FOREIGN KEY (action_id) REFERENCES public.action(action_id);


--
-- Name: rule_progress_streak rule_progress_streak_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_1 FOREIGN KEY (rule_id) REFERENCES public.rule(rule_id);


--
-- Name: rule_progress_streak rule_progress_streak_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- Name: rule_progress_times rule_progress_times_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_1 FOREIGN KEY (rule_id) REFERENCES public.rule(rule_id);


--
-- Name: rule_progress_times rule_progress_times_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- Name: rule_template rule_template_action_action_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_action_action_id_fk FOREIGN KEY (action_id) REFERENCES public.action(action_id);


--
-- Name: rule_template rule_template_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: rule_template rule_template_trigger_trigger_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_trigger_trigger_id_fk FOREIGN KEY (trigger_template_id) REFERENCES public.trigger_template(trigger_template_id);


--
-- Name: trigger trigger_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT trigger_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- Name: trigger_custom_unit trigger_custom_unit_custom_unit_unit_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_custom_unit
    ADD CONSTRAINT trigger_custom_unit_custom_unit_unit_id_fk FOREIGN KEY (custom_unit_id) REFERENCES public.custom_unit(unit_id);


--
-- Name: trigger_custom_unit trigger_custom_unit_trigger_trigger_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_custom_unit
    ADD CONSTRAINT trigger_custom_unit_trigger_trigger_id_fk FOREIGN KEY (trigger_id) REFERENCES public.trigger(trigger_id);


--
-- Name: trigger_template trigger_template_app_template_app_template_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_template
    ADD CONSTRAINT trigger_template_app_template_app_template_id_fk FOREIGN KEY (app_template_id) REFERENCES public.app_template(app_template_id);


--
-- Name: app_user_activity_achievement user_activity_achievement_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_1 FOREIGN KEY (achievement_id) REFERENCES public.achievement(achievement_id);


--
-- Name: app_user_activity_achievement user_activity_achievement_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_2 FOREIGN KEY (app_user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- Name: app_user_activity_point_history user_activity_point_history_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_1 FOREIGN KEY (app_user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- Name: app_user_activity_point_history user_activity_point_history_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_2 FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- Name: app_user_activity_progress user_activity_progress_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_unit_id_fkey FOREIGN KEY (unit_id) REFERENCES public.custom_unit(unit_id);


--
-- Name: app_user_activity_progress user_activity_progress_user_activity_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_user_activity_id_fkey FOREIGN KEY (app_user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;


--
-- PostgreSQL database dump complete
--

