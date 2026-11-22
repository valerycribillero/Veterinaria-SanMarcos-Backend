-- Bases para los once microservicios de San Marcos.
CREATE DATABASE IF NOT EXISTS `usuarios` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `mensajeria` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `mascota` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `producto` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `sanmarcos_ficha_db` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `productos` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `pedidos` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `agenda` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `sanmarcos_compras_db` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `sanmarcos_detalle_servicios_db` CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS `sanmarcos_servicios_db` CHARACTER SET utf8mb4;

-- La imagen de MySQL crea este usuario antes de ejecutar el script.
GRANT ALL PRIVILEGES ON `usuarios`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `mensajeria`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `mascota`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `producto`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `sanmarcos\_ficha\_db`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `productos`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `pedidos`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `agenda`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `sanmarcos\_compras\_db`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `sanmarcos\_detalle\_servicios\_db`.* TO 'sanmarcos'@'%';
GRANT ALL PRIVILEGES ON `sanmarcos\_servicios\_db`.* TO 'sanmarcos'@'%';
