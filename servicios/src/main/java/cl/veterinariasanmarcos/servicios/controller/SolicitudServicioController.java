package cl.veterinariasanmarcos.servicios.controller;

import io.swagger.v3.oas.annotations.Operation;
import cl.veterinariasanmarcos.servicios.dto.*;
import cl.veterinariasanmarcos.servicios.model.EstadoSolicitud;
import cl.veterinariasanmarcos.servicios.service.SolicitudServicioService;
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
@RequestMapping("/api/solicitudes-servicios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SolicitudServicioController {

    private final SolicitudServicioService service;

    @Operation(
        summary = "Solicitar servicio veterinario",
        description = "Registra una nueva solicitud de servicio veterinario con los datos enviados."
    )
    @PostMapping
    public ResponseEntity<SolicitudServicioResponseDTO> crearSolicitud(@Valid @RequestBody SolicitudServicioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSolicitud(dto));
    }

    @Operation(
        summary = "Listar solicitudes de servicio",
        description = "Devuelve las solicitudes de servicio registradas."
    )
    @GetMapping
    public ResponseEntity<List<SolicitudServicioResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @Operation(
        summary = "Buscar solicitud por ID",
        description = "Devuelve la solicitud de servicio identificada por su ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudServicioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(
        summary = "Listar solicitudes por dueño",
        description = "Devuelve las solicitudes asociadas al identificador del dueño indicado."
    )
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<SolicitudServicioResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(service.listarPorDueno(duenoId));
    }

    @Operation(
        summary = "Filtrar solicitudes por estado",
        description = "Devuelve las solicitudes que tienen el estado indicado."
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudServicioResponseDTO>> listarPorEstado(@PathVariable EstadoSolicitud estado) {
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @Operation(
        summary = "Gestionar solicitud desde recepción",
        description = "Aplica la decisión de recepción a la solicitud, incluida una propuesta de reagendamiento cuando corresponda."
    )
    @PutMapping("/{id}/gestionar")
    public ResponseEntity<SolicitudServicioResponseDTO> gestionarPorRecepcion(
            @PathVariable Long id,
            @Valid @RequestBody GestionRecepcionDTO dto) {
        return ResponseEntity.ok(service.gestionarPorRecepcion(id, dto));
    }

    @Operation(
        summary = "Aceptar reagendamiento",
        description = "Confirma la nueva fecha propuesta para la solicitud y verifica el identificador del dueño."
    )
    @PutMapping("/{id}/aceptar-reagendamiento")
    public ResponseEntity<SolicitudServicioResponseDTO> aceptarReagendamiento(
            @PathVariable Long id,
            @RequestParam Long duenoId) {
        return ResponseEntity.ok(service.aceptarReagendamiento(id, duenoId));
    }

    @Operation(
        summary = "Consultar agenda de servicios",
        description = "Devuelve los servicios agendados en el período indicado por inicio y fin."
    )
    @GetMapping("/agenda")
    public ResponseEntity<List<SolicitudServicioResponseDTO>> obtenerAgenda(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(service.agendaPorRango(inicio, fin));
    }

    @Operation(
        summary = "Cancelar solicitud de servicio",
        description = "Cambia a cancelada la solicitud identificada por su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity cancelarSolicitud(@PathVariable Long id) {
        service.cancelarSolicitud(id);
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud cancelada correctamente"));
    }
}
