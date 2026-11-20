package cl.veterinariasanmarcos.compras.controller;

import cl.veterinariasanmarcos.compras.dto.CompraRequestDTO;
import cl.veterinariasanmarcos.compras.dto.CompraResponseDTO;
import cl.veterinariasanmarcos.compras.model.EstadoCompra;
import cl.veterinariasanmarcos.compras.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraResponseDTO> crearCompra(@Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.crearCompra(dto));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarTodas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<CompraResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(compraService.listarPorDueno(duenoId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CompraResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCompra estado) {
        return ResponseEntity.ok(compraService.cambiarEstado(id, estado));
    }

    @GetMapping("/reportes/ventas")
    public ResponseEntity<List<CompraResponseDTO>> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(compraService.reporteVentasPorPeriodo(inicio, fin));
    }
}