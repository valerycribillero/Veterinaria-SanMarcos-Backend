package cl.veterinariasanmarcos.detalleconsultas.controller;

import cl.veterinariasanmarcos.detalleconsultas.dto.*;
import cl.veterinariasanmarcos.detalleconsultas.service.DetalleConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detalle-consultas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DetalleConsultaController {

    private final DetalleConsultaService service;

    @PostMapping
    public ResponseEntity<DetalleConsultaResponseDTO> registrarDetalle(
            @Valid @RequestBody DetalleConsultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarDetalle(dto));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> registrarLote(
            @Valid @RequestBody List<DetalleConsultaRequestDTO> listaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarLote(listaDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleConsultaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<ResumenConsultaClinicaDTO> obtenerResumenConsulta(@PathVariable Long consultaId) {
        return ResponseEntity.ok(service.obtenerResumenConsulta(consultaId));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/recetas")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> listarRecetasPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.listarRecetasPorPaciente(pacienteId));
    }

    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(service.listarPorDueno(duenoId));
    }

    @GetMapping("/proximas-dosis")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> proximasDosis(
            @RequestParam(defaultValue = "15") int dias) {
        return ResponseEntity.ok(service.obtenerProximasDosis(dias));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleConsultaResponseDTO> actualizarDetalle(
            @PathVariable Long id,
            @Valid @RequestBody DetalleConsultaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarDetalle(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity eliminarDetalle(@PathVariable Long id) {
        service.eliminarDetalle(id);
        return ResponseEntity.ok(Map.of("mensaje", "Detalle de consulta eliminado correctamente"));
    }
}