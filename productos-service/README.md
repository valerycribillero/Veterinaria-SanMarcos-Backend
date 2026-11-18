# Productos e inventario

Este servicio guarda productos, precios y stock. La tienda, el listado y el detalle consultan esta misma API.

## Iniciar

1. Inicia MySQL en Laragon y crea la base con `CREATE DATABASE productos;`.
2. Abre `ProductosApplication.java` en VS Code y pulsa **Run** sobre `main`.
3. Consulta `http://127.0.0.1:8086/api/productos`. Flyway crea las tablas y carga los 22 productos iniciales.

Usa `root` con contraseña vacía. `DB_URL`, `DB_USER` y `DB_PASSWORD` permiten cambiar la conexión. `PORT` cambia el puerto.

## Rutas

| Método | Ruta | Función |
|---|---|---|
| GET | `/api/productos` | Lista productos activos. |
| GET | `/api/productos/{codigo}` | Muestra el detalle del producto. |
| POST | `/api/productos` | Crea un producto. |
| PUT | `/api/productos/{codigo}` | Actualiza sus datos, precio y stock. |
| PATCH | `/api/productos/{codigo}/stock` | Establece el stock con `{"stock":15}`. |
| DELETE | `/api/productos/{codigo}` | Desactiva el producto. |

El código es único e inmutable. El precio debe ser positivo y entero. El stock debe ser entero y estar entre 0 y 1.000.000.

Las rutas de coordinación `PUT /api/reservas-stock/{referencia}` y `DELETE /api/reservas-stock/{referencia}` las utiliza Pedidos. La primera recibe `{"productos":[{"codigo":"ME001","cantidad":2}]}` y descuenta las existencias dentro de una transacción. Devuelve código, nombre, precio y cantidad reservados. La segunda devuelve las unidades reservadas.

Una referencia identifica una compra. Repetirla con los mismos productos conserva el resultado y evita descontar stock otra vez. Cancelarla permite devolver stock una sola vez, incluso si el producto se desactivó. Los productos se bloquean por código para coordinar compras simultáneas.

Los datos de prueba y de MySQL usan el mismo SQL de Flyway. Las imágenes siguen siendo rutas del frontend.
