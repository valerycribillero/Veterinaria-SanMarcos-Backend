-- Ejecutar una sola vez, con las aplicaciones detenidas y las nuevas bases recién creadas por Flyway.
-- La base tienda se conserva como origen. El catálogo inicial de productos se sustituye por sus datos.
USE tienda;
DROP PROCEDURE IF EXISTS migrar_datos_tienda;
DELIMITER $$
CREATE PROCEDURE migrar_datos_tienda()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF EXISTS (SELECT 1 FROM pedidos.pedido) OR EXISTS (SELECT 1 FROM productos.reserva_stock) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Las bases nuevas ya tienen compras o reservas. Detener la importación.';
    END IF;

    START TRANSACTION;

    DELETE FROM productos.producto;
    INSERT INTO productos.producto
        (codigo, nombre, categoria, precio, stock, imagen, principio_activo, presentacion, especie, activo)
    SELECT codigo, nombre, categoria, precio, stock, imagen, principio_activo, presentacion, especie, activo
    FROM tienda.producto;

    INSERT INTO pedidos.pedido
        (id, referencia, nombre_completo, correo, direccion, metodo, fecha, estado, total)
    SELECT id, CONCAT('legado-', id), nombre_completo, correo, direccion, metodo, fecha, estado, total
    FROM tienda.pedido;

    INSERT INTO pedidos.detalle_pedido
        (id, pedido_id, codigo, nombre, precio_unitario, cantidad, subtotal)
    SELECT id, pedido_id, codigo, nombre, precio_unitario, cantidad, subtotal
    FROM tienda.detalle_pedido;

    -- El stock de las compras anteriores ya fue descontado. Solo se registra la reserva histórica.
    INSERT INTO productos.reserva_stock (id, estado, firma)
    SELECT CONCAT('legado-', id),
        CASE WHEN estado = 'CANCELADO' THEN 'LIBERADA' ELSE 'RESERVADA' END,
        NULL
    FROM tienda.pedido;

    INSERT INTO productos.reserva_detalle
        (reserva_id, posicion, codigo, nombre, precio_unitario, cantidad)
    SELECT CONCAT('legado-', d.pedido_id),
        ROW_NUMBER() OVER (PARTITION BY d.pedido_id ORDER BY d.codigo) - 1,
        d.codigo, d.nombre, d.precio_unitario, d.cantidad
    FROM tienda.detalle_pedido d
    JOIN tienda.pedido p ON p.id = d.pedido_id
    WHERE p.estado <> 'CANCELADO';

    COMMIT;
END$$
DELIMITER ;

CALL migrar_datos_tienda();
DROP PROCEDURE migrar_datos_tienda;
