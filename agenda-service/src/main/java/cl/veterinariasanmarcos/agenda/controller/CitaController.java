package cl.veterinariasanmarcos.agenda.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.veterinariasanmarcos.agenda.dto.DatosCita;
import cl.veterinariasanmarcos.agenda.model.Cita;
import cl.veterinariasanmarcos.agenda.service.CitaService;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CitaController {
    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    @Operation(
        summary = "Reservar cita",
        description = "Crea una cita veterinaria en el horario indicado."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cita reservar(@Valid @RequestBody DatosCita datos) {
        return service.reservar(datos);
    }

    @Operation(
        summary = "Listar citas",
        description = "Devuelve las citas registradas. Usa el parámetro fecha para filtrar por día."
    )
    @GetMapping
    public List<Cita> listar(@RequestParam(required = false) LocalDate fecha) {
        return service.listar(fecha);
    }

    @Operation(
        summary = "Buscar cita por ID",
        description = "Devuelve los datos de la cita indicada."
    )
    @GetMapping("/{id}")
    public Cita buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(
        summary = "Actualizar cita",
        description = "Modifica los datos de la cita identificada por su ID."
    )
    @PutMapping("/{id}")
    public Cita actualizar(
        @PathVariable Long id,
        @Valid @RequestBody DatosCita datos
    ) {
        return service.actualizar(
            id,
            datos
        );
    }

    @Operation(
        summary = "Marcar cita como atendida",
        description = "Cambia el estado de la cita a atendida."
    )
    @PutMapping("/{id}/atendida")
    public Cita atender(@PathVariable Long id) {
        return service.atender(id);
    }

    @Operation(
        summary = "Cancelar cita",
        description = "Cancela la cita identificada por su ID."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long id) {
        service.cancelar(id);
    }
}
