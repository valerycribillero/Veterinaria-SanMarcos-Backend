package cl.veterinariasanmarcos.tienda.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.veterinariasanmarcos.tienda.dto.*;
import cl.veterinariasanmarcos.tienda.model.Pedido;
import cl.veterinariasanmarcos.tienda.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service = service; }
    public record CambioEstado(@NotNull Pedido.Estado estado) {}

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PedidoVista crear(@Valid @RequestBody NuevoPedido datos) { return service.crear(datos); }
    @GetMapping public List<PedidoVista> listar() { return service.listar(); }
    @GetMapping("/{id}") public PedidoVista buscar(@PathVariable Long id) { return service.buscar(id); }
    @PatchMapping("/{id}/estado")
    public PedidoVista cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstado datos) {
        return service.cambiarEstado(id, datos.estado());
    }
}
