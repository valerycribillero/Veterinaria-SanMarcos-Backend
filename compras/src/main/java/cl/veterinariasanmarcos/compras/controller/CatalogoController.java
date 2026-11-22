package cl.veterinariasanmarcos.compras.controller;

import io.swagger.v3.oas.annotations.Operation;
import cl.veterinariasanmarcos.compras.dto.ProductoServicioDTO;
import cl.veterinariasanmarcos.compras.model.TipoItem;
import cl.veterinariasanmarcos.compras.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @Operation(
        summary = "Listar el catálogo de compras",
        description = "Devuelve los productos y servicios disponibles en el catálogo de compras."
    )
    @GetMapping
    public ResponseEntity<List<ProductoServicioDTO>> listarTodo() {
        return ResponseEntity.ok(catalogoService.listarTodo());
    }

    @Operation(
        summary = "Listar medicamentos y vacunas",
        description = "Devuelve los ítems del catálogo clasificados como medicamentos o vacunas."
    )
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoServicioDTO>> listarMedicamentosYVacunas() {
        return ResponseEntity.ok(catalogoService.listarPorTipo(TipoItem.MEDICAMENTO_VACUNA));
    }

    @Operation(
        summary = "Listar servicios del catálogo",
        description = "Devuelve los ítems del catálogo de compras clasificados como servicios."
    )
    @GetMapping("/servicios")
    public ResponseEntity<List<ProductoServicioDTO>> listarServicios() {
        return ResponseEntity.ok(catalogoService.listarPorTipo(TipoItem.SERVICIO));
    }

    @Operation(
        summary = "Filtrar catálogo por categoría",
        description = "Devuelve los ítems del catálogo de compras que pertenecen a la categoría indicada."
    )
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoServicioDTO>> listarPorCategoria(
            @PathVariable String categoria) {
        return ResponseEntity.ok(catalogoService.listarPorCategoria(categoria));
    }

    @Operation(
        summary = "Buscar ítem del catálogo",
        description = "Devuelve el producto o servicio identificado por su código."
    )
    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoServicioDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(catalogoService.obtenerPorCodigo(codigo.toUpperCase()));
    }

    @Operation(
        summary = "Guardar ítem del catálogo",
        description = "Crea el producto o servicio enviado o actualiza el ítem que ya tiene ese código."
    )
    @PostMapping
    public ResponseEntity<ProductoServicioDTO> crearOActualizar(
            @Valid @RequestBody ProductoServicioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogoService.guardarOActualizar(dto));
    }

    @Operation(
        summary = "Actualizar stock del catálogo",
        description = "Establece el stock del ítem indicado usando el valor del parámetro stock."
    )
    @PatchMapping("/{codigo}/stock")
    public ResponseEntity<ProductoServicioDTO> actualizarStock(
        @PathVariable String codigo,
        @RequestParam int stock
    ) {
        return ResponseEntity.ok(catalogoService.actualizarStock(
            codigo.toUpperCase(),
            stock
        ));
    }
}
