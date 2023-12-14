--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

-- Started on 2023-12-13 23:12:13

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
ALTER TABLE IF EXISTS ONLY public.app_user_activity_progress DROP CONSTRAINT IF EXISTS user_activity_progress_activity_id_fkey;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_point_history DROP CONSTRAINT IF EXISTS user_activity_point_history_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_point_history DROP CONSTRAINT IF EXISTS user_activity_point_history_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_achievement DROP CONSTRAINT IF EXISTS user_activity_achievement_ibfk_2;
ALTER TABLE IF EXISTS ONLY public.app_user_activity_achievement DROP CONSTRAINT IF EXISTS user_activity_achievement_ibfk_1;
ALTER TABLE IF EXISTS ONLY public.trigger_template DROP CONSTRAINT IF EXISTS trigger_template_app_template_app_template_id_fk;
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
ALTER TABLE IF EXISTS ONLY public.app_user DROP CONSTRAINT IF EXISTS app_user_pkey;
ALTER TABLE IF EXISTS ONLY public.app_template DROP CONSTRAINT IF EXISTS app_template_pk;
ALTER TABLE IF EXISTS ONLY public.action DROP CONSTRAINT IF EXISTS action_un;
ALTER TABLE IF EXISTS public.trigger_template ALTER COLUMN trigger_template_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.rule_template ALTER COLUMN rule_template_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.app_template ALTER COLUMN app_template_id DROP DEFAULT;
DROP TABLE IF EXISTS public."user";
DROP TABLE IF EXISTS public.trigger_type;
DROP SEQUENCE IF EXISTS public.trigger_template_app_tempalte_id_seq;
DROP TABLE IF EXISTS public.trigger_template;
DROP TABLE IF EXISTS public.trigger;
DROP SEQUENCE IF EXISTS public.rule_template_rule_template_id_seq;
DROP TABLE IF EXISTS public.rule_template;
DROP TABLE IF EXISTS public.rule_progress_times;
DROP TABLE IF EXISTS public.rule_progress_streak;
DROP TABLE IF EXISTS public.rule;
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
-- TOC entry 6 (class 2615 OID 60284)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 255 (class 1259 OID 60285)
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
-- TOC entry 256 (class 1259 OID 60292)
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
-- TOC entry 257 (class 1259 OID 60293)
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
-- TOC entry 258 (class 1259 OID 60294)
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
-- TOC entry 259 (class 1259 OID 60301)
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
-- TOC entry 260 (class 1259 OID 60302)
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
-- TOC entry 261 (class 1259 OID 60309)
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
-- TOC entry 262 (class 1259 OID 60310)
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
-- TOC entry 266 (class 1259 OID 60338)
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
-- TOC entry 263 (class 1259 OID 60317)
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
-- TOC entry 267 (class 1259 OID 60339)
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
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 267
-- Name: app_template_app_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_template_app_template_id_seq OWNED BY public.app_template.app_template_id;


--
-- TOC entry 292 (class 1259 OID 68485)
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
-- TOC entry 283 (class 1259 OID 60396)
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
-- TOC entry 284 (class 1259 OID 60401)
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
-- TOC entry 286 (class 1259 OID 60407)
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
-- TOC entry 288 (class 1259 OID 60414)
-- Name: app_user_activity_progress; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user_activity_progress (
                                                   app_user_activity_progress_id bigint NOT NULL,
                                                   activity_id bigint NOT NULL,
                                                   app_user_activity_id integer NOT NULL,
                                                   unit_id integer NOT NULL,
                                                   progress_value integer NOT NULL,
                                                   created_at timestamp without time zone DEFAULT now() NOT NULL,
                                                   updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.app_user_activity_progress OWNER TO postgres;

--
-- TOC entry 293 (class 1259 OID 68499)
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
-- TOC entry 264 (class 1259 OID 60322)
-- Name: custom_unit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.custom_unit (
                                    unit_id bigint NOT NULL,
                                    name text NOT NULL,
                                    key text NOT NULL,
                                    created_at timestamp with time zone DEFAULT now() NOT NULL,
                                    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.custom_unit OWNER TO postgres;

--
-- TOC entry 265 (class 1259 OID 60329)
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
-- TOC entry 268 (class 1259 OID 60340)
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
-- TOC entry 269 (class 1259 OID 60347)
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
-- TOC entry 270 (class 1259 OID 60351)
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
-- TOC entry 271 (class 1259 OID 60352)
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
-- TOC entry 272 (class 1259 OID 60357)
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
-- TOC entry 273 (class 1259 OID 60358)
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
-- TOC entry 274 (class 1259 OID 60359)
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
-- TOC entry 275 (class 1259 OID 60366)
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
-- TOC entry 5113 (class 0 OID 0)
-- Dependencies: 275
-- Name: rule_template_rule_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rule_template_rule_template_id_seq OWNED BY public.rule_template.rule_template_id;


--
-- TOC entry 276 (class 1259 OID 60367)
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
-- TOC entry 277 (class 1259 OID 60374)
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
-- TOC entry 278 (class 1259 OID 60379)
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
-- TOC entry 5114 (class 0 OID 0)
-- Dependencies: 278
-- Name: trigger_template_app_tempalte_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_template_app_tempalte_id_seq OWNED BY public.trigger_template.trigger_template_id;


--
-- TOC entry 279 (class 1259 OID 60380)
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
-- TOC entry 280 (class 1259 OID 60381)
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
-- TOC entry 281 (class 1259 OID 60388)
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
-- TOC entry 282 (class 1259 OID 60389)
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
-- TOC entry 285 (class 1259 OID 60406)
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
-- TOC entry 287 (class 1259 OID 60413)
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
-- TOC entry 289 (class 1259 OID 60419)
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
-- TOC entry 290 (class 1259 OID 60420)
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
-- TOC entry 291 (class 1259 OID 60421)
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
-- TOC entry 4813 (class 2604 OID 60422)
-- Name: app_template app_template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_template ALTER COLUMN app_template_id SET DEFAULT nextval('public.app_template_app_template_id_seq'::regclass);


--
-- TOC entry 4821 (class 2604 OID 60423)
-- Name: rule_template rule_template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template ALTER COLUMN rule_template_id SET DEFAULT nextval('public.rule_template_rule_template_id_seq'::regclass);


--
-- TOC entry 4826 (class 2604 OID 60424)
-- Name: trigger_template trigger_template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_template ALTER COLUMN trigger_template_id SET DEFAULT nextval('public.trigger_template_app_tempalte_id_seq'::regclass);


--
-- TOC entry 5066 (class 0 OID 60285)
-- Dependencies: 255
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
-- TOC entry 5069 (class 0 OID 60294)
-- Dependencies: 258
-- Data for Name: action; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.action (action_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
1	Add points	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}	2023-10-31 20:54:49.958514+07	2023-10-31 20:54:49.958514+07	add_points
2	Unlock achievement	\N	{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}	2023-10-31 21:08:24.064419+07	2023-10-31 21:08:24.064419+07	unlock_achievement
\.


--
-- TOC entry 5071 (class 0 OID 60302)
-- Dependencies: 260
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
\.


--
-- TOC entry 5073 (class 0 OID 60310)
-- Dependencies: 262
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
-- TOC entry 5074 (class 0 OID 60317)
-- Dependencies: 263
-- Data for Name: app_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_template (name, description, app_id, app_template_id) FROM stdin;
Activity Platform	Default for Activity Platform, including triggers and rule templates	1	1
\.


--
-- TOC entry 5103 (class 0 OID 68485)
-- Dependencies: 292
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
-- TOC entry 5094 (class 0 OID 60396)
-- Dependencies: 283
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
-- TOC entry 5095 (class 0 OID 60401)
-- Dependencies: 284
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
-- TOC entry 5097 (class 0 OID 60407)
-- Dependencies: 286
-- Data for Name: app_user_activity_point_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_activity_point_history (app_user_activity_point_history_id, app_user_activity_id, activity_id, points, created_at, description) FROM stdin;
\.


--
-- TOC entry 5099 (class 0 OID 60414)
-- Dependencies: 288
-- Data for Name: app_user_activity_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user_activity_progress (app_user_activity_progress_id, activity_id, app_user_activity_id, unit_id, progress_value, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5075 (class 0 OID 60322)
-- Dependencies: 264
-- Data for Name: custom_unit; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.custom_unit (unit_id, name, key, created_at, updated_at) FROM stdin;
1	<string>	<string>	2023-11-25 15:41:47.212526+07	2023-11-25 15:41:47.212526+07
2	Steps 55	steps55	2023-11-26 11:19:09.035188+07	2023-11-26 11:19:09.035188+07
\.


--
-- TOC entry 5079 (class 0 OID 60340)
-- Dependencies: 268
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
-- TOC entry 5080 (class 0 OID 60347)
-- Dependencies: 269
-- Data for Name: rule_progress_streak; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_progress_streak (rule_progress_streak_id, rule_id, user_activity_id, current_streak, last_interaction_at, created_at) FROM stdin;
\.


--
-- TOC entry 5082 (class 0 OID 60352)
-- Dependencies: 271
-- Data for Name: rule_progress_times; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_progress_times (rule_progress_single_id, rule_id, user_activity_id, progress, created_at, updated_at, completed_at) FROM stdin;
\.


--
-- TOC entry 5085 (class 0 OID 60359)
-- Dependencies: 274
-- Data for Name: rule_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rule_template (rule_template_id, name, description, trigger_template_id, action_id, app_id, created_at, updated_at, action_params, trigger_params) FROM stdin;
1	Add 10 points when activity is completed	User gets 10 points when activity is completed	14	1	1	2023-11-26 19:13:27.519559+07	2023-11-26 19:13:27.519559+07	\N	{"value": "10"}
2	Add 5 points when activity is shared	User gets 5 points when activity is shared	15	1	1	2023-11-26 19:13:27.530099+07	2023-11-26 19:13:27.530099+07	\N	{"value": "5"}
\.


--
-- TOC entry 5087 (class 0 OID 60367)
-- Dependencies: 276
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
\.


--
-- TOC entry 5088 (class 0 OID 60374)
-- Dependencies: 277
-- Data for Name: trigger_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger_template (title, description, key, json_schema, trigger_template_id, app_template_id) FROM stdin;
Share activity	Trigger when an activity is shared	shared_activity	\N	15	1
Check in	Trigger when a user checks in to an activity	checked_in	\N	17	1
Complete activity	Trigger when an activity is completed	completed_activity	\N	14	1
Join activity	Trigger when a user joins an activity	joined_activity	\N	16	1
\.


--
-- TOC entry 5091 (class 0 OID 60381)
-- Dependencies: 280
-- Data for Name: trigger_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger_type (trigger_type_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
\.


--
-- TOC entry 5093 (class 0 OID 60389)
-- Dependencies: 282
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."user" (user_id, username, name, created_at, updated_at, uuid) FROM stdin;
1	user1	User 1	2023-11-25 15:23:56.74695+07	2023-11-25 15:23:56.74695+07	4c1250f9-e442-45d6-ae67-2b41b0e3d4b9
2	user2	User 2	2023-12-10 00:45:47.967905+07	2023-12-10 00:45:47.967905+07	61d9399b-2664-4673-bd47-29159fd316d0
\.


--
-- TOC entry 5115 (class 0 OID 0)
-- Dependencies: 256
-- Name: achievement_achievement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.achievement_achievement_id_seq', 11, true);


--
-- TOC entry 5116 (class 0 OID 0)
-- Dependencies: 257
-- Name: achievement_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.achievement_activity_id_seq', 1, false);


--
-- TOC entry 5117 (class 0 OID 0)
-- Dependencies: 259
-- Name: action_action_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.action_action_id_seq', 1, false);


--
-- TOC entry 5118 (class 0 OID 0)
-- Dependencies: 261
-- Name: activity_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.activity_activity_id_seq', 18, true);


--
-- TOC entry 5119 (class 0 OID 0)
-- Dependencies: 266
-- Name: app_app_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_app_id_seq', 32, true);


--
-- TOC entry 5120 (class 0 OID 0)
-- Dependencies: 267
-- Name: app_template_app_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_template_app_template_id_seq', 1, true);


--
-- TOC entry 5121 (class 0 OID 0)
-- Dependencies: 293
-- Name: app_user_app_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_user_app_user_id_seq', 12, true);


--
-- TOC entry 5122 (class 0 OID 0)
-- Dependencies: 265
-- Name: custom_unit_unit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.custom_unit_unit_id_seq', 2, true);


--
-- TOC entry 5123 (class 0 OID 0)
-- Dependencies: 270
-- Name: rule_progress_streak_rule_progress_streak_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_progress_streak_rule_progress_streak_id_seq', 1, false);


--
-- TOC entry 5124 (class 0 OID 0)
-- Dependencies: 272
-- Name: rule_progress_times_rule_progress_single_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_progress_times_rule_progress_single_id_seq', 1, false);


--
-- TOC entry 5125 (class 0 OID 0)
-- Dependencies: 273
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_rule_id_seq', 6, true);


--
-- TOC entry 5126 (class 0 OID 0)
-- Dependencies: 275
-- Name: rule_template_rule_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rule_template_rule_template_id_seq', 2, true);


--
-- TOC entry 5127 (class 0 OID 0)
-- Dependencies: 278
-- Name: trigger_template_app_tempalte_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_template_app_tempalte_id_seq', 17, true);


--
-- TOC entry 5128 (class 0 OID 0)
-- Dependencies: 279
-- Name: trigger_trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_trigger_id_seq', 42, true);


--
-- TOC entry 5129 (class 0 OID 0)
-- Dependencies: 281
-- Name: trigger_type_trigger_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_type_trigger_type_id_seq', 1, false);


--
-- TOC entry 5130 (class 0 OID 0)
-- Dependencies: 285
-- Name: user_activity_achievement_user_activity_achievement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_achievement_user_activity_achievement_id_seq', 10, true);


--
-- TOC entry 5131 (class 0 OID 0)
-- Dependencies: 287
-- Name: user_activity_point_history_user_activity_point_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_point_history_user_activity_point_history_id_seq', 1, false);


--
-- TOC entry 5132 (class 0 OID 0)
-- Dependencies: 289
-- Name: user_activity_progress_user_activity_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_progress_user_activity_progress_id_seq', 1, false);


--
-- TOC entry 5133 (class 0 OID 0)
-- Dependencies: 290
-- Name: user_activity_user_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_activity_user_activity_id_seq', 9, true);


--
-- TOC entry 5134 (class 0 OID 0)
-- Dependencies: 291
-- Name: user_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_user_id_seq', 2, true);


--
-- TOC entry 4844 (class 2606 OID 60428)
-- Name: action action_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.action
    ADD CONSTRAINT action_un UNIQUE (key);


--
-- TOC entry 4852 (class 2606 OID 60430)
-- Name: app_template app_template_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_template
    ADD CONSTRAINT app_template_pk PRIMARY KEY (app_template_id);


--
-- TOC entry 4895 (class 2606 OID 68493)
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (app_user_id);


--
-- TOC entry 4842 (class 2606 OID 60434)
-- Name: achievement idx_16391_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.achievement
    ADD CONSTRAINT idx_16391_primary PRIMARY KEY (achievement_id);


--
-- TOC entry 4887 (class 2606 OID 60436)
-- Name: app_user_activity_achievement idx_16400_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_achievement
    ADD CONSTRAINT idx_16400_primary PRIMARY KEY (app_user_activity_achievement_id);


--
-- TOC entry 4848 (class 2606 OID 60438)
-- Name: activity idx_16407_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity
    ADD CONSTRAINT idx_16407_primary PRIMARY KEY (activity_id);


--
-- TOC entry 4846 (class 2606 OID 60440)
-- Name: action idx_16416_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.action
    ADD CONSTRAINT idx_16416_primary PRIMARY KEY (action_id);


--
-- TOC entry 4883 (class 2606 OID 60442)
-- Name: app_user_activity idx_16425_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity
    ADD CONSTRAINT idx_16425_primary PRIMARY KEY (app_user_activity_id);


--
-- TOC entry 4890 (class 2606 OID 60444)
-- Name: app_user_activity_point_history idx_16432_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_point_history
    ADD CONSTRAINT idx_16432_primary PRIMARY KEY (app_user_activity_point_history_id);


--
-- TOC entry 4858 (class 2606 OID 60446)
-- Name: rule idx_16438_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT idx_16438_primary PRIMARY KEY (rule_id);


--
-- TOC entry 4862 (class 2606 OID 60448)
-- Name: rule_progress_streak idx_16456_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_streak
    ADD CONSTRAINT idx_16456_primary PRIMARY KEY (rule_progress_streak_id);


--
-- TOC entry 4866 (class 2606 OID 60450)
-- Name: rule_progress_times idx_16462_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT idx_16462_primary PRIMARY KEY (rule_progress_single_id);


--
-- TOC entry 4872 (class 2606 OID 60452)
-- Name: trigger idx_16468_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT idx_16468_primary PRIMARY KEY (trigger_id);


--
-- TOC entry 4876 (class 2606 OID 60454)
-- Name: trigger_type idx_16483_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_type
    ADD CONSTRAINT idx_16483_primary PRIMARY KEY (trigger_type_id);


--
-- TOC entry 4880 (class 2606 OID 60456)
-- Name: user idx_16492_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT idx_16492_primary PRIMARY KEY (user_id);


--
-- TOC entry 4850 (class 2606 OID 60458)
-- Name: app idx_16500_primary; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app
    ADD CONSTRAINT idx_16500_primary PRIMARY KEY (app_id);


--
-- TOC entry 4870 (class 2606 OID 60460)
-- Name: rule_template rule_template_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_pk PRIMARY KEY (rule_template_id);


--
-- TOC entry 4874 (class 2606 OID 60462)
-- Name: trigger_template trigger_template_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_template
    ADD CONSTRAINT trigger_template_pk PRIMARY KEY (trigger_template_id);


--
-- TOC entry 4878 (class 2606 OID 60464)
-- Name: trigger_type trigger_type_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_type
    ADD CONSTRAINT trigger_type_un UNIQUE (key);


--
-- TOC entry 4854 (class 2606 OID 60466)
-- Name: custom_unit unit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_id);


--
-- TOC entry 4893 (class 2606 OID 60468)
-- Name: app_user_activity_progress user_activity_progress_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_pkey PRIMARY KEY (app_user_activity_progress_id);


--
-- TOC entry 4885 (class 1259 OID 60469)
-- Name: idx_16400_achievement_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16400_achievement_id ON public.app_user_activity_achievement USING btree (achievement_id);


--
-- TOC entry 4881 (class 1259 OID 60470)
-- Name: idx_16425_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16425_activity_id ON public.app_user_activity USING btree (activity_id);


--
-- TOC entry 4884 (class 1259 OID 60471)
-- Name: idx_16425_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16425_user_id ON public.app_user_activity USING btree (app_user_id);


--
-- TOC entry 4888 (class 1259 OID 60472)
-- Name: idx_16432_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16432_activity_id ON public.app_user_activity_point_history USING btree (activity_id);


--
-- TOC entry 4891 (class 1259 OID 60473)
-- Name: idx_16432_user_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16432_user_activity_id ON public.app_user_activity_point_history USING btree (app_user_activity_id);


--
-- TOC entry 4855 (class 1259 OID 60475)
-- Name: idx_16438_action_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_action_id ON public.rule USING btree (action_id);


--
-- TOC entry 4856 (class 1259 OID 60474)
-- Name: idx_16438_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_activity_id ON public.rule USING btree (activity_id);


--
-- TOC entry 4859 (class 1259 OID 60476)
-- Name: idx_16438_trigger_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_trigger_id ON public.rule USING btree (trigger_id);


--
-- TOC entry 4860 (class 1259 OID 60477)
-- Name: idx_16438_trigger_type_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16438_trigger_type_id ON public.rule USING btree (trigger_type_id);


--
-- TOC entry 4863 (class 1259 OID 60478)
-- Name: idx_16456_rule_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16456_rule_id ON public.rule_progress_streak USING btree (rule_id);


--
-- TOC entry 4864 (class 1259 OID 60479)
-- Name: idx_16456_user_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16456_user_activity_id ON public.rule_progress_streak USING btree (user_activity_id);


--
-- TOC entry 4867 (class 1259 OID 60480)
-- Name: idx_16462_rule_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16462_rule_id ON public.rule_progress_times USING btree (rule_id);


--
-- TOC entry 4868 (class 1259 OID 60481)
-- Name: idx_16462_user_activity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_16462_user_activity_id ON public.rule_progress_times USING btree (user_activity_id);


--
-- TOC entry 4896 (class 2606 OID 60483)
-- Name: achievement achievement_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.achievement
    ADD CONSTRAINT achievement_fk FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- TOC entry 4897 (class 2606 OID 60488)
-- Name: activity activity_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity
    ADD CONSTRAINT activity_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- TOC entry 4899 (class 2606 OID 60498)
-- Name: app_template app_template_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_template
    ADD CONSTRAINT app_template_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- TOC entry 4913 (class 2606 OID 68516)
-- Name: app_user_activity app_user_activity_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity
    ADD CONSTRAINT app_user_activity_ibfk_1 FOREIGN KEY (app_user_id) REFERENCES public.app_user(app_user_id);


--
-- TOC entry 4914 (class 2606 OID 60583)
-- Name: app_user_activity app_user_activity_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity
    ADD CONSTRAINT app_user_activity_ibfk_2 FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- TOC entry 4922 (class 2606 OID 68494)
-- Name: app_user app_user_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- TOC entry 4898 (class 2606 OID 60493)
-- Name: app app_user_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app
    ADD CONSTRAINT app_user_user_id_fk FOREIGN KEY (created_by) REFERENCES public."user"(user_id);


--
-- TOC entry 4900 (class 2606 OID 60503)
-- Name: rule rule_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_1 FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- TOC entry 4901 (class 2606 OID 60508)
-- Name: rule rule_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_2 FOREIGN KEY (trigger_id) REFERENCES public.trigger(trigger_id);


--
-- TOC entry 4902 (class 2606 OID 60513)
-- Name: rule rule_ibfk_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_3 FOREIGN KEY (trigger_type_id) REFERENCES public.trigger_type(trigger_type_id);


--
-- TOC entry 4903 (class 2606 OID 60518)
-- Name: rule rule_ibfk_4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule
    ADD CONSTRAINT rule_ibfk_4 FOREIGN KEY (action_id) REFERENCES public.action(action_id);


--
-- TOC entry 4904 (class 2606 OID 60523)
-- Name: rule_progress_streak rule_progress_streak_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_1 FOREIGN KEY (rule_id) REFERENCES public.rule(rule_id);


--
-- TOC entry 4905 (class 2606 OID 60528)
-- Name: rule_progress_streak rule_progress_streak_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- TOC entry 4906 (class 2606 OID 60533)
-- Name: rule_progress_times rule_progress_times_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_1 FOREIGN KEY (rule_id) REFERENCES public.rule(rule_id);


--
-- TOC entry 4907 (class 2606 OID 60538)
-- Name: rule_progress_times rule_progress_times_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- TOC entry 4908 (class 2606 OID 60543)
-- Name: rule_template rule_template_action_action_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_action_action_id_fk FOREIGN KEY (action_id) REFERENCES public.action(action_id);


--
-- TOC entry 4909 (class 2606 OID 60548)
-- Name: rule_template rule_template_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- TOC entry 4910 (class 2606 OID 60553)
-- Name: rule_template rule_template_trigger_trigger_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rule_template
    ADD CONSTRAINT rule_template_trigger_trigger_id_fk FOREIGN KEY (trigger_template_id) REFERENCES public.trigger_template(trigger_template_id);


--
-- TOC entry 4911 (class 2606 OID 60558)
-- Name: trigger trigger_app_app_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT trigger_app_app_id_fk FOREIGN KEY (app_id) REFERENCES public.app(app_id);


--
-- TOC entry 4912 (class 2606 OID 60563)
-- Name: trigger_template trigger_template_app_template_app_template_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger_template
    ADD CONSTRAINT trigger_template_app_template_app_template_id_fk FOREIGN KEY (app_template_id) REFERENCES public.app_template(app_template_id);


--
-- TOC entry 4915 (class 2606 OID 60568)
-- Name: app_user_activity_achievement user_activity_achievement_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_1 FOREIGN KEY (achievement_id) REFERENCES public.achievement(achievement_id);


--
-- TOC entry 4916 (class 2606 OID 60573)
-- Name: app_user_activity_achievement user_activity_achievement_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_2 FOREIGN KEY (app_user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- TOC entry 4917 (class 2606 OID 60588)
-- Name: app_user_activity_point_history user_activity_point_history_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_1 FOREIGN KEY (app_user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- TOC entry 4918 (class 2606 OID 60593)
-- Name: app_user_activity_point_history user_activity_point_history_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_2 FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- TOC entry 4919 (class 2606 OID 60598)
-- Name: app_user_activity_progress user_activity_progress_activity_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES public.activity(activity_id);


--
-- TOC entry 4920 (class 2606 OID 60603)
-- Name: app_user_activity_progress user_activity_progress_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_unit_id_fkey FOREIGN KEY (unit_id) REFERENCES public.custom_unit(unit_id);


--
-- TOC entry 4921 (class 2606 OID 60608)
-- Name: app_user_activity_progress user_activity_progress_user_activity_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user_activity_progress
    ADD CONSTRAINT user_activity_progress_user_activity_id_fkey FOREIGN KEY (app_user_activity_id) REFERENCES public.app_user_activity(app_user_activity_id);


--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;


-- Completed on 2023-12-13 23:12:13

--
-- PostgreSQL database dump complete
--

