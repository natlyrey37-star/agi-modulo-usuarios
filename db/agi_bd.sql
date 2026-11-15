-- =============================================================
-- Proyecto AGI - Asistente Guía Interactivo
-- Script de la base de datos "agi" (MySQL 8.0) - versión 3.0
-- Evidencia GA7-220501096-AA3-EV01 - Codificación de módulos del software
--
-- Estructura tomada de la evidencia GA6-220501096-AA2-EV02.
-- Ejecutar completo en MySQL Workbench (rayo "Execute") antes de
-- iniciar la aplicación Spring Boot. ATENCIÓN: borra y vuelve a crear
-- la base de datos agi con los datos iniciales.
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

-- Pasos de cada guía (nuevo en la versión 3.0)
INSERT INTO paso (numero_paso, titulo_paso, instruccion, id_guia) VALUES
  (1, 'Abra la aplicación Teléfono',
      'Busque en la pantalla el ícono verde con forma de auricular y tóquelo una vez.', 1),
  (2, 'Marque el número',
      'Toque los números del teclado hasta escribir el número completo de la persona a la que quiere llamar.', 1),
  (3, 'Inicie la llamada',
      'Toque el botón verde con el auricular. Acerque el celular a su oído y espere a que contesten.', 1),
  (4, 'Termine la llamada',
      'Cuando termine de hablar, toque el botón rojo para colgar.', 1),

  (1, 'Abra la cámara',
      'Busque el ícono con forma de cámara fotográfica y tóquelo una vez.', 2),
  (2, 'Encuadre la imagen',
      'Sostenga el celular con las dos manos y apunte hacia lo que quiere fotografiar. Lo verá en la pantalla.', 2),
  (3, 'Tome la foto',
      'Toque el botón redondo grande de la parte de abajo. Escuchará un sonido parecido al de una cámara.', 2),
  (4, 'Revise su foto',
      'Toque el cuadro pequeño de la esquina inferior para ver la foto que acaba de tomar.', 2),

  (1, 'Abra el navegador',
      'Toque el ícono del navegador, por ejemplo Chrome, que tiene forma de círculo de colores.', 3),
  (2, 'Toque la barra de búsqueda',
      'Toque la barra blanca de la parte de arriba. En la pantalla aparecerá el teclado.', 3),
  (3, 'Escriba lo que busca',
      'Escriba con el teclado la palabra o la pregunta que quiere buscar.', 3),
  (4, 'Abra un resultado',
      'Toque el botón de buscar del teclado y luego toque el título del resultado que le interese.', 3);

-- Cuenta de administración para el primer inicio de sesión (nuevo en la versión 3.0)
-- Correo: admin@agi.com   Contraseña: Agi2026*   (guardada con hash BCrypt)
INSERT INTO usuario (nombres, apellidos, cedula, correo, contrasena, fecha_nacimiento) VALUES
  ('Administrador', 'AGI', '1000000001', 'admin@agi.com',
   '$2b$10$451EkyLezk6BVXSwq4u2bOTpd0h2Cd2yO.KRra27iKDcXnukoKXJe', '1990-01-01');
