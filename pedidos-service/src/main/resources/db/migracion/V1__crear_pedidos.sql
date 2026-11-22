CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    referencia VARCHAR(64) NOT NULL UNIQUE,
    nombre_completo VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    direccion VARCHAR(300) NOT NULL,
    metodo VARCHAR(30) NOT NULL,
    fecha DATETIME(6) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total DECIMAL(18,0) NOT NULL CHECK (total >= 0)
);
CREATE TABLE detalle_pedido (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    precio_unitario DECIMAL(10,0) NOT NULL CHECK (precio_unitario >= 0),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    subtotal DECIMAL(18,0) NOT NULL CHECK (subtotal >= 0),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT uq_detalle_producto UNIQUE (pedido_id, codigo)
);
