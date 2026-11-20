package cl.veterinariasanmarcos.compras.controller;

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

    @GetMapping
    public ResponseEntity<List<ProductoServicioDTO>> listarTodo() {
        return ResponseEntity.ok(catalogoService.listarTodo());
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoServicioDTO>> listarMedicamentosYVacunas() {
        return ResponseEntity.ok(catalogoService.listarPorTipo(TipoItem.MEDICAMENTO_VACUNA));
    }

    @GetMapping("/servicios")
    public ResponseEntity<List<ProductoServicioDTO>> listarServicios() {
        return ResponseEntity.ok(catalogoService.listarPorTipo(TipoItem.SERVICIO));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoServicioDTO>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(catalogoService.listarPorCategoria(categoria));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoServicioDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(catalogoService.obtenerPorCodigo(codigo.toUpperCase()));
    }

    @PostMapping
    public ResponseEntity<ProductoServicioDTO> crearOActualizar(@Valid @RequestBody ProductoServicioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.guardarOActualizar(dto));
    }

    @PatchMapping("/{codigo}/stock")
    public ResponseEntity<ProductoServicioDTO> actualizarStock(
            @PathVariable String codigo,
            @RequestParam int stock) {
        return ResponseEntity.ok(catalogoService.actualizarStock(codigo.toUpperCase(), stock));
    }
}