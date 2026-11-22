CREATE TABLE cita (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    horario_id BIGINT NOT NULL,
    dueno VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    mascota VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    servicio VARCHAR(150) NOT NULL,
    motivo VARCHAR(2000) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    veterinario VARCHAR(150) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT fk_cita_horario FOREIGN KEY (horario_id) REFERENCES horario(id)
);

CREATE INDEX idx_cita_fecha ON cita(fecha, hora);
