package cl.veterinariasanmarcos.ficha.controller;

import cl.veterinariasanmarcos.ficha.dto.PacienteRequestDTO;
import cl.veterinariasanmarcos.ficha.dto.PacienteResponseDTO;
import cl.veterinariasanmarcos.ficha.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fichas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarFichas() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> obtenerFicha(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    // Endpoint para privacidad: el Dueño solo consulta sus propias mascotas
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<PacienteResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(pacienteService.listarPorDueno(duenoId));
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crearFicha(
            @Valid @RequestBody PacienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizarFicha(
        @PathVariable Long id,
        @Valid @RequestBody PacienteRequestDTO dto
    ) {
        return ResponseEntity.ok(pacienteService.actualizar(
            id,
            dto
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity desactivarFicha(@PathVariable Long id) {
        pacienteService.desactivar(id);
        return ResponseEntity.ok(Map.of(
            "mensaje",
            "Ficha desactivada correctamente"
        ));
    }

    // Reporte de pacientes nuevos en un rango de fechas
    @GetMapping("/reportes/nuevos")
    public ResponseEntity contarPacientesNuevos(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) {
        return ResponseEntity.ok(
                Map.of(
                    "pacientesNuevos",
                    pacienteService.contarPacientesNuevos(
                        inicio,
                        fin
                    )
                ));
    }
}
