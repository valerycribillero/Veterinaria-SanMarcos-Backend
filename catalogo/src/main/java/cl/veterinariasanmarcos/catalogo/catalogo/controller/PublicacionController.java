package cl.veterinariasanmarcos.catalogo.catalogo.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.catalogo.catalogo.model.Publicacion;
import cl.veterinariasanmarcos.catalogo.catalogo.repository.PublicacionRepository;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PublicacionController {
    private final PublicacionRepository repository;

    public PublicacionController(PublicacionRepository repository) {
        this.repository = repository;
    }

    @Operation(
        summary = "Listar publicaciones",
        description = "Devuelve las publicaciones ordenadas desde la más reciente por ID."
    )
    @GetMapping
    public List<Publicacion> listar() {
        return repository.findAllByOrderByIdDesc();
    }

    @Operation(
        summary = "Buscar publicación por ID",
        description = "Devuelve el contenido de una publicación específica."
    )
    @GetMapping("/{id}")
    public Publicacion buscar(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Publicación no encontrada"));
    }

    @Operation(
        summary = "Crear publicación",
        description = "Guarda el contenido enviado como una publicación nueva."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Publicacion crear(@Valid @RequestBody Publicacion datos) {
        datos.setId(null);
        return repository.save(datos);
    }

    @Operation(
        summary = "Actualizar publicación",
        description = "Reemplaza los datos de la publicación identificada por su ID."
    )
    @PutMapping("/{id}")
    public Publicacion actualizar(@PathVariable Long id, @Valid @RequestBody Publicacion datos) {
        buscar(id);
        datos.setId(id);
        return repository.save(datos);
    }

    @Operation(
        summary = "Eliminar publicación",
        description = "Borra la publicación identificada por su ID."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        repository.delete(buscar(id));
    }
}
