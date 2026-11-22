CREATE TABLE mascotas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(60) NOT NULL,
    especie VARCHAR(10) NOT NULL,
    raza VARCHAR(60),
    edad INT NOT NULL,
    sexo VARCHAR(10),
    peso_kg DOUBLE,
    cliente_id BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    creado_en DATETIME,

    PRIMARY KEY (id)
);