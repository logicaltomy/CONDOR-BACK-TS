-- ensure_roles.sql
-- Asegura que existan los roles básicos y no inserta duplicados (case-insensitive)
USE usuarios_db;

INSERT INTO rol (nombre)
SELECT 'Administrador' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE LOWER(nombre) = 'administrador');

INSERT INTO rol (nombre)
SELECT 'Moderador' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE LOWER(nombre) = 'moderador');

INSERT INTO rol (nombre)
SELECT 'Usuario' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE LOWER(nombre) = 'usuario');
