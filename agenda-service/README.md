# Agenda y citas

Administra la disponibilidad de veterinarios y las reservas de citas. Flyway crea las tablas `horario` y `cita` con los scripts en `src/main/resources/db/migracion`.

## Arrancar y probar

Con el backend iniciado según el [README principal](../README.md), abre [Swagger de Agenda](http://localhost:8088/swagger-ui/index.html). En Postman usa la base `http://localhost:8080/agenda`.

1. Crea un horario con `POST /api/horarios`. Usa una fecha futura y guarda el ID de la respuesta.

   ```json
   {"fecha": "2030-01-15", "hora": "09:00", "veterinario": "Dra. Veterinaria"}
   ```

2. Reserva con `POST /api/citas` y reemplaza `horarioId` por el ID devuelto.

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

3. Consulta `GET /api/citas` para ver las reservas y `GET /api/horarios` para ver los horarios que siguen libres. Agrega `?fecha=2030-01-15` para filtrar por día.

Agenda usa la hora de Chile continental. Cada horario admite una reserva. Anular una cita con `DELETE /api/citas/{id}` libera el horario y deja la cita como `CANCELADA`.

React consulta esta API al crear horarios y reservar citas. Las pruebas de los controladores se ejecutan con **Run Test** en VS Code.
