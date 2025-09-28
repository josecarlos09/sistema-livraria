-- V1__create_table.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO tb_role (id, nome) VALUES (gen_random_uuid(), 'ROLE_USUARIO');
INSERT INTO tb_role (id, nome) VALUES (gen_random_uuid(), 'ROLE_ADMINISTRADOR');
