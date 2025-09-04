-- Script de Base de Datos para Microservicios de Banca
-- Creado para la prueba técnica

-- Crear base de datos para el servicio de clientes
CREATE DATABASE IF NOT EXISTS customer_db;
USE customer_db;

-- Tabla Persona (entidad base)
CREATE TABLE IF NOT EXISTS persona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(10) NOT NULL,
    edad INT NOT NULL,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla Cliente (hereda de Persona)
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    cliente_id VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE,
    INDEX idx_cliente_identificacion (persona_id),
    INDEX idx_cliente_estado (estado)
);

-- Crear base de datos para el servicio de contabilidad
CREATE DATABASE IF NOT EXISTS accounting_db;
USE accounting_db;

-- Tabla Cuenta
CREATE TABLE IF NOT EXISTS cuenta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    tipo_cuenta VARCHAR(20) NOT NULL,
    saldo_inicial DECIMAL(15,2) DEFAULT 0.00,
    saldo_actual DECIMAL(15,2) DEFAULT 0.00,
    cliente_id BIGINT NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cuenta_cliente (cliente_id),
    INDEX idx_cuenta_numero (numero_cuenta),
    INDEX idx_cuenta_estado (estado)
);

-- Tabla Movimiento
CREATE TABLE IF NOT EXISTS movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(20) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    saldo DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cuenta_id) REFERENCES cuenta(id) ON DELETE CASCADE,
    INDEX idx_movimiento_cuenta (cuenta_id),
    INDEX idx_movimiento_cliente (cliente_id),
    INDEX idx_movimiento_fecha (fecha)
);

-- Insertar datos de prueba según los casos de uso

-- Datos de Personas
INSERT INTO customer_db.persona (nombre, genero, edad, identificacion, direccion, telefono) VALUES
('Jose Lema', 'Masculino', 35, '1234567890', 'Otavalo sn y principal', '098254785'),
('Marianela Montalvo', 'Femenino', 28, '0987654321', 'Amazonas y NN.UU', '097548965'),
('Juan Osorio', 'Masculino', 42, '1122334455', '13 junio y Equinoccial', '098874587');

-- Datos de Clientes
INSERT INTO customer_db.cliente (persona_id, cliente_id, contrasena, estado) VALUES
(1, 'CLI001', '1234', TRUE),
(2, 'CLI002', '5678', TRUE),
(3, 'CLI003', '1245', TRUE);

-- Datos de Cuentas
INSERT INTO accounting_db.cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_actual, cliente_id, estado) VALUES
('478758', 'Ahorros', 2000.00, 2000.00, 1, TRUE),
('225487', 'Corriente', 100.00, 100.00, 2, TRUE),
('495878', 'Ahorros', 0.00, 0.00, 3, TRUE),
('496825', 'Ahorros', 540.00, 540.00, 2, TRUE),
('585545', 'Corriente', 1000.00, 1000.00, 1, TRUE);

-- Datos de Movimientos de ejemplo
INSERT INTO accounting_db.movimiento (cuenta_id, cliente_id, tipo_movimiento, valor, saldo, fecha) VALUES
(1, 1, 'Retiro', 575.00, 1425.00, '2024-01-10 10:30:00'),
(2, 2, 'Deposito', 600.00, 700.00, '2024-01-10 11:15:00'),
(3, 3, 'Deposito', 150.00, 150.00, '2024-01-10 14:20:00'),
(4, 2, 'Retiro', 540.00, 0.00, '2024-01-08 16:45:00');

-- Actualizar saldos actuales en las cuentas
UPDATE accounting_db.cuenta SET saldo_actual = 1425.00 WHERE id = 1;
UPDATE accounting_db.cuenta SET saldo_actual = 700.00 WHERE id = 2;
UPDATE accounting_db.cuenta SET saldo_actual = 150.00 WHERE id = 3;
UPDATE accounting_db.cuenta SET saldo_actual = 0.00 WHERE id = 4;

-- Crear índices adicionales para optimización
CREATE INDEX idx_movimiento_tipo_fecha ON accounting_db.movimiento(tipo_movimiento, fecha);
CREATE INDEX idx_cuenta_tipo_estado ON accounting_db.cuenta(tipo_cuenta, estado);

-- Crear vistas para reportes
CREATE VIEW accounting_db.vista_estado_cuenta AS
SELECT 
    c.id as cuenta_id,
    c.numero_cuenta,
    c.tipo_cuenta,
    c.saldo_actual,
    c.estado as cuenta_estado,
    c.cliente_id,
    p.nombre as cliente_nombre,
    p.identificacion as cliente_identificacion
FROM accounting_db.cuenta c
LEFT JOIN customer_db.persona p ON c.cliente_id = p.id;

-- Crear vista de movimientos con información del cliente
CREATE VIEW accounting_db.vista_movimientos_detalle AS
SELECT 
    m.id as movimiento_id,
    m.fecha,
    m.tipo_movimiento,
    m.valor,
    m.saldo,
    c.numero_cuenta,
    c.tipo_cuenta,
    c.saldo_inicial,
    c.estado as cuenta_estado,
    m.cliente_id,
    p.nombre as cliente_nombre,
    p.identificacion as cliente_identificacion
FROM accounting_db.movimiento m
JOIN accounting_db.cuenta c ON m.cuenta_id = c.id
LEFT JOIN customer_db.persona p ON m.cliente_id = p.id
ORDER BY m.fecha DESC;

-- Crear usuario para la aplicación
CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY 'app_password';
GRANT ALL PRIVILEGES ON customer_db.* TO 'app_user'@'%';
GRANT ALL PRIVILEGES ON accounting_db.* TO 'app_user'@'%';
FLUSH PRIVILEGES;

-- Mostrar información de las tablas creadas
SELECT 'Base de datos customer_db creada exitosamente' as mensaje;
SELECT 'Base de datos accounting_db creada exitosamente' as mensaje;
SELECT 'Datos de prueba insertados correctamente' as mensaje;
