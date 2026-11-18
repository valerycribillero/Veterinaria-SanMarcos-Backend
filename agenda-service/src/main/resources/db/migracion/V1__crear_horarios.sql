CREATE TABLE horario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    veterinario VARCHAR(150) NOT NULL,
    reservado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_horario_veterinario UNIQUE (fecha, hora, veterinario)
);
