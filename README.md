# Veterinaria San Marcos Backend

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
- `src/main/resources/db/migration`: contiene el SQL versionado que ejecuta Flyway.

Para comprobar el arranque con una base de pruebas en memoria, abre `MensajeriaServiceApplicationTests.java` y pulsa **Run Test** sobre `contextLoads`. La API es para desarrollo local y sus operaciones administrativas aún requieren autenticación antes de exponerlas a otras personas.
