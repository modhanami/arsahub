-- liquibase formatted sql

-- changeset Rooted:1700983242066-1
ALTER TABLE external_system
    ADD created_by BIGINT NOT NULL;

-- changeset Rooted:1700983242066-2
ALTER TABLE external_system
    ADD CONSTRAINT external_system_user_user_id_fk FOREIGN KEY (created_by) REFERENCES "user" (user_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

