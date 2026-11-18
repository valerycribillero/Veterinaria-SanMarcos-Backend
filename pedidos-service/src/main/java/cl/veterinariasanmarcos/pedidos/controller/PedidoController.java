package cl.veterinariasanmarcos.pedidos.controller;

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
    public record CambioEstado(@NotNull Pedido.Estado estado) {}
    private final PedidoService service;
    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping

    @ResponseStatus(HttpStatus.CREATED)
    public PedidoVista crear(@Valid @RequestBody NuevoPedido datos,
            @RequestHeader(value = "Idempotency-Key", required = false)
            @Pattern(regexp = "[A-Za-z0-9-]{1,64}") String referencia) {
        if (referencia == null) {
            referencia = UUID.randomUUID().toString();
        }
        return service.crear(referencia, datos);
    }

    @PostMapping("/{id}/confirmar")
    public PedidoVista confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @GetMapping
    public List<PedidoVista> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PedidoVista buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PatchMapping("/{id}/estado")
    public PedidoVista cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstado datos) {
        return service.cambiarEstado(id, datos.estado());
    }
}
