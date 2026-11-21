CREATE TABLE categoria (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150),
    PRIMARY KEY (id),
    UNIQUE KEY uk_categoria_nombre (nombre)
);