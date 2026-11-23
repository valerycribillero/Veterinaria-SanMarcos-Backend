package cl.veterinariasanmarcos.ficha.controller;

import cl.veterinariasanmarcos.ficha.dto.*;
import cl.veterinariasanmarcos.ficha.service.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> registrarConsulta(@Valid @RequestBody ConsultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.registrarConsulta(dto));
    }

    @GetMapping("/{id}")
    public ConsultaResponseDTO obtenerPorId(@PathVariable Long id) {
        return consultaService.obtenerPorId(id);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaResponseDTO>> historialPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(consultaService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/vacunas")
    public ResponseEntity<List<VacunaRegistroDTO>> historialVacunas(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(consultaService.obtenerHistorialVacunas(pacienteId));
    }

    @GetMapping("/vacunas/por-vencer")
    public ResponseEntity<List<VacunaRegistroDTO>> vacunasPorVencer(@RequestParam(defaultValue = "15") int dias) {
        return ResponseEntity.ok(consultaService.obtenerVacunasPorVencer(dias));
    }

    // Reporte de atenciones por período
    @GetMapping("/reportes/atenciones")
    public ResponseEntity<List<ConsultaResponseDTO>> reporteAtenciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(consultaService.reporteAtencionesPorPeriodo(inicio, fin));
    }
}