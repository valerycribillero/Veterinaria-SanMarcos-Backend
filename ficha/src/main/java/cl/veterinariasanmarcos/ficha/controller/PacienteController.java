package cl.veterinariasanmarcos.ficha.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Listar fichas clínicas",
        description = "Devuelve las fichas de los pacientes registrados."
    )
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarFichas() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @Operation(
        summary = "Buscar ficha por ID",
        description = "Devuelve los datos de la ficha clínica del paciente indicado."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> obtenerFicha(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    // Endpoint para privacidad: el Dueño solo consulta sus propias mascotas
    @Operation(
        summary = "Listar fichas por dueño",
        description = "Devuelve las fichas clínicas asociadas al identificador del dueño."
    )
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<PacienteResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(pacienteService.listarPorDueno(duenoId));
    }

    @Operation(
        summary = "Crear ficha clínica",
        description = "Registra una ficha con los datos del paciente enviados en el cuerpo de la petición."
    )
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crearFicha(
            @Valid @RequestBody PacienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.crear(dto));
    }

    @Operation(
        summary = "Actualizar ficha clínica",
        description = "Modifica los datos de una ficha clínica existente."
    )
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

    @Operation(
        summary = "Desactivar ficha clínica",
        description = "Marca como inactiva la ficha del paciente identificado por su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity desactivarFicha(@PathVariable Long id) {
        pacienteService.desactivar(id);
        return ResponseEntity.ok(Map.of(
            "mensaje",
            "Ficha desactivada correctamente"
        ));
    }

    // Reporte de pacientes nuevos en un rango de fechas
    @Operation(
        summary = "Contar pacientes nuevos",
        description = "Devuelve el número de pacientes registrados entre inicio y fin, expresados en formato de fecha y hora ISO."
    )
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
