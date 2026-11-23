# Productos de la tienda

Administra productos, precios y stock. Flyway crea las tablas desde `src/main/resources/db/migracion` y carga el catálogo inicial.

Con el backend iniciado según el [README principal](../README.md), abre [Swagger de Productos](http://localhost:8086/swagger-ui/index.html) o prueba `GET http://localhost:8080/productos/api/productos` en Postman.

| Método | Ruta directa | Función |
|---|---|---|
| GET | `/api/productos` | Lista los productos activos. |
| GET | `/api/productos/{codigo}` | Consulta el detalle de un producto. |
| POST | `/api/productos` | Crea un producto. |
| PUT | `/api/productos/{codigo}` | Actualiza sus datos. |
| PATCH | `/api/productos/{codigo}/stock` | Cambia el stock. Ejemplo `{"stock":15}`. |
| DELETE | `/api/productos/{codigo}` | Desactiva el producto. |

Pedidos usa `PUT /api/reservas-stock/{referencia}` para reservar unidades y `DELETE /api/reservas-stock/{referencia}` para devolverlas al inventario. Repetir una reserva con la misma referencia y los mismos ítems conserva el resultado.
