CREATE TABLE reserva_stock (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    estado VARCHAR(20) NOT NULL,
    firma VARCHAR(5000)
);
CREATE TABLE reserva_detalle (
    reserva_id VARCHAR(64) NOT NULL,
    posicion INT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    precio_unitario DECIMAL(10,0) NOT NULL,
    cantidad INT NOT NULL,
    PRIMARY KEY (reserva_id, posicion),
    FOREIGN KEY (reserva_id) REFERENCES reserva_stock(id),
    FOREIGN KEY (codigo) REFERENCES producto(codigo)
);
