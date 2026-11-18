package cl.veterinariasanmarcos.agenda.controller;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cita reservar(@Valid @RequestBody DatosCita datos) {
        return service.reservar(datos);
    }

    @GetMapping
    public List<Cita> listar(@RequestParam(required = false) LocalDate fecha) {
        return service.listar(fecha);
    }

    @GetMapping("/{id}")
    public Cita buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Long id, @Valid @RequestBody DatosCita datos) {
        return service.actualizar(id, datos);
    }

    @PutMapping("/{id}/atendida")
    public Cita atender(@PathVariable Long id) {
        return service.atender(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long id) {
        service.cancelar(id);
    }
}
