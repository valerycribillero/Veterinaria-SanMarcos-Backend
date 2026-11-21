# Compras y pedidos

Este servicio guarda las compras y sus detalles. Consulta a Productos por HTTP para reservar stock y obtener los precios del momento de la compra.

## Iniciar

1. Inicia MySQL en Laragon y crea la base con `CREATE DATABASE pedidos;`.
2. Arranca Productos en el puerto 8086.
3. Abre `PedidosApplication.java` en VS Code y pulsa **Run** sobre `main`.
4. Consulta `http://127.0.0.1:8087/api/pedidos`.

La conexión usa `root` con contraseña vacía. `DB_URL`, `DB_USER` y `DB_PASSWORD` permiten cambiarla. `PRODUCTOS_URL` indica dónde está Productos y vale `http://127.0.0.1:8086` por defecto. `PORT` cambia el puerto HTTP.

## Rutas

| Método | Ruta | Función |
|---|---|---|
| POST | `/api/pedidos` | Registra la solicitud y confirma el stock. Responde 201 al completar la compra. |
| GET | `/api/pedidos` | Lista las compras y solicitudes pendientes. |
| GET | `/api/pedidos/{id}` | Muestra el pedido con sus detalles. |
| POST | `/api/pedidos/{id}/confirmar` | Reintenta confirmar un pedido pendiente. |
| PATCH | `/api/pedidos/{id}/estado` | Actualiza el estado, por ejemplo con `{"estado":"CANCELADO"}`. |

Ejemplo para registrar una compra en Postman.

```json
{
  "nombreCompleto": "Cliente de prueba",
  "correo": "cliente@example.com",
  "direccion": "Av. Brasil 2140",
  "metodo": "Transferencia",
  "productos": [{"codigo": "ME001", "cantidad": 2}]
}
```

Para poder repetir el POST de la misma compra sin crear otra, envía el encabezado `Idempotency-Key` con una referencia única, por ejemplo `compra-prueba-1`. Conserva esa referencia hasta recibir la confirmación. Utiliza una referencia diferente para la siguiente compra. Si omites el encabezado, cada POST crea una solicitud nueva.

El servidor conserva el nombre y precio reservados y calcula el total. El precio de una compra anterior se mantiene aunque cambie el catálogo.

## Interrupciones y reintentos

La solicitud se guarda como `PENDIENTE` antes de contactar a Productos. Si falla la conexión, la API responde 503 e incluye `pedidoId`. Consulta ese ID y repite `POST /api/pedidos/{id}/confirmar`. Productos reconoce la reserva por su referencia y devuelve el mismo resultado aunque haya descontado el stock antes de la interrupción.

Stock insuficiente devuelve 409 y un producto inexistente devuelve 404. La solicitud queda pendiente para que pueda corregirse el catálogo y reintentarse, o cancelarse. El intento fallido revierte los descuentos de todas las líneas en Productos.

El estado avanza de `CONFIRMADO` a `ENVIADO` y luego a `ENTREGADO`. Un pedido pendiente o confirmado puede cancelarse. Durante la devolución de stock queda como `CANCELACION_PENDIENTE`. Si se corta la comunicación, repite el cambio a `CANCELADO`. El stock se devuelve una sola vez. Un pedido enviado o entregado conserva su estado ante un intento de cancelación.

La recuperación se realiza mediante estos reintentos explícitos. Las solicitudes pendientes aparecen en el listado y conservan sus datos entre reinicios.

## Conectar React

El listado, detalle e inventario consultan Productos en 8086. Pago e historial consultan Pedidos en 8087. Al completar el POST, muestra el total devuelto por Pedidos y vacía el carrito. Ante un error, conserva el carrito y el `pedidoId` para consultar o reintentar.

Las APIs de administración y los listados de compras necesitan integrar autenticación y roles antes de usarse públicamente. El servicio registra pedidos con un método de pago seleccionado. El cobro bancario requiere una integración adicional.

## Pruebas

Abre `src/test/java/cl/veterinariasanmarcos/pedidos/controller/PedidoControllerTest.java` y pulsa **Run Test**. Usa `@WebMvcTest`, MockMvc y Mockito para comprobar crear, listar, buscar, confirmar y cambiar el estado de un pedido, además de respuestas a datos inválidos y pedidos inexistentes.

`when(...).thenReturn(...)` prepara la respuesta del servicio simulado. `mockMvc.perform(...)` hace la petición y `andExpect(...)` comprueba el resultado. En Spring Boot 4, `@MockitoBean` cumple el papel de `@MockBean` de ejemplos anteriores.

Estas pruebas no necesitan MySQL ni Productos iniciado. Comprueban el controlador y las validaciones de entrada; no prueban el cálculo de stock ni la comunicación real entre servicios.
