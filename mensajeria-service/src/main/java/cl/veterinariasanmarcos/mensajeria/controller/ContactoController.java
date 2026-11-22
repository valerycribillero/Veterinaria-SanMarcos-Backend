package cl.veterinariasanmarcos.mensajeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.veterinariasanmarcos.mensajeria.dto.NuevoContacto;
import cl.veterinariasanmarcos.mensajeria.model.Contacto;
import cl.veterinariasanmarcos.mensajeria.service.ContactoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contactos")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ContactoController {

    private final ContactoService service;

    public ContactoController(ContactoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Enviar mensaje de contacto",
        description = "Guarda el mensaje enviado desde el formulario de contacto junto con los datos del remitente."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contacto crear(@Valid @RequestBody NuevoContacto datos) {
        return service.crear(datos);
    }

    @Operation(
        summary = "Listar mensajes de contacto",
        description = "Devuelve los mensajes recibidos y su estado de atención."
    )
    @GetMapping
    public List<Contacto> listar() {
        return service.listar();
    }

    @Operation(
        summary = "Marcar mensaje como atendido",
        description = "Actualiza el mensaje indicado por ID para que quede marcado como atendido."
    )
    @PutMapping("/{id}/atendido")
    public Contacto marcarAtendido(@PathVariable Long id) {
        return service.marcarAtendido(id);
    }

    @Operation(
        summary = "Eliminar mensaje de contacto",
        description = "Borra el mensaje de contacto identificado por su ID."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
