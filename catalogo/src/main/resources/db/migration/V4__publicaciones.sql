CREATE TABLE publicacion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    resumen VARCHAR(1000) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    fecha VARCHAR(30) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    imagen VARCHAR(500) NOT NULL,
    contenido TEXT NOT NULL
);
INSERT INTO publicacion (titulo, resumen, autor, fecha, categoria, imagen, contenido) VALUES ('Cuidados esenciales para tu perro en época de verano', 'Aprende a proteger a tu canino del golpe de calor, deshidratación y quemaduras en las almohadillas.', 'Dra. María Paz Fernández', '15 Oct 2026', 'Cuidados Caninos', '/img/perro.jpg', 'Durante los días de calor, procura que tu perro tenga agua fresca disponible, evite paseos en las horas de mayor temperatura y descanse en un lugar con sombra. Revisa también el suelo antes de salir: el pavimento caliente puede dañar sus almohadillas. Si notas jadeo excesivo, decaimiento o vómitos, consulta con un médico veterinario.');
INSERT INTO publicacion (titulo, resumen, autor, fecha, categoria, imagen, contenido) VALUES ('Guía de nutrición felina: ¿Qué necesita realmente tu gato?', 'Descubre la importancia de las proteínas de alta calidad y la hidratación en la dieta diaria felina.', 'Dr. Carlos Silva Reyes', '10 Oct 2026', 'Salud Felina', '/img/gato.jpg', 'Los gatos necesitan una alimentación rica en proteínas de origen animal y acceso constante a agua limpia. Combinar alimento húmedo y seco puede ayudar a mejorar su hidratación. Mantén sus porciones según indicación veterinaria y evita darles alimentos de consumo humano que puedan afectar su salud.');
INSERT INTO publicacion (titulo, resumen, autor, fecha, categoria, imagen, contenido) VALUES ('Conejos y exóticos: Prevención de problemas dentales', 'El crecimiento continuo de los dientes en roedores y conejos requiere una dieta adecuada en heno.', 'Dra. Ana López Castro', '02 Oct 2026', 'Mascotas Exóticas', '/img/conejo.jpg', 'Los dientes de conejos y pequeños roedores crecen continuamente. Por eso, una dieta con heno de buena calidad es fundamental para desgastarlos de forma natural. Si tu mascota deja de comer, babea o presenta dificultad para masticar, necesita una evaluación veterinaria lo antes posible.');
