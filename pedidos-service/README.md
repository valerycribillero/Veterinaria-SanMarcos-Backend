# Pedidos de la tienda

Guarda pedidos y sus detalles. Al confirmar una compra, consulta a Productos para reservar stock y calcular el total con los precios vigentes. Flyway crea las tablas desde `src/main/resources/db/migracion`.

Con Productos y el backend iniciados según el [README principal](../README.md), abre [Swagger de Pedidos](http://localhost:8087/swagger-ui/index.html). En Postman usa la base `http://localhost:8080/pedidos`.

| Método | Ruta | Función |
|---|---|---|
| POST | `/api/pedidos` | Registra el pedido e intenta confirmar el stock. |
| GET | `/api/pedidos` | Lista los pedidos y su estado. |
| GET | `/api/pedidos/{id}` | Muestra el detalle de un pedido. |
| POST | `/api/pedidos/{id}/confirmar` | Reintenta confirmar un pedido pendiente. |
| PATCH | `/api/pedidos/{id}/estado` | Cambia el estado. Ejemplo `{"estado":"CANCELADO"}`. |

Ejemplo de pedido para Postman.

```json
{
  "nombreCompleto": "Cliente de prueba",
  "correo": "cliente@example.com",
  "direccion": "Av. Brasil 2140",
  "metodo": "Transferencia",
  "productos": [{"codigo": "ME001", "cantidad": 2}]
}
```

Usa el mismo encabezado `Idempotency-Key` al reintentar el mismo pedido. Si la API responde `503` con un `pedidoId`, consulta ese ID y reintenta la confirmación. El estado de un pedido confirmado puede avanzar a `ENVIADO` y después a `ENTREGADO`. Cancelar un pedido pendiente o confirmado devuelve el stock asociado.

En React, el carrito y el historial usan este servicio. Las pruebas de los controladores se ejecutan con **Run Test** en VS Code.
