package cl.veterinariasanmarcos.productos.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.veterinariasanmarcos.productos.dto.*;
import cl.veterinariasanmarcos.productos.service.ReservaService;

@RestController
@RequestMapping("/api/reservas-stock")
public class ReservaController {
    private final ReservaService service;
    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PutMapping("/{id}")
    public ReservaVista reservar(@PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,64}") String id,
            @Valid @RequestBody DatosReserva datos) {
        return service.reservar(id, datos);
    }

    @DeleteMapping("/{id}")

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void liberar(@PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,64}") String id) {
        service.liberar(id);
    }
}
