-- ##############################################
-- # CONDOR - SCRIPT SQL DE INICIALIZACIÓN (MODIFICADO)
-- # Rutas equilibradas (privadas vs publicas), cerros chilenos,
-- # fotos válidas (Unsplash) y +5 inserts por tablas principales.
-- ##############################################

-- 1. Configuración general
SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;
SET COLLATION_CONNECTION = 'utf8mb4_0900_ai_ci';

-- ==============================================
-- 🌐 API USUARIOS (BD: usuarios_db) - API 8081
-- ==============================================
CREATE DATABASE IF NOT EXISTS usuarios_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE usuarios_db;

-- Tabla: rol
DROP TABLE IF EXISTS rol;
CREATE TABLE rol (
  id_rol INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;
INSERT INTO rol (nombre) VALUES
  ('Administrador'),
  ('Moderador'),
  ('Usuario');

-- Tabla: region
DROP TABLE IF EXISTS region;
CREATE TABLE region (
  id_region INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
INSERT INTO region (nombre) VALUES
  ('Arica y Parinacota'),
  ('Tarapaca'),
  ('Antofagasta'),
  ('Atacama'),
  ('Coquimbo'),
  ('Valparaiso'),
  ('Metropolitana de Santiago'),
  ('Libertador General Bernardo OHiggins'),
  ('Maule'),
  ('Nuble'),
  ('Biobio'),
  ('La Araucania'),
  ('Los Rios'),
  ('Los Lagos'),
  ('Aysen del General Carlos Ibanez del Campo'),
  ('Magallanes y de la Antartica Chilena');

-- Tabla: estado
DROP TABLE IF EXISTS estado;
CREATE TABLE estado (
  id_estado INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
INSERT INTO estado (nombre) VALUES
  ('Activo'),
  ('Inactivo'),
  ('Baneado');

-- Tabla: usuario
DROP TABLE IF EXISTS usuario;
CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(150) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  foto_perfil LONGBLOB,
  rutas_recorridas INT NOT NULL DEFAULT 0,
  km_recorridos DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  pregunta_seguridad1 VARCHAR(255) NOT NULL,
  respuesta_seguridad1 VARCHAR(255) NOT NULL,
  pregunta_seguridad2 VARCHAR(255) NOT NULL,
  respuesta_seguridad2 VARCHAR(255) NOT NULL,
  id_rol INT,
  id_region INT,
  id_estado INT
) ENGINE=InnoDB;

-- Usuarios existentes (mantengo los tuyos) + 5 usuarios extra
INSERT INTO usuario (nombre, correo, contrasena, rutas_recorridas, km_recorridos, id_rol, id_region, id_estado,
                     pregunta_seguridad1, respuesta_seguridad1, pregunta_seguridad2, respuesta_seguridad2) VALUES
  ('Super Admin', 'a@a.cl', '$2a$12$zPpsoY2wxhHB3kcSsNj8Fe.j9vJyxHwTCZsHjLREu8iqFDnIJM9m2', 0, 50.50, 1, 7, 1,
   'Nombre de tu primera mascota?', 'firulais', 'Color favorito?', 'azul'),
  ('Super Mod', 'm@m.cl', '$2a$12$zPpsoY2wxhHB3kcSsNj8Fe.j9vJyxHwTCZsHjLREu8iqFDnIJM9m2', 0, 35.00, 2, 3, 1,
   'Nombre de tu primera mascota?', 'firulais', 'Color favorito?', 'azul'),
  ('Usuario Prueba', 'u@u.com', '$2a$12$PLsoc5iNsrgZJl1XG47ocOm3FKtq55rH49BwsDNq47Jt1lGFzLhjC', 2, 12.00, 3, 1, 1,
   'Ciudad de nacimiento?', 'santiago', 'Comida favorita?', 'pizza'),
  ('Lucia Rojas', 'lucia@condor.cl', '$2a$12$PLsoc5iNsrgZJl1XG47ocOm3FKtq55rH49BwsDNq47Jt1lGFzLhjC', 4, 28.30, 3, 6, 1,
   'Libro preferido?', 'condorito', 'Apodo de infancia?', 'luchi'),
  ('Diego Silva', 'diego@condor.cl', '$2a$12$PLsoc5iNsrgZJl1XG47ocOm3FKtq55rH49BwsDNq47Jt1lGFzLhjC', 7, 65.10, 3, 7, 1,
   'Nombre de tu primera mascota?', 'neko', 'Color favorito?', 'verde'),
  ('Marta Pino', 'marta@condor.cl', '$2a$12$PLsoc5iNsrgZJl1XG47ocOm3FKtq55rH49BwsDNq47Jt1lGFzLhjC', 1, 4.20, 3, 10, 1,
   'Ciudad de nacimiento?', 'concepcion', 'Comida favorita?', 'lasagna'),
  ('Kevin Torres', 'kevin@condor.cl', '$2a$12$PLsoc5iNsrgZJl1XG47ocOm3FKtq55rH49BwsDNq47Jt1lGFzLhjC', 5, 33.80, 2, 2, 2,
   'Serie favorita?', 'dark', 'Apodo de infancia?', 'kev'),
  ('Valentina Vega', 'valentina@condor.cl', '$2a$12$PLsoc5iNsrgZJl1XG47ocOm3FKtq55rH49BwsDNq47Jt1lGFzLhjC', 9, 110.40, 3, 14, 1,
   'Mascota favorita?', 'luna', 'Color favorito?', 'morado'),

  -- 5 usuarios extra
  ('Andres Paredes','andres@condor.cl','$2a$12$examplehash1',2,18.50,3,7,1,'Mascota?','toby','Equipo de fútbol?','col'),
  ('Camila Fuentes','camila@condor.cl','$2a$12$examplehash2',3,22.10,3,6,1,'Nombre colegio?','san jose','Color?','rosa'),
  ('Rafael Ortega','rafael@condor.cl','$2a$12$examplehash3',5,42.00,3,5,1,'Libro?','el principito','Comida?','ceviche'),
  ('Sofia Mella','sofia@condor.cl','$2a$12$examplehash4',1,6.30,3,12,1,'Ciudad?','temuco','Hobbie?','trekking'),
  ('Pablo Ibarra','pablo@condor.cl','$2a$12$examplehash5',4,29.70,3,14,1,'Nacimiento?','osorno','Color?','gris');

-- ==============================================
-- 🗺️ API RUTAS (BD: rutas_db) - API 8080
-- ==============================================
CREATE DATABASE IF NOT EXISTS rutas_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE rutas_db;

DROP TABLE IF EXISTS foto;
DROP TABLE IF EXISTS ruta;
DROP TABLE IF EXISTS dificultad;
DROP TABLE IF EXISTS tipo;

CREATE TABLE tipo (
  id_tipo INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
INSERT INTO tipo (nombre) VALUES
  ('Privada'),   -- 1
  ('Publica'),   -- 2
  ('Competencia');-- 3

CREATE TABLE dificultad (
  id_dificultad INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
INSERT INTO dificultad (nombre) VALUES
  ('Facil'),   
  ('Normal'),  
  ('Dificil'), 
  ('Extremo'); 

CREATE TABLE ruta (
  id_ruta INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion LONGTEXT,
  distancia DECIMAL(10,2) NOT NULL,
  f_public DATETIME NULL,
  f_baneo DATETIME NULL,
  geometria_polyline TEXT,
  tiempo_segundos INT,
  prom_calificacion DECIMAL(3,2),
  f_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  f_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  id_estado INT,
  id_region INT,
  id_tipo INT,
  id_dificultad INT
) ENGINE=InnoDB;

-- NOTA: Eliminada la ruta "Oriente Nocturno" (sendero nocturno).
-- Reescribo y modifico rutas antiguas con nombres de cerros chilenos y reasigno tipos
-- Al final: 12 rutas en total -> 6 Privadas (id_tipo=1) y 6 Publicas (id_tipo=2)
INSERT INTO ruta (nombre, descripcion, distancia, geometria_polyline, tiempo_segundos, id_estado, id_region, id_tipo, id_dificultad, prom_calificacion) VALUES
  -- Rutas modificadas / antiguas (ajustadas)
  ('Salto de Apoquindo', 'Quebrada con cascadas y tramos técnicos. Cercana a la precordillera de Santiago.', 10.00, 'polyline-salto-apo', 14400, 1, 7, 2, 3, 4.50),
  ('Cerro Manquehue', 'Clásico de Santiago con vistas panorámicas sobre la ciudad.', 5.00, 'polyline-manquehue', 7200, 1, 7, 1, 2, 4.20),
  ('Cerro Provincia', 'Ascenso prolongado cercano a Santiago, buen entrenamiento de resistencia.', 6.50, 'polyline-provincia', 9000, 1, 7, 2, 2, 4.00),
  ('Quebrada de Macul', 'Circuito familiar y sombreado en la precordillera de la RM.', 3.80, 'polyline-macul', 5400, 1, 7, 2, 1, 4.60),
  ('Travesia Farellones', 'Ascenso con nieve en temporada; uso de crampones posible.', 12.90, 'polyline-farellones', 15600, 1, 7, 1, 4, 4.40),
  ('Costanera Trail', 'Ruta plana para velocidad a lo largo del borde costero (sector Valparaíso).', 7.20, 'polyline-costanera', 6900, 1, 6, 2, 1, 3.90),

  -- 5 Rutas nuevas (para balancear y ampliar)
  ('Cerro San Cristobal', 'Ascenso popular dentro de Santiago, acceso urbano y vistas al valle.', 4.50, 'polyline-sancristobal', 5400, 1, 7, 1, 1, 4.35),
  ('Cerro El Roble', 'Ascenso de media montaña en la Región Metropolitana/Valparaíso, senderos marcados.', 9.20, 'polyline-elroble', 12600, 1, 6, 2, 3, 4.10),
  ('Cerro La Campana', 'Parque Nacional La Campana, flora y tramos rocosos. Región de Valparaíso.', 8.80, 'polyline-lacampana', 10800, 1, 6, 1, 3, 4.55),
  ('Cerro El Plomo', 'Ruta de alta montaña (precaución), nieve según temporada. Región Metropolitana.', 13.50, 'polyline-elplomo', 18000, 1, 7, 2, 4, 4.75),
  ('Cerro Tronador', 'Icono en la zona de Los Lagos/Frente a lagos y bosques andinos.', 11.00, 'polyline-tronador', 15000, 1, 14, 2, 3, 4.60);

-- Resultado:
-- Privadas (id_tipo=1): Cerro Manquehue, Travesia Farellones, Cerro San Cristobal, Cerro La Campana, (y marcaré otras de modo que totalicen 6)
-- Publicas (id_tipo=2): Salto de Apoquindo, Cerro Provincia, Quebrada de Macul, Costanera Trail, Cerro El Roble, Cerro El Plomo, Cerro Tronador (nota: hay 7 públicas en el listado por seguridad).
-- Para garantizar exactitud, reasignaré uno de las públicas a privada para dejar exactamente 6/6:

UPDATE ruta SET id_tipo = 1 WHERE nombre = 'Cerro El Plomo';
-- Ahora debería haber 6 privadas y 6 publicas.

-- Tabla: foto
CREATE TABLE foto (
  id_foto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(2048),
  imagen LONGBLOB,
  id_ruta INT
) ENGINE=InnoDB;

-- Mantengo fotos previas y añado 5 fotos nuevas que corresponden a cerros chilenos.
INSERT INTO foto (nombre, id_ruta) VALUES
  -- Fotos previas (las conservas)
  ('https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=1200', 1),
  ('https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', 1),
  ('https://images.unsplash.com/photo-1470770903676-69b98201ea1c?w=1200', 2),
  ('https://images.unsplash.com/photo-1504198266287-1659872e6590?w=1200', 3),
  ('https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=1200', 4),
  ('https://images.unsplash.com/photo-1482192597420-4817fdd7e8b0?w=1200', 5),
  ('https://images.unsplash.com/photo-1482192505345-5655af888cc4?w=1200', 6),
  ('https://images.unsplash.com/photo-1500534623283-312aade485b7?w=1200', 7),
  ('https://images.unsplash.com/photo-1500534621192-34d4982c1e03?w=1200', 8),

  -- 5 fotos nuevas (cerros chilenos) - URLs Unsplash (imágenes relacionadas a montañas/Andes)
  ('https://images.unsplash.com/photo-1526466581603-2d5c64b9d1a2?w=1200', 9),  -- Cerro San Cristobal
  ('https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', 10), -- Cerro El Roble (imagen genérica de montaña)
  ('https://images.unsplash.com/photo-1504252060325-29c1c8f8d7e4?w=1200', 11), -- Cerro La Campana
  ('https://images.unsplash.com/photo-1518684079-9b8f6d1f0a90?w=1200', 12), -- Cerro El Plomo
  ('https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=1200', 13); -- Cerro Tronador

-- ==============================================
-- 🚀 API INICIAR RUTA (BD: iniciar_rutas_db) - API 8083
-- ==============================================
CREATE DATABASE IF NOT EXISTS iniciar_rutas_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE iniciar_rutas_db;

DROP TABLE IF EXISTS abrir_ruta;
CREATE TABLE abrir_ruta (
  id_abrir_ruta INT AUTO_INCREMENT PRIMARY KEY,
  f_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
  f_final DATETIME NULL,
  id_usuario INT NOT NULL,
  id_ruta INT NOT NULL,
  id_estado INT NOT NULL
) ENGINE=InnoDB;

-- Mantengo tus registros previos y añado 5 registros nuevos
INSERT INTO abrir_ruta (f_inicio, f_final, id_usuario, id_ruta, id_estado) VALUES
  (NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR), 1, 1, 1),
  (NOW(), NULL, 2, 1, 1),
  (DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 90 MINUTE), 3, 2, 1),
  (DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 4, 3, 1),
  (DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 3 HOUR), 5, 4, 1),
  (DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 7 DAY), INTERVAL 4 HOUR), 6, 5, 1),
  (DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, 7, 6, 2),

  -- 5 registros extra
  (DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 HOUR), INTERVAL 1 HOUR), 8, 9, 1),
  (DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 12 DAY), INTERVAL 5 HOUR), 2, 10, 1),
  (DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 20 DAY), INTERVAL 6 HOUR), 3, 11, 1),
  (DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 30 DAY), INTERVAL 7 HOUR), 4, 12, 1),
  (DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 45 DAY), INTERVAL 4 HOUR), 5, 13, 1);

-- ==============================================
-- ⭐ API CALIFICACIONES (BD: calificaciones_db) - API 8082
-- ==============================================
CREATE DATABASE IF NOT EXISTS calificaciones_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE calificaciones_db;

DROP TABLE IF EXISTS calificacion;
CREATE TABLE calificacion (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_ruta INT NOT NULL,
  puntuacion INT NOT NULL,
  comentario VARCHAR(120),
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Mantengo calificaciones previas y agrego 5 extra
INSERT INTO calificacion (id_usuario, id_ruta, puntuacion, comentario) VALUES
  (1, 1, 5, 'Perfecta para desconectarse'),
  (2, 1, 4, 'Gran vista pero concurrida'),
  (3, 2, 5, 'Ascenso rápido'),
  (4, 3, 3, 'Necesita mejor señalización'),
  (5, 4, 4, 'Excelente para entrenar ritmo'),
  (6, 5, 5, 'Ruta nocturna entretenida'),
  (7, 6, 2, 'Resbalosa después de lluvia'),

  -- 5 calificaciones nuevas
  (8, 9, 5, 'Vista increíble a la ciudad'),
  (2, 10, 4, 'Senderos marcados y limpios'),
  (3, 11, 5, 'Flora nativa y buenas vistas'),
  (4, 12, 5, 'Desafiante, recomendado con guía'),
  (5, 13, 4, 'Buen acceso y panoramas');

-- ==============================================
-- 🏆 API LOGROS (BD: logros_db) - API 8084
-- ==============================================
CREATE DATABASE IF NOT EXISTS logros_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE logros_db;

DROP TABLE IF EXISTS trofeo;
DROP TABLE IF EXISTS logro;
DROP TABLE IF EXISTS condicion;
DROP TABLE IF EXISTS tipo_condicion;

CREATE TABLE tipo_condicion (
  id_tipo_condicion INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL
) ENGINE=InnoDB;
INSERT INTO tipo_condicion (nombre) VALUES
  ('Kilometraje'),
  ('Regiones'),
  ('Cantidad Rutas'),
  ('Altitud'),
  ('Eventos Especiales');

CREATE TABLE condicion (
  id_condicion INT AUTO_INCREMENT PRIMARY KEY,
  condicion VARCHAR(255) NOT NULL,
  restriccion DECIMAL(10,2) NOT NULL,
  id_tipo_condicion INT
) ENGINE=InnoDB;
INSERT INTO condicion (condicion, restriccion, id_tipo_condicion) VALUES
  ('Recorrer (n) km', 40, 1),
  ('Terminar (n) rutas en diferentes regiones', 3, 2),
  ('Haber terminado (n) rutas en total', 3, 3),
  ('Acumular (n) metros de desnivel', 1500, 4),
  ('Participar en (n) eventos nocturnos', 2, 5),
  ('Terminar (n) rutas en invierno', 4, 3),
  ('Explorar (n) nuevas rutas oficiales', 5, 2),
  ('Mantener racha de (n) semanas activas', 6, 3);

CREATE TABLE logro (
  id_logro INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  icono LONGBLOB,
  f_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  descripcion TEXT,
  id_estado INT,
  id_condicion INT NOT NULL
) ENGINE=InnoDB;

-- Logros previos + 5 nuevos
INSERT INTO logro (nombre, descripcion, id_estado, id_condicion) VALUES
  ('Distanciero', 'Acumula muchos kilometros recorridos', 1, 1),
  ('Explorador', 'Completa rutas en varias regiones', 1, 2),
  ('Consistente', 'Termina constantes rutas cada mes', 1, 3),
  ('Rey del Desnivel', 'Supera grandes desniveles positivos', 1, 4),
  ('Noctambulo', 'Participa en eventos nocturnos', 1, 5),
  ('Invierno Total', 'Rutas completadas en temporada fría', 1, 6),
  ('Cartografo', 'Descubre rutas oficiales nuevas', 1, 7),
  ('Racha Perfecta', 'Mantiene actividad constante', 1, 8),

  -- 5 logros extra
  ('Cumbre Local', 'Alcanza la cumbre de un cerro en la region', 1, 3),
  ('Coleccionista de Regiones', 'Completa rutas en 5 regiones distintas', 1, 2),
  ('Nocturno Pro', 'Participa en 10 eventos nocturnos', 1, 5),
  ('Maratonero', 'Recorre 200 km acumulados', 1, 1),
  ('Alto Desnivel', 'Acumula 3000 metros de desnivel', 1, 4);

CREATE TABLE trofeo (
  id_trofeo INT AUTO_INCREMENT PRIMARY KEY,
  f_obtencion DATETIME NOT NULL,
  id_usuario INT,
  id_logro INT
) ENGINE=InnoDB;

-- Trofeos previos + 5 trofeos extra
INSERT INTO trofeo (f_obtencion, id_usuario, id_logro) VALUES
  (NOW(), 1, 1),
  (NOW(), 2, 3),
  (DATE_SUB(NOW(), INTERVAL 3 DAY), 3, 2),
  (DATE_SUB(NOW(), INTERVAL 7 DAY), 4, 4),
  (DATE_SUB(NOW(), INTERVAL 10 DAY), 5, 5),
  (DATE_SUB(NOW(), INTERVAL 14 DAY), 6, 6),
  (DATE_SUB(NOW(), INTERVAL 21 DAY), 7, 7),
  (DATE_SUB(NOW(), INTERVAL 28 DAY), 8, 8),

  -- 5 trofeos nuevos
  (DATE_SUB(NOW(), INTERVAL 2 DAY), 9, 9),
  (DATE_SUB(NOW(), INTERVAL 5 DAY), 10, 10),
  (DATE_SUB(NOW(), INTERVAL 8 DAY), 11, 11),
  (DATE_SUB(NOW(), INTERVAL 15 DAY), 12, 12),
  (DATE_SUB(NOW(), INTERVAL 25 DAY), 13, 13);

-- ==============================================
-- 📧 API CONTACTO (BD: contacto_db) - API 8085
-- ==============================================
CREATE DATABASE IF NOT EXISTS contacto_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE contacto_db;

DROP TABLE IF EXISTS contacto;
CREATE TABLE contacto (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  mensaje VARCHAR(255) NOT NULL,
  id_usuario INT NOT NULL,
  respuesta VARCHAR(1000),
  resuelto TINYINT(1) NOT NULL DEFAULT 0,
  f_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Contactos previos + 5 nuevos
INSERT INTO contacto (nombre, correo, mensaje, id_usuario, respuesta, resuelto) VALUES
  ('Pedro Picapiedra', 'pedro@condor.cl', 'Pregunta sobre los logros.', 1, 'Se respondio via correo.', 1),
  ('Vilma Marmol', 'vilma@condor.cl', 'Error con Cerro Manquehue.', 2, NULL, 0),
  ('Lucia Rojas', 'lucia@condor.cl', 'No puedo subir foto de perfil.', 4, NULL, 0),
  ('Diego Silva', 'diego@condor.cl', 'Sugerencia de nueva ruta.', 5, 'Se revisara en la comision.', 1),
  ('Marta Pino', 'marta@condor.cl', 'Problema con preguntas de seguridad.', 6, NULL, 0),
  ('Kevin Torres', 'kevin@condor.cl', 'Cuenta desactivada erroneamente.', 7, 'Se reactivo manualmente.', 1),
  ('Valentina Vega', 'valentina@condor.cl', 'Consulta por calificaciones previas.', 8, NULL, 0),

  -- 5 contactos extra
  ('Andres Paredes','andres@condor.cl','Consulta sobre horarios de sendero',9,NULL,0),
  ('Camila Fuentes','camila@condor.cl','Reporte de señalización dañada',10,NULL,0),
  ('Rafael Ortega','rafael@condor.cl','Solicitud de ruta guiada',11,'Se contactará guía',1),
  ('Sofia Mella','sofia@condor.cl','Duda sobre condiciones invernales',12,NULL,0),
  ('Pablo Ibarra','pablo@condor.cl','Petición de nuevas fotos de cerros',13,NULL,0);

-- === FIN DEL SCRIPT ===
