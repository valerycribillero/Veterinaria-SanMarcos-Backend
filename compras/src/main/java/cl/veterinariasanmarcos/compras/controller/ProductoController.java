package cl.veterinariasanmarcos.compras.controller;

import cl.veterinariasanmarcos.compras.dto.ProductoDTO;
import cl.veterinariasanmarcos.compras.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
    }

    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<ProductoDTO>> listarPorEspecie(@PathVariable String especie) {
        return ResponseEntity.ok(productoService.listarPorEspecie(especie));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoDTO>> listarBajoStock(
            @RequestParam(defaultValue = "20") int umbral) {
        return ResponseEntity.ok(productoService.listarBajoStock(umbral));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.obtenerPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearOActualizar(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.guardarOActualizar(dto));
    }

    @PatchMapping("/{codigo}/stock")
    public ResponseEntity<ProductoDTO> actualizarStock(
        @PathVariable String codigo,
        @RequestParam int stock
    ) {
        return ResponseEntity.ok(productoService.actualizarStock(
            codigo,
            stock
        ));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity desactivar(@PathVariable String codigo) {
        productoService.desactivar(codigo);
        return ResponseEntity.ok(Map.of(
            "mensaje",
            "Producto desactivado correctamente"
        ));
    }
}
