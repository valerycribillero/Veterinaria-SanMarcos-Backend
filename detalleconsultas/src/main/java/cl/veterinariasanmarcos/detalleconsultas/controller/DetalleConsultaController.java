package cl.veterinariasanmarcos.detalleconsultas.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Registrar detalle clínico",
        description = "Guarda un detalle clínico asociado a una consulta veterinaria."
    )
    @PostMapping
    public ResponseEntity<DetalleConsultaResponseDTO> registrarDetalle(
            @Valid @RequestBody DetalleConsultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarDetalle(dto));
    }

    @Operation(
        summary = "Registrar detalles en lote",
        description = "Guarda varios detalles clínicos a partir de la lista enviada en el cuerpo."
    )
    @PostMapping("/lote")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> registrarLote(
            @Valid @RequestBody List<DetalleConsultaRequestDTO> listaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarLote(listaDto));
    }

    @Operation(
        summary = "Buscar detalle clínico por ID",
        description = "Devuelve el detalle de consulta identificado por su ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<DetalleConsultaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(
        summary = "Consultar resumen clínico",
        description = "Devuelve el resumen de los detalles clínicos asociados a la consulta indicada."
    )
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<ResumenConsultaClinicaDTO> obtenerResumenConsulta(@PathVariable Long consultaId) {
        return ResponseEntity.ok(service.obtenerResumenConsulta(consultaId));
    }

    @Operation(
        summary = "Listar detalles de un paciente",
        description = "Devuelve los detalles de consultas asociados al identificador del paciente."
    )
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @Operation(
        summary = "Consultar recetas de un paciente",
        description = "Devuelve los detalles clasificados como recetas para el paciente indicado."
    )
    @GetMapping("/paciente/{pacienteId}/recetas")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> listarRecetasPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.listarRecetasPorPaciente(pacienteId));
    }

    @Operation(
        summary = "Listar detalles por dueño",
        description = "Devuelve los detalles de consultas asociados al dueño indicado."
    )
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(service.listarPorDueno(duenoId));
    }

    @Operation(
        summary = "Consultar próximas dosis",
        description = "Devuelve las dosis previstas dentro del número de días indicado. Por defecto consulta 15 días."
    )
    @GetMapping("/proximas-dosis")
    public ResponseEntity<List<DetalleConsultaResponseDTO>> proximasDosis(
            @RequestParam(defaultValue = "15") int dias) {
        return ResponseEntity.ok(service.obtenerProximasDosis(dias));
    }

    @Operation(
        summary = "Actualizar detalle clínico",
        description = "Modifica los datos del detalle de consulta identificado por su ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<DetalleConsultaResponseDTO> actualizarDetalle(
            @PathVariable Long id,
            @Valid @RequestBody DetalleConsultaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarDetalle(id, dto));
    }

    @Operation(
        summary = "Eliminar detalle clínico",
        description = "Elimina el detalle de consulta identificado por su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarDetalle(@PathVariable Long id) {
        service.eliminarDetalle(id);
        return ResponseEntity.ok(Map.of("mensaje", "Detalle de consulta eliminado correctamente"));
    }
}
