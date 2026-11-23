package cl.veterinariasanmarcos.agenda.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.veterinariasanmarcos.agenda.dto.NuevoHorario;
import cl.veterinariasanmarcos.agenda.model.Horario;
import cl.veterinariasanmarcos.agenda.service.HorarioService;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class HorarioController {
    private final HorarioService service;

    public HorarioController(HorarioService service) {
        this.service = service;
    }

    @Operation(
        summary = "Crear horario",
        description = "Registra un horario para la agenda veterinaria."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Horario crear(@Valid @RequestBody NuevoHorario datos) {
        return service.crear(datos);
    }

    @Operation(
        summary = "Listar horarios disponibles",
        description = "Devuelve los horarios disponibles. Usa el parámetro fecha para consultar un día específico."
    )
    @GetMapping
    public List<Horario> listar(@RequestParam(required = false) LocalDate fecha) {
        return service.listarDisponibles(fecha);
    }

    @Operation(
        summary = "Desactivar horario",
        description = "Retira el horario indicado de la lista de horarios disponibles."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }

    @Operation(
        summary = "Activar horario",
        description = "Vuelve a habilitar el horario indicado para la agenda."
    )
    @PutMapping("/{id}/activar")
    public Horario activar(@PathVariable Long id) {
        return service.activar(id);
    }
}
