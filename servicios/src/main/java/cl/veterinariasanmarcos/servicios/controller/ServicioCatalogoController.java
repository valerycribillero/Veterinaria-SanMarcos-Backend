package cl.veterinariasanmarcos.servicios.controller;

import io.swagger.v3.oas.annotations.Operation;
import cl.veterinariasanmarcos.servicios.dto.ServicioCatalogoDTO;
import cl.veterinariasanmarcos.servicios.service.ServicioCatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ServicioCatalogoController {

    private final ServicioCatalogoService service;

    @Operation(
        summary = "Listar servicios veterinarios",
        description = "Devuelve los servicios veterinarios disponibles en el catálogo."
    )
    @GetMapping
    public ResponseEntity<List<ServicioCatalogoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(
        summary = "Filtrar servicios por categoría",
        description = "Devuelve los servicios que pertenecen a la categoría indicada."
    )
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ServicioCatalogoDTO>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(service.listarPorCategoria(categoria));
    }

    @Operation(
        summary = "Filtrar servicios por especie",
        description = "Devuelve los servicios que corresponden a la especie indicada."
    )
    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<ServicioCatalogoDTO>> listarPorEspecie(@PathVariable String especie) {
        return ResponseEntity.ok(service.listarPorEspecie(especie));
    }

    @Operation(
        summary = "Buscar servicio por código",
        description = "Devuelve el servicio veterinario identificado por su código."
    )
    @GetMapping("/{codigo}")
    public ResponseEntity<ServicioCatalogoDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @Operation(
        summary = "Guardar servicio veterinario",
        description = "Crea un servicio en el catálogo o actualiza el que ya tiene el mismo código."
    )
    @PostMapping
    public ResponseEntity<ServicioCatalogoDTO> crearOActualizar(@Valid @RequestBody ServicioCatalogoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarOActualizar(dto));
    }

    @Operation(
        summary = "Desactivar servicio veterinario",
        description = "Marca como inactivo el servicio identificado por su código."
    )
    @DeleteMapping("/{codigo}")
    public ResponseEntity desactivar(@PathVariable String codigo) {
        service.desactivar(codigo);
        return ResponseEntity.ok(Map.of("mensaje", "Servicio desactivado correctamente"));
    }
}
