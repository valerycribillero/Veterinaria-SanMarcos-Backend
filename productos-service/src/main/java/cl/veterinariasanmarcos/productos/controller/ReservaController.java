package cl.veterinariasanmarcos.productos.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Reservar stock para un pedido",
        description = "Reserva las cantidades solicitadas bajo una referencia única y devuelve el resultado de la reserva."
    )
    @PutMapping("/{id}")
    public ReservaVista reservar(
        @PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,64}") String id,
        @Valid @RequestBody DatosReserva datos
    ) {
        return service.reservar(
            id,
            datos
        );
    }

    @Operation(
        summary = "Liberar reserva de stock",
        description = "Libera la reserva identificada por su referencia y devuelve las existencias reservadas."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void liberar(@PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,64}") String id) {
        service.liberar(id);
    }
}
