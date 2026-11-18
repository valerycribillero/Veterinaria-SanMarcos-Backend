CREATE TABLE movimiento_stock (
    id INT NOT NULL AUTO_INCREMENT,
    producto_id INT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(200),
    PRIMARY KEY (id),
    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id)
);