--liquibase formatted sql

--changeset elias:002-create-table-animal
CREATE TABLE IF NOT EXISTS animals
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
--rollback DROP TABLE animals;