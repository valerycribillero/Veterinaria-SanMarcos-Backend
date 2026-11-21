# Agenda y citas

Administra horarios de atención y reservas. Usa Spring Boot, MySQL y Flyway, con las carpetas `controller`, `service`, `repository`, `model` y `dto`.

## Iniciar

1. Inicia MySQL en Laragon y ejecuta `CREATE DATABASE agenda;`.
2. Abre `AgendaApplication.java` en VS Code y pulsa **Run** sobre `main`.
3. Abre `http://127.0.0.1:8088/api/horarios`.

La conexión local utiliza `root` con contraseña vacía. Flyway crea las tablas desde `src/main/resources/db/migracion`. `V1` crea horarios y `V2` crea citas. Al principio el listado está vacío.

## Probar en Postman

Crea las peticiones manualmente en Postman. Primero crea un horario y luego reserva una cita usando el ID recibido.

Ejemplo de POST a `/api/horarios`, usando una fecha futura.

```json
{"fecha": "2030-01-15", "hora": "09:00", "veterinario": "Dra. Veterinaria"}
```

Ejemplo de POST a `/api/citas`. Utiliza el ID del horario creado.

```json
{
  "horarioId": 1,
  "dueno": "Cliente de prueba",
  "correo": "cliente@example.com",
  "mascota": "Luna",
  "especie": "Perro",
  "servicio": "Vacunación",
  "motivo": "Control anual"
}
```

## Rutas

| Método | Ruta | Función |
|---|---|---|
| POST | `/api/horarios` | Crea un horario para un veterinario. |
| GET | `/api/horarios` | Lista horarios futuros, activos y disponibles. |
| DELETE | `/api/horarios/{id}` | Desactiva un horario libre. |
| PUT | `/api/horarios/{id}/activar` | Reactiva un horario futuro. |
| POST | `/api/citas` | Reserva el horario. Responde 201. |
| GET | `/api/citas` | Lista las citas con sus estados. |
| GET | `/api/citas/{id}` | Consulta una cita. |
| PUT | `/api/citas/{id}` | Edita o reprograma una cita. Recibe los mismos campos del POST. |
| PUT | `/api/citas/{id}/atendida` | Marca la cita como atendida. |
| DELETE | `/api/citas/{id}` | Cancela la cita y libera su horario. Responde 204. |

Los listados admiten `?fecha=2030-01-15` para filtrar.

## Reglas

Cada horario corresponde a una fecha, hora y veterinario. Se admite una reserva por horario y los bloqueos evitan dos reservas simultáneas. Dos veterinarios distintos pueden atender a la misma hora. La zona utilizada es `America/Santiago`. Los turnos se registran por hora de inicio con precisión de minutos. La duración debe incorporarse al integrar las prestaciones veterinarias.

Los estados son `RESERVADA`, `ATENDIDA` y `CANCELADA`. Cancelar conserva el historial. Repetir una cancelación es seguro. Reprogramar libera el horario anterior y ocupa el nuevo en una sola transacción. Si el nuevo está ocupado, se conserva la cita anterior. Una cita atendida o cancelada conserva su estado frente a cambios incompatibles.

Datos inválidos responden 400, recursos inexistentes 404 y horarios ocupados o cambios incompatibles 409. El campo `servicio` es opcional y toma «Consulta general» por defecto. El servidor obtiene fecha, hora y veterinario desde el horario seleccionado.

## Integración y pruebas

La API admite React desde `localhost:5173` y `127.0.0.1:5173`. El formulario de React aún guarda citas localmente. Para conectarlo deberá consultar horarios y enviar el `horarioId` elegido.

Los nombres de dueño, mascota, servicio y veterinario se reciben como texto. Su asociación con registros de otros servicios y los permisos de administración se integrarán cuando estén disponibles. La API escucha localmente en `127.0.0.1`.

Las pruebas están en `src/test/java/cl/veterinariasanmarcos/agenda/controller`: `CitaControllerTest.java` y `HorarioControllerTest.java`. Abre un archivo y pulsa **Run Test**.

Usan `@WebMvcTest`, MockMvc y un servicio simulado con `@MockitoBean`. Cada prueba prepara la respuesta con `when(...).thenReturn(...)`, realiza la petición con `mockMvc.perform(...)` y comprueba el resultado con `andExpect(...)`. `@BeforeEach` prepara los datos que se repiten antes de cada prueba.

No necesitan MySQL. Comprueban las rutas, las respuestas y las validaciones de entrada; no prueban la base de datos ni dos reservas simultáneas.
