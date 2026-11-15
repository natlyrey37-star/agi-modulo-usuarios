-- =============================================================
-- Proyecto AGI - Usuario de base de datos propio de la aplicación (opcional)
--
-- Aplica el principio de menor privilegio definido en el modelo de datos:
-- la aplicación no se conecta con root, sino con una cuenta que solo puede
-- consultar, insertar, actualizar y eliminar en la base agi, y solo desde
-- el mismo equipo (localhost).
--
-- Uso: ejecutar en MySQL Workbench con la cuenta root, después de agi_bd.sql.
-- Luego, en config/db.properties:  db.usuario=agi_app  y  db.clave=AgiApp2026*
-- =============================================================

CREATE USER IF NOT EXISTS 'agi_app'@'localhost' IDENTIFIED BY 'AgiApp2026*';

GRANT SELECT, INSERT, UPDATE, DELETE ON agi.* TO 'agi_app'@'localhost';

FLUSH PRIVILEGES;
