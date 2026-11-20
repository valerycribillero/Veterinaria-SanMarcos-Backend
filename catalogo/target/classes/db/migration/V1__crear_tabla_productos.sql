CREATE TABLE producto (
    id INT NOT NULL AUTO_INCREMENT,
    categoria VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    principio_activo VARCHAR(100),
    presentacion VARCHAR(80),
    especie VARCHAR(30) NOT NULL,
    stock INT NOT NULL,
    precio INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_producto_codigo (codigo)
);