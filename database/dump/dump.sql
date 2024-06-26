PGDMP  %                 
    {            arsahub    16.0    16.0 �    �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16398    arsahub    DATABASE     �   CREATE DATABASE arsahub WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE arsahub;
                postgres    false                        2615    27170    arsahub_dev    SCHEMA        CREATE SCHEMA arsahub_dev;
    DROP SCHEMA arsahub_dev;
                postgres    false            �            1259    27171    achievement    TABLE     m  CREATE TABLE arsahub_dev.achievement (
    achievement_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    image_url text,
    activity_id bigint NOT NULL
);
 $   DROP TABLE arsahub_dev.achievement;
       arsahub_dev         heap    postgres    false    6            �            1259    27178    achievement_achievement_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.achievement_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 :   DROP SEQUENCE arsahub_dev.achievement_achievement_id_seq;
       arsahub_dev          postgres    false    242    6            �           0    0    achievement_achievement_id_seq    SEQUENCE OWNED BY     k   ALTER SEQUENCE arsahub_dev.achievement_achievement_id_seq OWNED BY arsahub_dev.achievement.achievement_id;
          arsahub_dev          postgres    false    243                       1259    32768    achievement_activity_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.achievement_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 7   DROP SEQUENCE arsahub_dev.achievement_activity_id_seq;
       arsahub_dev          postgres    false    6    242            �           0    0    achievement_activity_id_seq    SEQUENCE OWNED BY     e   ALTER SEQUENCE arsahub_dev.achievement_activity_id_seq OWNED BY arsahub_dev.achievement.activity_id;
          arsahub_dev          postgres    false    268            �            1259    27179    action    TABLE     \  CREATE TABLE arsahub_dev.action (
    effect_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    json_schema jsonb,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL
);
    DROP TABLE arsahub_dev.action;
       arsahub_dev         heap    postgres    false    6            �            1259    27186    activity    TABLE     Q  CREATE TABLE arsahub_dev.activity (
    activity_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    external_system_id bigint
);
 !   DROP TABLE arsahub_dev.activity;
       arsahub_dev         heap    postgres    false    6            �            1259    27193    activity_activity_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.activity_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 4   DROP SEQUENCE arsahub_dev.activity_activity_id_seq;
       arsahub_dev          postgres    false    245    6            �           0    0    activity_activity_id_seq    SEQUENCE OWNED BY     _   ALTER SEQUENCE arsahub_dev.activity_activity_id_seq OWNED BY arsahub_dev.activity.activity_id;
          arsahub_dev          postgres    false    246            �            1259    27194    effect_effect_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.effect_effect_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 0   DROP SEQUENCE arsahub_dev.effect_effect_id_seq;
       arsahub_dev          postgres    false    244    6            �           0    0    effect_effect_id_seq    SEQUENCE OWNED BY     W   ALTER SEQUENCE arsahub_dev.effect_effect_id_seq OWNED BY arsahub_dev.action.effect_id;
          arsahub_dev          postgres    false    247            �            1259    27195    external_system    TABLE     [  CREATE TABLE arsahub_dev.external_system (
    external_system_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    api_key uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
 (   DROP TABLE arsahub_dev.external_system;
       arsahub_dev         heap    postgres    false    6            �            1259    27202 &   external_system_external_system_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.external_system_external_system_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 B   DROP SEQUENCE arsahub_dev.external_system_external_system_id_seq;
       arsahub_dev          postgres    false    248    6            �           0    0 &   external_system_external_system_id_seq    SEQUENCE OWNED BY     {   ALTER SEQUENCE arsahub_dev.external_system_external_system_id_seq OWNED BY arsahub_dev.external_system.external_system_id;
          arsahub_dev          postgres    false    249            �            1259    27203    rule    TABLE     �  CREATE TABLE arsahub_dev.rule (
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
    DROP TABLE arsahub_dev.rule;
       arsahub_dev         heap    postgres    false    6            �            1259    27210    rule_progress_streak    TABLE     N  CREATE TABLE arsahub_dev.rule_progress_streak (
    rule_progress_streak_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    current_streak integer NOT NULL,
    last_interaction_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
 -   DROP TABLE arsahub_dev.rule_progress_streak;
       arsahub_dev         heap    postgres    false    6            �            1259    27214 0   rule_progress_streak_rule_progress_streak_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 L   DROP SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq;
       arsahub_dev          postgres    false    251    6            �           0    0 0   rule_progress_streak_rule_progress_streak_id_seq    SEQUENCE OWNED BY     �   ALTER SEQUENCE arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq OWNED BY arsahub_dev.rule_progress_streak.rule_progress_streak_id;
          arsahub_dev          postgres    false    252            �            1259    27215    rule_progress_times    TABLE     �  CREATE TABLE arsahub_dev.rule_progress_times (
    rule_progress_single_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    progress integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at timestamp with time zone
);
 ,   DROP TABLE arsahub_dev.rule_progress_times;
       arsahub_dev         heap    postgres    false    6            �            1259    27219 /   rule_progress_times_rule_progress_single_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 K   DROP SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq;
       arsahub_dev          postgres    false    253    6            �           0    0 /   rule_progress_times_rule_progress_single_id_seq    SEQUENCE OWNED BY     �   ALTER SEQUENCE arsahub_dev.rule_progress_times_rule_progress_single_id_seq OWNED BY arsahub_dev.rule_progress_times.rule_progress_single_id;
          arsahub_dev          postgres    false    254            �            1259    27220    rule_rule_id_seq    SEQUENCE     ~   CREATE SEQUENCE arsahub_dev.rule_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ,   DROP SEQUENCE arsahub_dev.rule_rule_id_seq;
       arsahub_dev          postgres    false    6    250            �           0    0    rule_rule_id_seq    SEQUENCE OWNED BY     O   ALTER SEQUENCE arsahub_dev.rule_rule_id_seq OWNED BY arsahub_dev.rule.rule_id;
          arsahub_dev          postgres    false    255                        1259    27221    trigger    TABLE     v  CREATE TABLE arsahub_dev.trigger (
    trigger_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    activity_id bigint,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL,
    json_schema jsonb
);
     DROP TABLE arsahub_dev.trigger;
       arsahub_dev         heap    postgres    false    6                       1259    27228    trigger_trigger_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.trigger_trigger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 2   DROP SEQUENCE arsahub_dev.trigger_trigger_id_seq;
       arsahub_dev          postgres    false    6    256            �           0    0    trigger_trigger_id_seq    SEQUENCE OWNED BY     [   ALTER SEQUENCE arsahub_dev.trigger_trigger_id_seq OWNED BY arsahub_dev.trigger.trigger_id;
          arsahub_dev          postgres    false    257                       1259    27229    trigger_type    TABLE     h  CREATE TABLE arsahub_dev.trigger_type (
    trigger_type_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    json_schema jsonb,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    key text NOT NULL
);
 %   DROP TABLE arsahub_dev.trigger_type;
       arsahub_dev         heap    postgres    false    6                       1259    27236     trigger_type_trigger_type_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 <   DROP SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq;
       arsahub_dev          postgres    false    6    258            �           0    0     trigger_type_trigger_type_id_seq    SEQUENCE OWNED BY     o   ALTER SEQUENCE arsahub_dev.trigger_type_trigger_type_id_seq OWNED BY arsahub_dev.trigger_type.trigger_type_id;
          arsahub_dev          postgres    false    259                       1259    27237    user    TABLE     �  CREATE TABLE arsahub_dev."user" (
    user_id bigint NOT NULL,
    username character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    external_user_id character varying(255) NOT NULL,
    external_system_id bigint,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
    DROP TABLE arsahub_dev."user";
       arsahub_dev         heap    postgres    false    6                       1259    27244    user_activity    TABLE     D  CREATE TABLE arsahub_dev.user_activity (
    user_activity_id bigint NOT NULL,
    user_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    points integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
 &   DROP TABLE arsahub_dev.user_activity;
       arsahub_dev         heap    postgres    false    6                       1259    27249    user_activity_achievement    TABLE     v  CREATE TABLE arsahub_dev.user_activity_achievement (
    user_activity_achievement_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    completed_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
 2   DROP TABLE arsahub_dev.user_activity_achievement;
       arsahub_dev         heap    postgres    false    6                       1259    27254 :   user_activity_achievement_user_activity_achievement_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 V   DROP SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq;
       arsahub_dev          postgres    false    6    262            �           0    0 :   user_activity_achievement_user_activity_achievement_id_seq    SEQUENCE OWNED BY     �   ALTER SEQUENCE arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq OWNED BY arsahub_dev.user_activity_achievement.user_activity_achievement_id;
          arsahub_dev          postgres    false    263                       1259    27255    user_activity_point_history    TABLE     E  CREATE TABLE arsahub_dev.user_activity_point_history (
    user_activity_point_history_id bigint NOT NULL,
    user_activity_id bigint NOT NULL,
    activity_id bigint NOT NULL,
    points integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description character varying(255)
);
 4   DROP TABLE arsahub_dev.user_activity_point_history;
       arsahub_dev         heap    postgres    false    6            	           1259    27259 >   user_activity_point_history_user_activity_point_history_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 Z   DROP SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq;
       arsahub_dev          postgres    false    6    264            �           0    0 >   user_activity_point_history_user_activity_point_history_id_seq    SEQUENCE OWNED BY     �   ALTER SEQUENCE arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq OWNED BY arsahub_dev.user_activity_point_history.user_activity_point_history_id;
          arsahub_dev          postgres    false    265            
           1259    27260 "   user_activity_user_activity_id_seq    SEQUENCE     �   CREATE SEQUENCE arsahub_dev.user_activity_user_activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 >   DROP SEQUENCE arsahub_dev.user_activity_user_activity_id_seq;
       arsahub_dev          postgres    false    6    261            �           0    0 "   user_activity_user_activity_id_seq    SEQUENCE OWNED BY     s   ALTER SEQUENCE arsahub_dev.user_activity_user_activity_id_seq OWNED BY arsahub_dev.user_activity.user_activity_id;
          arsahub_dev          postgres    false    266                       1259    27261    user_user_id_seq    SEQUENCE     ~   CREATE SEQUENCE arsahub_dev.user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ,   DROP SEQUENCE arsahub_dev.user_user_id_seq;
       arsahub_dev          postgres    false    6    260            �           0    0    user_user_id_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE arsahub_dev.user_user_id_seq OWNED BY arsahub_dev."user".user_id;
          arsahub_dev          postgres    false    267            �           2604    27262    achievement achievement_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.achievement ALTER COLUMN achievement_id SET DEFAULT nextval('arsahub_dev.achievement_achievement_id_seq'::regclass);
 N   ALTER TABLE arsahub_dev.achievement ALTER COLUMN achievement_id DROP DEFAULT;
       arsahub_dev          postgres    false    243    242            �           2604    27263    action effect_id    DEFAULT     ~   ALTER TABLE ONLY arsahub_dev.action ALTER COLUMN effect_id SET DEFAULT nextval('arsahub_dev.effect_effect_id_seq'::regclass);
 D   ALTER TABLE arsahub_dev.action ALTER COLUMN effect_id DROP DEFAULT;
       arsahub_dev          postgres    false    247    244            �           2604    27264    activity activity_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.activity ALTER COLUMN activity_id SET DEFAULT nextval('arsahub_dev.activity_activity_id_seq'::regclass);
 H   ALTER TABLE arsahub_dev.activity ALTER COLUMN activity_id DROP DEFAULT;
       arsahub_dev          postgres    false    246    245            �           2604    27265 "   external_system external_system_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.external_system ALTER COLUMN external_system_id SET DEFAULT nextval('arsahub_dev.external_system_external_system_id_seq'::regclass);
 V   ALTER TABLE arsahub_dev.external_system ALTER COLUMN external_system_id DROP DEFAULT;
       arsahub_dev          postgres    false    249    248            �           2604    27266    rule rule_id    DEFAULT     v   ALTER TABLE ONLY arsahub_dev.rule ALTER COLUMN rule_id SET DEFAULT nextval('arsahub_dev.rule_rule_id_seq'::regclass);
 @   ALTER TABLE arsahub_dev.rule ALTER COLUMN rule_id DROP DEFAULT;
       arsahub_dev          postgres    false    255    250            �           2604    27267 ,   rule_progress_streak rule_progress_streak_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.rule_progress_streak ALTER COLUMN rule_progress_streak_id SET DEFAULT nextval('arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq'::regclass);
 `   ALTER TABLE arsahub_dev.rule_progress_streak ALTER COLUMN rule_progress_streak_id DROP DEFAULT;
       arsahub_dev          postgres    false    252    251            �           2604    27268 +   rule_progress_times rule_progress_single_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.rule_progress_times ALTER COLUMN rule_progress_single_id SET DEFAULT nextval('arsahub_dev.rule_progress_times_rule_progress_single_id_seq'::regclass);
 _   ALTER TABLE arsahub_dev.rule_progress_times ALTER COLUMN rule_progress_single_id DROP DEFAULT;
       arsahub_dev          postgres    false    254    253            �           2604    27269    trigger trigger_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.trigger ALTER COLUMN trigger_id SET DEFAULT nextval('arsahub_dev.trigger_trigger_id_seq'::regclass);
 F   ALTER TABLE arsahub_dev.trigger ALTER COLUMN trigger_id DROP DEFAULT;
       arsahub_dev          postgres    false    257    256            �           2604    27270    trigger_type trigger_type_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.trigger_type ALTER COLUMN trigger_type_id SET DEFAULT nextval('arsahub_dev.trigger_type_trigger_type_id_seq'::regclass);
 P   ALTER TABLE arsahub_dev.trigger_type ALTER COLUMN trigger_type_id DROP DEFAULT;
       arsahub_dev          postgres    false    259    258            �           2604    27271    user user_id    DEFAULT     x   ALTER TABLE ONLY arsahub_dev."user" ALTER COLUMN user_id SET DEFAULT nextval('arsahub_dev.user_user_id_seq'::regclass);
 B   ALTER TABLE arsahub_dev."user" ALTER COLUMN user_id DROP DEFAULT;
       arsahub_dev          postgres    false    267    260            �           2604    27272    user_activity user_activity_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.user_activity ALTER COLUMN user_activity_id SET DEFAULT nextval('arsahub_dev.user_activity_user_activity_id_seq'::regclass);
 R   ALTER TABLE arsahub_dev.user_activity ALTER COLUMN user_activity_id DROP DEFAULT;
       arsahub_dev          postgres    false    266    261            �           2604    27273 6   user_activity_achievement user_activity_achievement_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.user_activity_achievement ALTER COLUMN user_activity_achievement_id SET DEFAULT nextval('arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq'::regclass);
 j   ALTER TABLE arsahub_dev.user_activity_achievement ALTER COLUMN user_activity_achievement_id DROP DEFAULT;
       arsahub_dev          postgres    false    263    262            �           2604    27274 :   user_activity_point_history user_activity_point_history_id    DEFAULT     �   ALTER TABLE ONLY arsahub_dev.user_activity_point_history ALTER COLUMN user_activity_point_history_id SET DEFAULT nextval('arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq'::regclass);
 n   ALTER TABLE arsahub_dev.user_activity_point_history ALTER COLUMN user_activity_point_history_id DROP DEFAULT;
       arsahub_dev          postgres    false    265    264            �          0    27171    achievement 
   TABLE DATA           ~   COPY arsahub_dev.achievement (achievement_id, title, description, created_at, updated_at, image_url, activity_id) FROM stdin;
    arsahub_dev          postgres    false    242   ��       �          0    27179    action 
   TABLE DATA           n   COPY arsahub_dev.action (effect_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
    arsahub_dev          postgres    false    244   (�       �          0    27186    activity 
   TABLE DATA           t   COPY arsahub_dev.activity (activity_id, title, description, created_at, updated_at, external_system_id) FROM stdin;
    arsahub_dev          postgres    false    245   �       �          0    27195    external_system 
   TABLE DATA           w   COPY arsahub_dev.external_system (external_system_id, title, description, api_key, created_at, updated_at) FROM stdin;
    arsahub_dev          postgres    false    248   o�       �          0    27203    rule 
   TABLE DATA           �   COPY arsahub_dev.rule (rule_id, title, description, activity_id, created_at, updated_at, trigger_id, trigger_type_id, effect_id, trigger_type_params, effect_params, trigger_params) FROM stdin;
    arsahub_dev          postgres    false    250   ��       �          0    27210    rule_progress_streak 
   TABLE DATA           �   COPY arsahub_dev.rule_progress_streak (rule_progress_streak_id, rule_id, user_activity_id, current_streak, last_interaction_at, created_at) FROM stdin;
    arsahub_dev          postgres    false    251   ��       �          0    27215    rule_progress_times 
   TABLE DATA           �   COPY arsahub_dev.rule_progress_times (rule_progress_single_id, rule_id, user_activity_id, progress, created_at, updated_at, completed_at) FROM stdin;
    arsahub_dev          postgres    false    253   ��       �          0    27221    trigger 
   TABLE DATA           }   COPY arsahub_dev.trigger (trigger_id, title, description, activity_id, created_at, updated_at, key, json_schema) FROM stdin;
    arsahub_dev          postgres    false    256   �       �          0    27229    trigger_type 
   TABLE DATA           z   COPY arsahub_dev.trigger_type (trigger_type_id, title, description, json_schema, created_at, updated_at, key) FROM stdin;
    arsahub_dev          postgres    false    258   ��       �          0    27237    user 
   TABLE DATA           |   COPY arsahub_dev."user" (user_id, username, name, external_user_id, external_system_id, created_at, updated_at) FROM stdin;
    arsahub_dev          postgres    false    260   ��       �          0    27244    user_activity 
   TABLE DATA           t   COPY arsahub_dev.user_activity (user_activity_id, user_id, activity_id, points, created_at, updated_at) FROM stdin;
    arsahub_dev          postgres    false    261   �       �          0    27249    user_activity_achievement 
   TABLE DATA           �   COPY arsahub_dev.user_activity_achievement (user_activity_achievement_id, achievement_id, user_activity_id, completed_at, created_at, updated_at) FROM stdin;
    arsahub_dev          postgres    false    262   V�       �          0    27255    user_activity_point_history 
   TABLE DATA           �   COPY arsahub_dev.user_activity_point_history (user_activity_point_history_id, user_activity_id, activity_id, points, created_at, description) FROM stdin;
    arsahub_dev          postgres    false    264   ��       �           0    0    achievement_achievement_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('arsahub_dev.achievement_achievement_id_seq', 2, true);
          arsahub_dev          postgres    false    243            �           0    0    achievement_activity_id_seq    SEQUENCE SET     O   SELECT pg_catalog.setval('arsahub_dev.achievement_activity_id_seq', 1, false);
          arsahub_dev          postgres    false    268            �           0    0    activity_activity_id_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('arsahub_dev.activity_activity_id_seq', 1, true);
          arsahub_dev          postgres    false    246            �           0    0    effect_effect_id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('arsahub_dev.effect_effect_id_seq', 2, true);
          arsahub_dev          postgres    false    247            �           0    0 &   external_system_external_system_id_seq    SEQUENCE SET     Z   SELECT pg_catalog.setval('arsahub_dev.external_system_external_system_id_seq', 1, false);
          arsahub_dev          postgres    false    249            �           0    0 0   rule_progress_streak_rule_progress_streak_id_seq    SEQUENCE SET     d   SELECT pg_catalog.setval('arsahub_dev.rule_progress_streak_rule_progress_streak_id_seq', 1, false);
          arsahub_dev          postgres    false    252            �           0    0 /   rule_progress_times_rule_progress_single_id_seq    SEQUENCE SET     c   SELECT pg_catalog.setval('arsahub_dev.rule_progress_times_rule_progress_single_id_seq', 67, true);
          arsahub_dev          postgres    false    254            �           0    0    rule_rule_id_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('arsahub_dev.rule_rule_id_seq', 25, true);
          arsahub_dev          postgres    false    255            �           0    0    trigger_trigger_id_seq    SEQUENCE SET     I   SELECT pg_catalog.setval('arsahub_dev.trigger_trigger_id_seq', 8, true);
          arsahub_dev          postgres    false    257            �           0    0     trigger_type_trigger_type_id_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('arsahub_dev.trigger_type_trigger_type_id_seq', 1, true);
          arsahub_dev          postgres    false    259            �           0    0 :   user_activity_achievement_user_activity_achievement_id_seq    SEQUENCE SET     m   SELECT pg_catalog.setval('arsahub_dev.user_activity_achievement_user_activity_achievement_id_seq', 2, true);
          arsahub_dev          postgres    false    263            �           0    0 >   user_activity_point_history_user_activity_point_history_id_seq    SEQUENCE SET     r   SELECT pg_catalog.setval('arsahub_dev.user_activity_point_history_user_activity_point_history_id_seq', 1, false);
          arsahub_dev          postgres    false    265            �           0    0 "   user_activity_user_activity_id_seq    SEQUENCE SET     U   SELECT pg_catalog.setval('arsahub_dev.user_activity_user_activity_id_seq', 3, true);
          arsahub_dev          postgres    false    266            �           0    0    user_user_id_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('arsahub_dev.user_user_id_seq', 1, true);
          arsahub_dev          postgres    false    267            �           2606    27407    action action_un 
   CONSTRAINT     O   ALTER TABLE ONLY arsahub_dev.action
    ADD CONSTRAINT action_un UNIQUE (key);
 ?   ALTER TABLE ONLY arsahub_dev.action DROP CONSTRAINT action_un;
       arsahub_dev            postgres    false    244            �           2606    27276    achievement idx_16391_primary 
   CONSTRAINT     l   ALTER TABLE ONLY arsahub_dev.achievement
    ADD CONSTRAINT idx_16391_primary PRIMARY KEY (achievement_id);
 L   ALTER TABLE ONLY arsahub_dev.achievement DROP CONSTRAINT idx_16391_primary;
       arsahub_dev            postgres    false    242            �           2606    27278 +   user_activity_achievement idx_16400_primary 
   CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT idx_16400_primary PRIMARY KEY (user_activity_achievement_id);
 Z   ALTER TABLE ONLY arsahub_dev.user_activity_achievement DROP CONSTRAINT idx_16400_primary;
       arsahub_dev            postgres    false    262            �           2606    27280    activity idx_16407_primary 
   CONSTRAINT     f   ALTER TABLE ONLY arsahub_dev.activity
    ADD CONSTRAINT idx_16407_primary PRIMARY KEY (activity_id);
 I   ALTER TABLE ONLY arsahub_dev.activity DROP CONSTRAINT idx_16407_primary;
       arsahub_dev            postgres    false    245            �           2606    27282    action idx_16416_primary 
   CONSTRAINT     b   ALTER TABLE ONLY arsahub_dev.action
    ADD CONSTRAINT idx_16416_primary PRIMARY KEY (effect_id);
 G   ALTER TABLE ONLY arsahub_dev.action DROP CONSTRAINT idx_16416_primary;
       arsahub_dev            postgres    false    244            �           2606    27284    user_activity idx_16425_primary 
   CONSTRAINT     p   ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT idx_16425_primary PRIMARY KEY (user_activity_id);
 N   ALTER TABLE ONLY arsahub_dev.user_activity DROP CONSTRAINT idx_16425_primary;
       arsahub_dev            postgres    false    261            �           2606    27286 -   user_activity_point_history idx_16432_primary 
   CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT idx_16432_primary PRIMARY KEY (user_activity_point_history_id);
 \   ALTER TABLE ONLY arsahub_dev.user_activity_point_history DROP CONSTRAINT idx_16432_primary;
       arsahub_dev            postgres    false    264            �           2606    27288    rule idx_16438_primary 
   CONSTRAINT     ^   ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT idx_16438_primary PRIMARY KEY (rule_id);
 E   ALTER TABLE ONLY arsahub_dev.rule DROP CONSTRAINT idx_16438_primary;
       arsahub_dev            postgres    false    250            �           2606    27290 &   rule_progress_streak idx_16456_primary 
   CONSTRAINT     ~   ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT idx_16456_primary PRIMARY KEY (rule_progress_streak_id);
 U   ALTER TABLE ONLY arsahub_dev.rule_progress_streak DROP CONSTRAINT idx_16456_primary;
       arsahub_dev            postgres    false    251            �           2606    27292 %   rule_progress_times idx_16462_primary 
   CONSTRAINT     }   ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT idx_16462_primary PRIMARY KEY (rule_progress_single_id);
 T   ALTER TABLE ONLY arsahub_dev.rule_progress_times DROP CONSTRAINT idx_16462_primary;
       arsahub_dev            postgres    false    253            �           2606    27294    trigger idx_16468_primary 
   CONSTRAINT     d   ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT idx_16468_primary PRIMARY KEY (trigger_id);
 H   ALTER TABLE ONLY arsahub_dev.trigger DROP CONSTRAINT idx_16468_primary;
       arsahub_dev            postgres    false    256            �           2606    27296    trigger_type idx_16483_primary 
   CONSTRAINT     n   ALTER TABLE ONLY arsahub_dev.trigger_type
    ADD CONSTRAINT idx_16483_primary PRIMARY KEY (trigger_type_id);
 M   ALTER TABLE ONLY arsahub_dev.trigger_type DROP CONSTRAINT idx_16483_primary;
       arsahub_dev            postgres    false    258            �           2606    27298    user idx_16492_primary 
   CONSTRAINT     `   ALTER TABLE ONLY arsahub_dev."user"
    ADD CONSTRAINT idx_16492_primary PRIMARY KEY (user_id);
 G   ALTER TABLE ONLY arsahub_dev."user" DROP CONSTRAINT idx_16492_primary;
       arsahub_dev            postgres    false    260            �           2606    27300 !   external_system idx_16500_primary 
   CONSTRAINT     t   ALTER TABLE ONLY arsahub_dev.external_system
    ADD CONSTRAINT idx_16500_primary PRIMARY KEY (external_system_id);
 P   ALTER TABLE ONLY arsahub_dev.external_system DROP CONSTRAINT idx_16500_primary;
       arsahub_dev            postgres    false    248            �           2606    27409    trigger_type trigger_type_un 
   CONSTRAINT     [   ALTER TABLE ONLY arsahub_dev.trigger_type
    ADD CONSTRAINT trigger_type_un UNIQUE (key);
 K   ALTER TABLE ONLY arsahub_dev.trigger_type DROP CONSTRAINT trigger_type_un;
       arsahub_dev            postgres    false    258            �           2606    27405    trigger trigger_un 
   CONSTRAINT     Q   ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT trigger_un UNIQUE (key);
 A   ALTER TABLE ONLY arsahub_dev.trigger DROP CONSTRAINT trigger_un;
       arsahub_dev            postgres    false    256            �           1259    27301    idx_16400_achievement_id    INDEX     m   CREATE INDEX idx_16400_achievement_id ON arsahub_dev.user_activity_achievement USING btree (achievement_id);
 1   DROP INDEX arsahub_dev.idx_16400_achievement_id;
       arsahub_dev            postgres    false    262            �           1259    27302    idx_16425_activity_id    INDEX     [   CREATE INDEX idx_16425_activity_id ON arsahub_dev.user_activity USING btree (activity_id);
 .   DROP INDEX arsahub_dev.idx_16425_activity_id;
       arsahub_dev            postgres    false    261            �           1259    27303    idx_16425_user_id    INDEX     S   CREATE INDEX idx_16425_user_id ON arsahub_dev.user_activity USING btree (user_id);
 *   DROP INDEX arsahub_dev.idx_16425_user_id;
       arsahub_dev            postgres    false    261            �           1259    27304    idx_16432_activity_id    INDEX     i   CREATE INDEX idx_16432_activity_id ON arsahub_dev.user_activity_point_history USING btree (activity_id);
 .   DROP INDEX arsahub_dev.idx_16432_activity_id;
       arsahub_dev            postgres    false    264            �           1259    27305    idx_16432_user_activity_id    INDEX     s   CREATE INDEX idx_16432_user_activity_id ON arsahub_dev.user_activity_point_history USING btree (user_activity_id);
 3   DROP INDEX arsahub_dev.idx_16432_user_activity_id;
       arsahub_dev            postgres    false    264            �           1259    27306    idx_16438_activity_id    INDEX     R   CREATE INDEX idx_16438_activity_id ON arsahub_dev.rule USING btree (activity_id);
 .   DROP INDEX arsahub_dev.idx_16438_activity_id;
       arsahub_dev            postgres    false    250            �           1259    27307    idx_16438_effect_id    INDEX     N   CREATE INDEX idx_16438_effect_id ON arsahub_dev.rule USING btree (effect_id);
 ,   DROP INDEX arsahub_dev.idx_16438_effect_id;
       arsahub_dev            postgres    false    250            �           1259    27308    idx_16438_trigger_id    INDEX     P   CREATE INDEX idx_16438_trigger_id ON arsahub_dev.rule USING btree (trigger_id);
 -   DROP INDEX arsahub_dev.idx_16438_trigger_id;
       arsahub_dev            postgres    false    250            �           1259    27309    idx_16438_trigger_type_id    INDEX     Z   CREATE INDEX idx_16438_trigger_type_id ON arsahub_dev.rule USING btree (trigger_type_id);
 2   DROP INDEX arsahub_dev.idx_16438_trigger_type_id;
       arsahub_dev            postgres    false    250            �           1259    27310    idx_16456_rule_id    INDEX     Z   CREATE INDEX idx_16456_rule_id ON arsahub_dev.rule_progress_streak USING btree (rule_id);
 *   DROP INDEX arsahub_dev.idx_16456_rule_id;
       arsahub_dev            postgres    false    251            �           1259    27311    idx_16456_user_activity_id    INDEX     l   CREATE INDEX idx_16456_user_activity_id ON arsahub_dev.rule_progress_streak USING btree (user_activity_id);
 3   DROP INDEX arsahub_dev.idx_16456_user_activity_id;
       arsahub_dev            postgres    false    251            �           1259    27312    idx_16462_rule_id    INDEX     Y   CREATE INDEX idx_16462_rule_id ON arsahub_dev.rule_progress_times USING btree (rule_id);
 *   DROP INDEX arsahub_dev.idx_16462_rule_id;
       arsahub_dev            postgres    false    253            �           1259    27313    idx_16462_user_activity_id    INDEX     k   CREATE INDEX idx_16462_user_activity_id ON arsahub_dev.rule_progress_times USING btree (user_activity_id);
 3   DROP INDEX arsahub_dev.idx_16462_user_activity_id;
       arsahub_dev            postgres    false    253            �           1259    27314    idx_16468_activity_id    INDEX     U   CREATE INDEX idx_16468_activity_id ON arsahub_dev.trigger USING btree (activity_id);
 .   DROP INDEX arsahub_dev.idx_16468_activity_id;
       arsahub_dev            postgres    false    256            �           1259    27315    idx_16492_external_user_id    INDEX     e   CREATE UNIQUE INDEX idx_16492_external_user_id ON arsahub_dev."user" USING btree (external_user_id);
 3   DROP INDEX arsahub_dev.idx_16492_external_user_id;
       arsahub_dev            postgres    false    260            �           2606    32778    achievement achievement_fk    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.achievement
    ADD CONSTRAINT achievement_fk FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);
 I   ALTER TABLE ONLY arsahub_dev.achievement DROP CONSTRAINT achievement_fk;
       arsahub_dev          postgres    false    4792    245    242            �           2606    27316    activity activity_fk    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.activity
    ADD CONSTRAINT activity_fk FOREIGN KEY (external_system_id) REFERENCES arsahub_dev.external_system(external_system_id);
 C   ALTER TABLE ONLY arsahub_dev.activity DROP CONSTRAINT activity_fk;
       arsahub_dev          postgres    false    248    4794    245            �           2606    27321    rule rule_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_1 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);
 ?   ALTER TABLE ONLY arsahub_dev.rule DROP CONSTRAINT rule_ibfk_1;
       arsahub_dev          postgres    false    4792    245    250            �           2606    27326    rule rule_ibfk_2    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_2 FOREIGN KEY (trigger_id) REFERENCES arsahub_dev.trigger(trigger_id);
 ?   ALTER TABLE ONLY arsahub_dev.rule DROP CONSTRAINT rule_ibfk_2;
       arsahub_dev          postgres    false    256    4811    250            �           2606    27331    rule rule_ibfk_3    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_3 FOREIGN KEY (trigger_type_id) REFERENCES arsahub_dev.trigger_type(trigger_type_id);
 ?   ALTER TABLE ONLY arsahub_dev.rule DROP CONSTRAINT rule_ibfk_3;
       arsahub_dev          postgres    false    258    4815    250            �           2606    27336    rule rule_ibfk_4    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule
    ADD CONSTRAINT rule_ibfk_4 FOREIGN KEY (effect_id) REFERENCES arsahub_dev.action(effect_id);
 ?   ALTER TABLE ONLY arsahub_dev.rule DROP CONSTRAINT rule_ibfk_4;
       arsahub_dev          postgres    false    4790    250    244            �           2606    27341 0   rule_progress_streak rule_progress_streak_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_1 FOREIGN KEY (rule_id) REFERENCES arsahub_dev.rule(rule_id);
 _   ALTER TABLE ONLY arsahub_dev.rule_progress_streak DROP CONSTRAINT rule_progress_streak_ibfk_1;
       arsahub_dev          postgres    false    4798    250    251            �           2606    27346 0   rule_progress_streak rule_progress_streak_ibfk_2    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule_progress_streak
    ADD CONSTRAINT rule_progress_streak_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);
 _   ALTER TABLE ONLY arsahub_dev.rule_progress_streak DROP CONSTRAINT rule_progress_streak_ibfk_2;
       arsahub_dev          postgres    false    251    261    4823            �           2606    27351 .   rule_progress_times rule_progress_times_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_1 FOREIGN KEY (rule_id) REFERENCES arsahub_dev.rule(rule_id);
 ]   ALTER TABLE ONLY arsahub_dev.rule_progress_times DROP CONSTRAINT rule_progress_times_ibfk_1;
       arsahub_dev          postgres    false    250    4798    253            �           2606    27356 .   rule_progress_times rule_progress_times_ibfk_2    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.rule_progress_times
    ADD CONSTRAINT rule_progress_times_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);
 ]   ALTER TABLE ONLY arsahub_dev.rule_progress_times DROP CONSTRAINT rule_progress_times_ibfk_2;
       arsahub_dev          postgres    false    261    253    4823            �           2606    27361    trigger trigger_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.trigger
    ADD CONSTRAINT trigger_ibfk_1 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);
 E   ALTER TABLE ONLY arsahub_dev.trigger DROP CONSTRAINT trigger_ibfk_1;
       arsahub_dev          postgres    false    4792    256    245            �           2606    27366 :   user_activity_achievement user_activity_achievement_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_1 FOREIGN KEY (achievement_id) REFERENCES arsahub_dev.achievement(achievement_id);
 i   ALTER TABLE ONLY arsahub_dev.user_activity_achievement DROP CONSTRAINT user_activity_achievement_ibfk_1;
       arsahub_dev          postgres    false    262    242    4786            �           2606    27371 :   user_activity_achievement user_activity_achievement_ibfk_2    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity_achievement
    ADD CONSTRAINT user_activity_achievement_ibfk_2 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);
 i   ALTER TABLE ONLY arsahub_dev.user_activity_achievement DROP CONSTRAINT user_activity_achievement_ibfk_2;
       arsahub_dev          postgres    false    4823    262    261            �           2606    27376 "   user_activity user_activity_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT user_activity_ibfk_1 FOREIGN KEY (user_id) REFERENCES arsahub_dev."user"(user_id);
 Q   ALTER TABLE ONLY arsahub_dev.user_activity DROP CONSTRAINT user_activity_ibfk_1;
       arsahub_dev          postgres    false    261    260    4820            �           2606    27381 "   user_activity user_activity_ibfk_2    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity
    ADD CONSTRAINT user_activity_ibfk_2 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);
 Q   ALTER TABLE ONLY arsahub_dev.user_activity DROP CONSTRAINT user_activity_ibfk_2;
       arsahub_dev          postgres    false    4792    245    261            �           2606    27386 >   user_activity_point_history user_activity_point_history_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_1 FOREIGN KEY (user_activity_id) REFERENCES arsahub_dev.user_activity(user_activity_id);
 m   ALTER TABLE ONLY arsahub_dev.user_activity_point_history DROP CONSTRAINT user_activity_point_history_ibfk_1;
       arsahub_dev          postgres    false    264    4823    261            �           2606    27391 >   user_activity_point_history user_activity_point_history_ibfk_2    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev.user_activity_point_history
    ADD CONSTRAINT user_activity_point_history_ibfk_2 FOREIGN KEY (activity_id) REFERENCES arsahub_dev.activity(activity_id);
 m   ALTER TABLE ONLY arsahub_dev.user_activity_point_history DROP CONSTRAINT user_activity_point_history_ibfk_2;
       arsahub_dev          postgres    false    4792    245    264            �           2606    27396    user user_ibfk_1    FK CONSTRAINT     �   ALTER TABLE ONLY arsahub_dev."user"
    ADD CONSTRAINT user_ibfk_1 FOREIGN KEY (external_system_id) REFERENCES arsahub_dev.external_system(external_system_id);
 A   ALTER TABLE ONLY arsahub_dev."user" DROP CONSTRAINT user_ibfk_1;
       arsahub_dev          postgres    false    248    260    4794            �   _   x�3�tL��L-K�M�+Q0���4202�54�50V00�25�25�3�4066�60' �n�e�b����VƦV�z��f�hfb������ 0$�      �   �   x����N�0Eg�+,��خ���,lLUn� )�m����$�����t:-�!��6�hu�4�y��P��B��mYA�GX��U�m[g���.<g&觘0�Ml6��vu 3��^�:����y��v��x}�ڮ�@�}�#��<�,�s"�ʥ�EZ䋜�Kv�G��YOZX�{�s��eU���]�Vn�)��g�s�JȔ]Kɋ����Cw}��W)��)e�6      �   C   x�3�����VH,Q���UH����4202�54�56T02�2��2��35�4�4�60' ������ �|�      �      x������ � �      �   �   x����N�0���SX��F��4i^ qA\��2m��R���C�(Sp����O1��>�� }�[�r���f�c^��R�bd[֖�)Z���5�؜��%�i��z\���ҩ��u���;��r�}*ç�M56@��֛\���S�A�	=\lEA���|�`:�Z�.(��Rp�<�	�� ϻT�� �I^���F<�q��s�;�a��CӰ�Mb1�
I�������R      �      x������ � �      �   <   x����� �w2E�d;2�����Ag�}ow�Q�T�����A/���_+Y��2����      �   �   x����j�  F���nw[�FMac��u]chc��PJ�}��������8y1z�D��N�}2��D%�1�	�J�q���Q��'T�C��[��R��wªkCJ���(X�����1vm�E]�Ef�[�z�Y%�N����=Z(��iM*r����G���; ��0�^�<g���q�yQ���i�Ʈ
iŗ��=�WV}o�U2<� ;��*��њQY���p�v�:o���4�s���Ix>      �   �   x�����0�g�Mu��RA�>���8�sH������Ҡ�������$ѵ�Ө�D3÷�)3U5�#e{_�0���V�{3���=E��r����B��k�4k��j3���+��Xp؁_��!�v�i���eY"%Չ'	��J�OJgi���,U���.�"&�| K?o      �   F   x�3�,-N-2��
�y������?N##c]C]cC##+C+#=33sCms��\1z\\\ �9�      �   E   x�-ɱ�0D�:L�>݁��d�9�"��o^\<9�:�����kg�X?R��`ӭ�=���&"X�      �   5   x�3�4�4���4202�54�50Q02�2��26�3�425�60�+����� ��      �      x������ � �     