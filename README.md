# Veterinaria San Marcos Backend

El primer servicio, `mensajeria-service`, guarda y administra los mensajes de Contacto. El frontend puede enviar peticiones JSON desde el formulario de React y consultar el listado desde administración.

## Ejecutar en Windows

1. Enciende MySQL y crea una base vacía para este servicio.

   ```sql
   CREATE DATABASE mensajeria;
   ```

2. Con MySQL de Laragon iniciado, ejecuta el servicio en PowerShell.

   ```powershell
   cd mensajeria-service
   .\mvnw.cmd spring-boot:run
   ```

   Se necesita JDK 17 o superior. El Maven Wrapper descarga Maven cuando lo ejecutas por primera vez. El servicio escucha en `127.0.0.1:8082` y usa MySQL en `127.0.0.1:3306`. Si el puerto o la base difieren, ajusta `DB_URL`. Flyway aplica las migraciones pendientes al arrancar y Hibernate comprueba el esquema.

   La configuración local usa el usuario `root` sin contraseña. Si tu instalación tiene otras credenciales, define `DB_USER` y `DB_PASSWORD` en la terminal antes de arrancar.

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

Desde `mensajeria-service`, ejecuta `.\mvnw.cmd test` para comprobar el arranque con una base de pruebas en memoria. La API es para desarrollo local y sus operaciones administrativas aún requieren autenticación antes de exponerlas a otras personas.
