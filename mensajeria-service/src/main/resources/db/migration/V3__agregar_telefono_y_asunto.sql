ALTER TABLE contacto
    ADD COLUMN telefono VARCHAR(25);

ALTER TABLE contacto
    ADD COLUMN asunto VARCHAR(100) NOT NULL DEFAULT 'Consulta General';
