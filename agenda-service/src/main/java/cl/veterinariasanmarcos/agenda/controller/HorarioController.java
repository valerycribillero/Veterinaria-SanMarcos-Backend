package cl.veterinariasanmarcos.agenda.controller;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Horario crear(@Valid @RequestBody NuevoHorario datos) {
        return service.crear(datos);
    }

    @GetMapping
    public List<Horario> listar(@RequestParam(required = false) LocalDate fecha) {
        return service.listarDisponibles(fecha);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }

    @PutMapping("/{id}/activar")
    public Horario activar(@PathVariable Long id) {
        return service.activar(id);
    }
}
