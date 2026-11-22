package cl.veterinariasanmarcos.servicios.controller;

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

    @PostMapping
    public ResponseEntity<SolicitudServicioResponseDTO> crearSolicitud(@Valid @RequestBody SolicitudServicioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSolicitud(dto));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudServicioResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudServicioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<SolicitudServicioResponseDTO>> listarPorDueno(@PathVariable Long duenoId) {
        return ResponseEntity.ok(service.listarPorDueno(duenoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudServicioResponseDTO>> listarPorEstado(@PathVariable EstadoSolicitud estado) {
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @PutMapping("/{id}/gestionar")
    public ResponseEntity<SolicitudServicioResponseDTO> gestionarPorRecepcion(
            @PathVariable Long id,
            @Valid @RequestBody GestionRecepcionDTO dto) {
        return ResponseEntity.ok(service.gestionarPorRecepcion(id, dto));
    }

    @PutMapping("/{id}/aceptar-reagendamiento")
    public ResponseEntity<SolicitudServicioResponseDTO> aceptarReagendamiento(
            @PathVariable Long id,
            @RequestParam Long duenoId) {
        return ResponseEntity.ok(service.aceptarReagendamiento(id, duenoId));
    }

    @GetMapping("/agenda")
    public ResponseEntity<List<SolicitudServicioResponseDTO>> obtenerAgenda(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(service.agendaPorRango(inicio, fin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity cancelarSolicitud(@PathVariable Long id) {
        service.cancelarSolicitud(id);
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud cancelada correctamente"));
    }
}