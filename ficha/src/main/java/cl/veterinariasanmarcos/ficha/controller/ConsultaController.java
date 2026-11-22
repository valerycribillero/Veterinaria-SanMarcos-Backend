package cl.veterinariasanmarcos.ficha.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Listar consultas veterinarias",
        description = "Devuelve las consultas registradas. Usa veterinarioId para filtrar por profesional."
    )
    @GetMapping
    public List<ConsultaResponseDTO> listarConsultas(
        @RequestParam(required = false) Long veterinarioId
    ) {
        return consultaService.listarConsultas(veterinarioId);
    }

    @Operation(
        summary = "Buscar consulta por ID",
        description = "Devuelve la información de la consulta veterinaria indicada."
    )
    @GetMapping("/{id}")
    public ConsultaResponseDTO obtenerPorId(@PathVariable Long id) {
        return consultaService.obtenerPorId(id);
    }

    @Operation(
        summary = "Registrar consulta veterinaria",
        description = "Guarda una consulta clínica con los datos del paciente y de la atención."
    )
    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> registrarConsulta(
            @Valid @RequestBody ConsultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consultaService.registrarConsulta(dto));
    }

    @Operation(
        summary = "Consultar historial de atenciones",
        description = "Devuelve las consultas clínicas asociadas al paciente indicado."
    )
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaResponseDTO>> historialPorPaciente(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(consultaService.listarPorPaciente(pacienteId));
    }

    @Operation(
        summary = "Consultar historial de vacunas",
        description = "Devuelve los registros de vacunación del paciente indicado."
    )
    @GetMapping("/paciente/{pacienteId}/vacunas")
    public ResponseEntity<List<VacunaRegistroDTO>> historialVacunas(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(consultaService.obtenerHistorialVacunas(pacienteId));
    }

    @Operation(
        summary = "Listar vacunas por vencer",
        description = "Devuelve las vacunas cuyo vencimiento está dentro del plazo indicado. Por defecto consulta 15 días."
    )
    @GetMapping("/vacunas/por-vencer")
    public ResponseEntity<List<VacunaRegistroDTO>> vacunasPorVencer(
            @RequestParam(defaultValue = "15") int dias) {
        return ResponseEntity.ok(consultaService.obtenerVacunasPorVencer(dias));
    }

    // Reporte de atenciones por período
    @Operation(
        summary = "Consultar atenciones por período",
        description = "Devuelve las atenciones entre inicio y fin, expresados en formato de fecha y hora ISO."
    )
    @GetMapping("/reportes/atenciones")
    public ResponseEntity<List<ConsultaResponseDTO>> reporteAtenciones(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) {
        return ResponseEntity.ok(consultaService.reporteAtencionesPorPeriodo(
            inicio,
            fin
        ));
    }
}
