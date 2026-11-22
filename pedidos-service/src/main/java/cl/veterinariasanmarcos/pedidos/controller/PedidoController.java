package cl.veterinariasanmarcos.pedidos.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.veterinariasanmarcos.pedidos.dto.*;
import cl.veterinariasanmarcos.pedidos.model.Pedido;
import cl.veterinariasanmarcos.pedidos.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PedidoController {
    public record CambioEstado(
            @NotNull
            Pedido.Estado estado
    ) {
    }

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Crear pedido de tienda",
        description = "Registra el pedido e intenta reservar stock en Productos. El encabezado Idempotency-Key permite repetir la solicitud sin duplicar el pedido."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoVista crear(
        @Valid @RequestBody NuevoPedido datos,
        @RequestHeader(
            value = "Idempotency-Key",
            required = false
        )
                    @Pattern(regexp = "[A-Za-z0-9-]{1,64}")
                    String referencia
    ) {
        if (referencia == null) {
            referencia = UUID.randomUUID().toString();
        }
        return service.crear(
            referencia,
            datos
        );
    }

    @Operation(
        summary = "Reintentar confirmación del pedido",
        description = "Vuelve a intentar la reserva de stock y la confirmación de un pedido pendiente."
    )
    @PostMapping("/{id}/confirmar")
    public PedidoVista confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @Operation(
        summary = "Listar pedidos de tienda",
        description = "Devuelve los pedidos registrados, incluidas las solicitudes pendientes."
    )
    @GetMapping
    public List<PedidoVista> listar() {
        return service.listar();
    }

    @Operation(
        summary = "Buscar pedido por ID",
        description = "Devuelve el pedido indicado con sus productos y su estado."
    )
    @GetMapping("/{id}")
    public PedidoVista buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(
        summary = "Cambiar estado del pedido",
        description = "Aplica la transición de estado solicitada. Al cancelar un pedido confirmado, el servicio devuelve el stock reservado."
    )
    @PatchMapping("/{id}/estado")
    public PedidoVista cambiarEstado(
        @PathVariable Long id,
        @Valid @RequestBody CambioEstado datos
    ) {
        return service.cambiarEstado(
            id,
            datos.estado()
        );
    }
}
