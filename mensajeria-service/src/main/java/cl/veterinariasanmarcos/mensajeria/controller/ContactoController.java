package cl.veterinariasanmarcos.mensajeria.controller;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contacto crear(@Valid @RequestBody NuevoContacto datos) {
        return service.crear(datos);
    }

    @GetMapping
    public List<Contacto> listar() {
        return service.listar();
    }

    @PutMapping("/{id}/atendido")
    public Contacto marcarAtendido(@PathVariable Long id) {
        return service.marcarAtendido(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
