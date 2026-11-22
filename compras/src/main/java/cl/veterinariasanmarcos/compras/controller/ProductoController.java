package cl.veterinariasanmarcos.compras.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Listar productos de Compras",
        description = "Devuelve el inventario de productos administrado por Compras."
    )
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @Operation(
        summary = "Filtrar productos por categoría",
        description = "Devuelve los productos del inventario de Compras que pertenecen a la categoría indicada."
    )
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
    }

    @Operation(
        summary = "Filtrar productos por especie",
        description = "Devuelve los productos del inventario de Compras para la especie indicada."
    )
    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<ProductoDTO>> listarPorEspecie(@PathVariable String especie) {
        return ResponseEntity.ok(productoService.listarPorEspecie(especie));
    }

    @Operation(
        summary = "Listar productos con bajo stock",
        description = "Devuelve los productos activos con existencias iguales o inferiores al umbral indicado. El valor predeterminado es 20."
    )
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoDTO>> listarBajoStock(
            @RequestParam(defaultValue = "20") int umbral) {
        return ResponseEntity.ok(productoService.listarBajoStock(umbral));
    }

    @Operation(
        summary = "Buscar producto por código",
        description = "Devuelve el producto del inventario de Compras identificado por su código."
    )
    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.obtenerPorCodigo(codigo));
    }

    @Operation(
        summary = "Guardar producto de Compras",
        description = "Crea un producto o actualiza el producto existente con el mismo código."
    )
    @PostMapping
    public ResponseEntity<ProductoDTO> crearOActualizar(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.guardarOActualizar(dto));
    }

    @Operation(
        summary = "Actualizar stock de un producto",
        description = "Establece las existencias del producto indicado usando el parámetro stock."
    )
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

    @Operation(
        summary = "Desactivar producto",
        description = "Marca como inactivo el producto del inventario de Compras identificado por su código."
    )
    @DeleteMapping("/{codigo}")
    public ResponseEntity desactivar(@PathVariable String codigo) {
        productoService.desactivar(codigo);
        return ResponseEntity.ok(Map.of(
            "mensaje",
            "Producto desactivado correctamente"
        ));
    }
}
