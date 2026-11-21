# Veterinaria San Marcos Backend

## Servicios

| Servicio | Clase que debes ejecutar con Run en VS Code | Base MySQL | Puerto |
|---|---|---|---|
| Mensajería | `MensajeriaServiceApplication` | `mensajeria` | 8082 |
| Productos e inventario | `ProductosApplication` | `productos` | 8086 |
| Compras y pedidos | `PedidosApplication` | `pedidos` | 8087 |
| Agenda y citas | `AgendaApplication` | `agenda` | 8088 |

Para iniciar los dos servicios nuevos, activa MySQL en Laragon y crea las bases.

```sql
CREATE DATABASE productos;
CREATE DATABASE pedidos;
```

Ejecuta primero `ProductosApplication` y después `PedidosApplication`. Los dos tienen su propio `pom.xml` y sus carpetas `controller`, `service`, `repository`, `model` y `dto`. La comunicación HTTP con Productos está dentro de `PedidoService`.

El listado y el detalle consultan la misma información de Productos. Las compras y su historial se guardan en Pedidos. Cada servicio es dueño de sus tablas y Flyway aplica sus propias migraciones.

En React, las consultas de productos conservan el puerto 8086. Las peticiones a `/api/pedidos` pasan al puerto 8087. La integración del frontend con estas dos APIs corresponde al siguiente paso.

Si una compra o cancelación queda pendiente por una interrupción, conserva el `pedidoId` y reintenta esa operación. El README de Pedidos describe las rutas de recuperación. Las pruebas actuales usan MockMvc y servicios simulados para comprobar los controladores y las validaciones de entrada.

## Conservar datos anteriores de tienda

La base `tienda` conserva sus datos. Para trasladar productos y compras anteriores, arranca ambos servicios una vez y luego detenlos. Antes de utilizar las bases nuevas, revisa y ejecuta `migrar_datos_tienda.sql` en MySQL. El script copia el stock actual, los pedidos y sus precios históricos. También registra las reservas anteriores para permitir cancelar compras sin descontar ni devolver stock dos veces.

La importación sustituye el catálogo de ejemplo de `productos` por el catálogo de `tienda`. Debe ejecutarse sobre las bases nuevas, con todas las aplicaciones detenidas. Crea una copia de seguridad si las bases contienen información que necesites conservar.

Las siguientes instrucciones corresponden al servicio de Mensajería.

El primer servicio, `mensajeria-service`, guarda y administra los mensajes de Contacto. El frontend puede enviar peticiones JSON desde el formulario de React y consultar el listado desde administración.

## Ejecutar en Visual Studio Code

1. Inicia MySQL en Laragon. Si aún no creaste la base, ejecuta en MySQL lo siguiente.

   ```sql
   CREATE DATABASE mensajeria;
   ```

2. En VS Code, abre `MensajeriaServiceApplication.java` y pulsa **Run** sobre `main`.

3. Abre [la API de contactos](http://127.0.0.1:8082/api/contactos). Si aparece `[]`, el backend está funcionando y aún no hay mensajes.

Flyway crea las tablas al iniciar el backend.

## Probar en Postman

| Método | Ruta | Resultado |
|---|---|---|
| POST | `/api/contactos` | Guarda un contacto pendiente. Responde `201`. |
| GET | `/api/contactos` | Lista mensajes del más reciente al más antiguo. |
| PUT | `/api/contactos/{id}/atendido` | Actualiza el estado a `ATENDIDO`. |
| DELETE | `/api/contactos/{id}` | Elimina el contacto. Responde `204`. |

Para probar el POST, envía este cuerpo como JSON a `http://127.0.0.1:8082/api/contactos`.

```json
{
  "nombreCompleto": "Cliente de prueba",
  "correo": "cliente@example.com",
  "telefono": "+56912345678",
  "asunto": "Consulta General",
  "mensaje": "Quisiera consultar por los horarios de atención."
}
```

El teléfono es opcional. Si no se envía el asunto, se guarda como «Consulta General». El servidor genera el ID, la fecha y el estado inicial. Una solicitud con datos inválidos devuelve `400`. Actualizar o eliminar un contacto inexistente devuelve `404`.

Las migraciones `V1` y `V2` siguen el orden del ejemplo de clase. La primera crea la tabla y la segunda agrega el estado. La `V3` añade los campos de teléfono y asunto. Si ya ejecutaste las migraciones del profesor en `mensajeria`, crea una base vacía como `contacto` y configura `DB_URL=jdbc:mysql://127.0.0.1:3306/contacto` para que cada proyecto tenga su propio historial de Flyway.

## Carpetas

- `controller`: recibe y responde peticiones HTTP.
- `service`: aplica reglas como marcar un contacto atendido.
- `repository`: accede a MySQL mediante Spring Data JPA.
- `model`: define la entidad en Java.
- `dto`: define y valida los datos de entrada.
- `src/main/resources/db/migracion`: contiene el SQL versionado que ejecuta Flyway.

Las pruebas de controladores están en la carpeta `src/test/java` de cada servicio, dentro de `controller`. Abre `ContactoControllerTest.java`, `ProductoControllerTest.java`, `ReservaControllerTest.java`, `PedidoControllerTest.java`, `CitaControllerTest.java` o `HorarioControllerTest.java` y pulsa **Run Test**. Usan `when`, `mockMvc.perform` y `andExpect`; no requieren MySQL ni otros servicios iniciados. `@MockitoBean` es la anotación usada para simular el servicio en Spring Boot 4. Estas pruebas no comprueban la persistencia ni la comunicación real entre microservicios.

Para comprobar el arranque de Mensajería con una base de pruebas en memoria, abre `MensajeriaServiceApplicationTests.java` y pulsa **Run Test** sobre `laAplicacionIniciaCorrectamente`. La API es para desarrollo local y sus operaciones administrativas aún requieren autenticación antes de exponerlas a otras personas.
