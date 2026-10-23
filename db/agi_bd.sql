-- =============================================================
-- Proyecto AGI - Asistente Guía Interactivo
-- Script de la base de datos "agi" (MySQL 8.0)
-- Evidencia GA7-220501096-AA2-EV01 - Codificación de módulos del software
--
-- Estructura tomada de la evidencia GA6-220501096-AA2-EV02.
-- Ejecutar completo en MySQL Workbench (rayo "Execute") antes
-- de correr el módulo Java.
-- =============================================================

DROP DATABASE IF EXISTS agi;
CREATE DATABASE agi
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;

USE agi;

-- -------------------------------------------------------------
-- Tabla usuario: personas registradas en la aplicación
-- -------------------------------------------------------------
CREATE TABLE usuario (
  id_usuario       INT          NOT NULL AUTO_INCREMENT,
  nombres          VARCHAR(60)  NOT NULL,
  apellidos        VARCHAR(60)  NOT NULL,
  cedula           VARCHAR(15)  NOT NULL,
  correo           VARCHAR(100) NOT NULL,
  contrasena       VARCHAR(255) NOT NULL,
  fecha_nacimiento DATE         NOT NULL,
  CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
  CONSTRAINT uq_usuario_cedula UNIQUE (cedula),
  CONSTRAINT uq_usuario_correo UNIQUE (correo)
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Tabla categoria: tipo de guía
-- -------------------------------------------------------------
CREATE TABLE categoria (
  id_categoria     INT         NOT NULL AUTO_INCREMENT,
  nombre_categoria VARCHAR(60) NOT NULL,
  CONSTRAINT pk_categoria PRIMARY KEY (id_categoria)
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Tabla guia: guías disponibles en la aplicación
-- -------------------------------------------------------------
CREATE TABLE guia (
  id_guia      INT          NOT NULL AUTO_INCREMENT,
  titulo       VARCHAR(100) NOT NULL,
  descripcion  VARCHAR(255) NULL,
  id_categoria INT          NOT NULL,
  CONSTRAINT pk_guia PRIMARY KEY (id_guia),
  CONSTRAINT fk_guia_categoria FOREIGN KEY (id_categoria)
    REFERENCES categoria (id_categoria)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Tabla paso: pasos ordenados de cada guía
-- -------------------------------------------------------------
CREATE TABLE paso (
  id_paso     INT          NOT NULL AUTO_INCREMENT,
  numero_paso INT          NOT NULL,
  titulo_paso VARCHAR(100) NOT NULL,
  instruccion VARCHAR(255) NOT NULL,
  id_guia     INT          NOT NULL,
  CONSTRAINT pk_paso PRIMARY KEY (id_paso),
  CONSTRAINT chk_paso_numero CHECK (numero_paso > 0),
  CONSTRAINT fk_paso_guia FOREIGN KEY (id_guia)
    REFERENCES guia (id_guia)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Tabla progreso: avance de cada usuario en cada guía
-- -------------------------------------------------------------
CREATE TABLE progreso (
  id_progreso INT         NOT NULL AUTO_INCREMENT,
  id_usuario  INT         NOT NULL,
  id_guia     INT         NOT NULL,
  estado      VARCHAR(20) NOT NULL DEFAULT 'pendiente',
  fecha       DATE        NOT NULL,
  CONSTRAINT pk_progreso PRIMARY KEY (id_progreso),
  CONSTRAINT chk_progreso_estado
    CHECK (estado IN ('pendiente', 'en curso', 'completada')),
  CONSTRAINT fk_progreso_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_progreso_guia FOREIGN KEY (id_guia)
    REFERENCES guia (id_guia)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Datos iniciales de catálogo
-- -------------------------------------------------------------
INSERT INTO categoria (nombre_categoria) VALUES
  ('comunicación'), ('multimedia'), ('conectividad');

INSERT INTO guia (titulo, descripcion, id_categoria) VALUES
  ('Hacer una llamada', 'Aprende a marcar un número y llamar a un contacto.', 1),
  ('Tomar una foto', 'Aprende a abrir la cámara y tomar una fotografía.', 2),
  ('Navegar por internet', 'Aprende a abrir el navegador y buscar información.', 3);
