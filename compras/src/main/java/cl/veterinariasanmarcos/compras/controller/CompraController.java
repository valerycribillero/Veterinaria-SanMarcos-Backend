package cl.veterinariasanmarcos.compras.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Registrar compra",
        description = "Guarda una compra con los datos y artículos enviados en el cuerpo de la petición."
    )
    @PostMapping
    public ResponseEntity<CompraResponseDTO> crearCompra(@Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.crearCompra(dto));
    }

    @Operation(
        summary = "Listar compras",
        description = "Devuelve las compras registradas."
    )
    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarTodas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @Operation(
        summary = "Buscar compra por ID",
        description = "Devuelve la compra indicada y sus datos asociados."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @Operation(
        summary = "Listar compras de un dueño",
        description = "Devuelve las compras asociadas al identificador del dueño indicado."
    )
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<CompraResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(compraService.listarPorDueno(duenoId));
    }

    @Operation(
        summary = "Cambiar estado de compra",
        description = "Actualiza el estado de una compra usando el parámetro estado."
    )
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CompraResponseDTO> cambiarEstado(
        @PathVariable Long id,
        @RequestParam EstadoCompra estado
    ) {
        return ResponseEntity.ok(compraService.cambiarEstado(
            id,
            estado
        ));
    }

    @Operation(
        summary = "Consultar ventas por período",
        description = "Devuelve las compras del período delimitado por inicio y fin en formato de fecha y hora ISO."
    )
    @GetMapping("/reportes/ventas")
    public ResponseEntity<List<CompraResponseDTO>> reporteVentas(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) {
        return ResponseEntity.ok(compraService.reporteVentasPorPeriodo(
            inicio,
            fin
        ));
    }
}
