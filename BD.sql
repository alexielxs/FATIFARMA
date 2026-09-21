DROP DATABASE IF EXISTS db_fatifarma_central;
CREATE DATABASE db_fatifarma_central;
USE db_fatifarma_central;

-- =========================================================================
-- BLOQUE 1: SEGURIDAD Y CONTROL DE ACCESOS (Autenticación y Autorización)
-- =========================================================================

-- 1. Tabla Maestra de Roles: Define las jerarquías oficiales del mostrador
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE -- 'PROPIETARIO', 'FARMACEUTICO', 'TECNICA'
);

-- 2. Tabla de Usuarios: Registra al personal y se amarra físicamente a su rol asignado
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rol_id BIGINT NOT NULL,                    -- LLAVE FORÁNEA FÍSICA: Conecta con la tabla roles
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,        -- Utilizado para iniciar sesión (HU01, HU02)
    password VARCHAR(255) NOT NULL,            -- Contraseña encriptada con BCrypt (Protección de datos)
    
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) 
        REFERENCES roles(id) ON DELETE RESTRICT
);

-- =========================================================================
-- BLOQUE 2: INVENTARIO Y CATÁLOGO DE MEDICAMENTOS (DIGEMID y Normalización 3NF)
-- =========================================================================

-- 3. Tabla Maestra de Categorías: Mapea de forma física tus 4 pestañas del catálogo visual
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE -- 'MEDICAMENTOS', 'CUIDADO_PERSONAL', 'SUPLEMENTOS', 'NO_FARMACEUTICOS'
);

-- 4. Tabla Maestra de Sucursales: Resuelve la unicidad operativa de los locales comerciales de Ate
CREATE TABLE sucursales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_sucursal VARCHAR(100) NOT NULL UNIQUE -- 'FATIFARMA_ATE_CENTRAL', 'FATIFARMA_ATE_PRINCIPAL'
);

-- 5. Tabla Maestra de Productos: Almacena la definición científica y regulatoria (HU04)
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    categoria_id BIGINT NOT NULL,                 -- LLAVE FORÁNEA FÍSICA: Amarra la categoría en 3NF
    nombre VARCHAR(255) NOT NULL,                 -- Ej: Paracetamol 500mg
    forma_farmaceutica VARCHAR(100) NULL,         -- Tableta, Jarabe, Crema, Inyectable
    presentacion VARCHAR(150) NULL,               -- Caja x 100 tabletas
    unidad_inventario VARCHAR(50) NOT NULL,       -- Caja
    unidad_stock VARCHAR(50) NOT NULL,            -- Unidades sueltas
    stock_minimo INT NOT NULL,                    -- Disparador para alertas de desabastecimiento (Tarjeta Roja)
    fiscalizado_digemid BOOLEAN NOT NULL,         -- Exige receta médica si es TRUE
    registro_sanitario VARCHAR(100) NULL,         -- Código oficial (Ej: NG-4512)
    codigo_medicamento VARCHAR(100) NULL UNIQUE,  -- Código de barras comercial institucional
    
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) 
        REFERENCES categorias(id) ON DELETE RESTRICT
);

-- 6. Tabla de Lotes: Registra las existencias físicas distribuidas por sedes y vencimientos (HU05)
CREATE TABLE lotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,                  -- LLAVE FORÁNEA FÍSICA: Enlaza con el producto maestro
    sucursal_id BIGINT NOT NULL,                  -- LLAVE FORÁNEA FÍSICA: Vincula de forma exacta la farmacia de destino
    codigo_lote VARCHAR(100) NOT NULL,            -- Código de lote (Ej: LOTE-A123)
    cantidad INT NOT NULL,                        -- Cantidad real disponible en estante
    precio_venta DOUBLE NOT NULL,                  -- Precio unitario comercial
    fecha_vencimiento DATE NOT NULL,              -- Controlado por alertas de caducidad (Tarjeta Amarilla)
    ubicacion_anaquel VARCHAR(255) NULL,          -- HU08: Ubicación física exacta en la botica (Ej: Vitrina 2)
    usuario_registro VARCHAR(255) NOT NULL,       -- Email del farmacéutico auditor que ingresó la mercadería
    
    CONSTRAINT fk_lotes_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_lotes_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE RESTRICT
);

-- =========================================================================
-- BLOQUE 3: PUNTO DE VENTA Y FOLDER DIGITAL (Caja Inmutable y Control Forense)
-- =========================================================================

-- 7. Tabla de Ventas: Cabecera inmutable de la transacción y el expediente médico posterior (HU09)
CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,                   -- LLAVE FORÁNEA FÍSICA: Registra qué usuario cobró en caja
    sucursal_id BIGINT NOT NULL,                  -- LLAVE FORÁNEA FÍSICA: Registra en qué farmacia se emitió el ticket
    tipo_venta VARCHAR(50) NOT NULL,              -- 'NORMAL' o 'CON_RECETA' (Diferenciación automática de tu UseCase)
    fecha_venta DATETIME NOT NULL,                -- Sella el momento exacto del cobro contable
    total DOUBLE NOT NULL,                         -- Monto económico total de la boleta
    metodo_pago VARCHAR(50) NOT NULL,             -- 'EFECTIVO', 'YAPE', 'PLIN', 'TARJETA' (Para las gráficas de la HU12)
    
    -- Campos Embebidos de la Receta Médica (Nacen en NULL hasta la regularización en el folder virtual)
    receta_numero VARCHAR(100) NULL,              
    medico_nombre VARCHAR(255) NULL,              
    medico_colegiatura VARCHAR(50) NULL,          
    medico_rne VARCHAR(50) NULL,                  -- Registro Nacional de Especialista
    receta_fecha_emision DATE NULL,
    receta_fecha_vigencia DATE NULL,
    cliente_dni VARCHAR(20) NULL,                 
    receta_fotocopiada_base64 LONGTEXT NULL,      -- Archivo PDF/Foto convertido en texto plano Base64
    
    -- CANDADO DE AUDITORÍA FORENSE DE LA DIGEMID (Agregado para la HU15)
    receta_fecha_registro_sistema DATETIME NULL,  -- Sella de forma transparente cuándo se digitalizó el documento
    
    CONSTRAINT fk_ventas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ventas_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE RESTRICT
);

-- 8. Tabla de Detalles: Registra el desglose analítico del carrito de compras (HU10 - Fraccionamiento)
CREATE TABLE detalle_ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,                     -- LLAVE FORÁNEA FÍSICA: Enlaza con la boleta de venta
    lote_id BIGINT NOT NULL,                      -- LLAVE FORÁNEA FÍSICA: Enlaza con el stock físico del lote consumido
    producto_nombre VARCHAR(255) NOT NULL,        -- Captura el nombre inmutable del medicamento al vender
    cantidad INT NOT NULL,                        -- Unidades o pastillas fraccionadas compradas
    precio_unitario DOUBLE NOT NULL,              -- Precio sellado al momento de la transacción
    
    CONSTRAINT fk_detalle_ventas_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_ventas_lote FOREIGN KEY (lote_id) REFERENCES lotes(id) ON DELETE RESTRICT
);

-- =========================================================================
-- DATA SEMILLA: Inserción de catálogos maestros iniciales obligatorios
-- =========================================================================

-- Inserción de Jerarquías
INSERT INTO roles (nombre_rol) VALUES ('PROPIETARIO'), ('FARMACEUTICO'), ('TECNICA');

-- Inserción de Categorías
INSERT INTO categorias (nombre_categoria) VALUES ('MEDICAMENTOS'), ('CUIDADO_PERSONAL'), ('SUPLEMENTOS'), ('NO_FARMACEUTICOS');

-- Inserción de Sucursales de la Botica Fatifarma en Ate
INSERT INTO sucursales (nombre_sucursal) VALUES ('FATIFARMA_ATE_CENTRAL'), ('FATIFARMA_ATE_PRINCIPAL');
