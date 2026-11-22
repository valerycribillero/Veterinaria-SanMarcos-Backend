package cl.veterinariasanmarcos.mascota.mascota.controller;

import cl.veterinariasanmarcos.mascota.mascota.model.Mascota;
import cl.veterinariasanmarcos.mascota.mascota.service.MascotaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class MascotaController {

    @Autowired private MascotaService service;

    @Operation(
        summary = "Listar mascotas",
        description = "Devuelve las mascotas registradas. Si se envía clienteId, devuelve solo las mascotas de ese cliente."
    )
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
                @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @GetMapping
    public ResponseEntity<List<Mascota>> listar() {
        return ResponseEntity.ok(service.obtenerMascotas());
    }

    @Operation(
            summary = "Buscar mascota por ID",
            description = "Obtiene una mascota según su identificador")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Mascota encontrada"),
                @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> obtenerPorId(@PathVariable Long id) {
        Mascota mascota = service.obtenerPorId(id);
        return mascota == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(mascota);
    }

    @Operation(
        summary = "Listar mascotas",
        description = "Devuelve las mascotas registradas. Si se envía clienteId, devuelve solo las mascotas de ese cliente."
    )
    @GetMapping(params = "clienteId")
    public ResponseEntity<List<Mascota>> listarPorCliente(@RequestParam Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorClienteId(clienteId));
    }

    @Operation(summary = "Registrar mascota", description = "Permite registrar una nueva mascota")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Mascota registrada correctamente"),
                @ApiResponse(responseCode = "400", description = "Datos inválidos")
            })
    @PostMapping("/agregar")
    public ResponseEntity<Mascota> guardar(@Valid @RequestBody Mascota mascota) {
        Mascota nuevaMascota = service.guardarMascota(mascota);
        return ResponseEntity.status(201).body(nuevaMascota);
    }

    @Operation(
            summary = "Actualizar mascota",
            description = "Actualiza los datos de una mascota mediante su identificador")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Mascota actualizada correctamente"),
                @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Mascota> actualizar(
            @PathVariable Long id, @Valid @RequestBody Mascota mascota) {
        Mascota actualizada = service.actualizarMascota(id, mascota);
        return actualizada == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(actualizada);
    }

    @Operation(
            summary = "Eliminar mascota",
            description = "Elimina una mascota mediante su identificador")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.obtenerPorId(id) == null) return ResponseEntity.notFound().build();
        service.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}
