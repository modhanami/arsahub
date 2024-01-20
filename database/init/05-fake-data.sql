INSERT INTO "user" (user_id, username, name, created_at, updated_at, uuid, password, email)
VALUES (1, 'a@a.ab', 'Hoo', '2024-01-06 14:06:56.979224 +00:00', '2024-01-06 14:06:56.979224 +00:00',
        'a3ba848b-84e6-43ca-8d47-c00bfbe3050f',
        '$argon2id$v=19$m=16384,t=2,p=1$+MvHehCryQWDWJuV+nJOfQ$1grg4+RWBmgggHuO8yursAwvMV+kxL3la91Q7S94VZ4', 'a@a.ab');
INSERT INTO app (app_id, title, description, api_key, created_at, updated_at, owner_id)
VALUES (36, 'My App', null, '2ccb0c54-ee82-4025-9bc6-1a92d855c251', '2024-01-06 14:06:56.991283 +00:00',
        '2024-01-06 14:06:56.991283 +00:00', 1);
