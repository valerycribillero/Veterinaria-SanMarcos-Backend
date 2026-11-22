package cl.veterinariasanmarcos.servicios.controller;

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

    @GetMapping
    public ResponseEntity<List<ServicioCatalogoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ServicioCatalogoDTO>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(service.listarPorCategoria(categoria));
    }

    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<ServicioCatalogoDTO>> listarPorEspecie(@PathVariable String especie) {
        return ResponseEntity.ok(service.listarPorEspecie(especie));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ServicioCatalogoDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ServicioCatalogoDTO> crearOActualizar(@Valid @RequestBody ServicioCatalogoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarOActualizar(dto));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity desactivar(@PathVariable String codigo) {
        service.desactivar(codigo);
        return ResponseEntity.ok(Map.of("mensaje", "Servicio desactivado correctamente"));
    }
}