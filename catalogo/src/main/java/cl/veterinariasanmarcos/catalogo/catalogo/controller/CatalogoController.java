package cl.veterinariasanmarcos.catalogo.catalogo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.veterinariasanmarcos.catalogo.catalogo.dto.CatalogoListadoDTO;
import cl.veterinariasanmarcos.catalogo.catalogo.model.Catalogo;
import cl.veterinariasanmarcos.catalogo.catalogo.service.CatalogoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Tag(name = "Catalogo", description = "API de gestión del catálogo de productos veterinarios")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar producto", description = "Crea y registra un nuevo producto en el catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/agregar")
    public ResponseEntity<Catalogo> crearProducto(
            @Valid @RequestBody Catalogo catalogo) {

        Catalogo nuevo = service.guardarProducto(catalogo);

        return ResponseEntity.status(201).body(nuevo);
    }

    @Operation(summary = "Listar productos", description = "Obtiene una lista de todos los productos del catálogo")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping("/listar")
    public List<Catalogo> obtenerProducto() {
        return service.obtenerProducto();
    }

    @Operation(summary = "Buscar producto por ID", description = "Busca un producto específico a través de su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Catalogo> obtenerPorId(
            @PathVariable Integer id) {

        Catalogo catalogo = service.obtenerPorId(id);

        if (catalogo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(catalogo);
    }

    @Operation(summary = "Buscar producto por categoría", description = "Busca productos según su categoría (Antibióticos, Vacunas, etc.)")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada")
    @GetMapping("/buscar")
    public List<Catalogo> buscarPorCategoria(
            @RequestParam String categoria) {

        return service.buscarPorTipo(categoria);
    }

    @Operation(summary = "Actualizar producto", description = "Cambia los datos de un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Catalogo> actualizarProducto(
            @PathVariable Integer id,
            @Valid @RequestBody Catalogo catalogo) {

        Catalogo actualizado = service.actualizarProducto(id, catalogo);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar producto", description = "Borra un producto del catálogo a través del ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Integer id) {

        Catalogo catalogo = service.obtenerPorId(id);

        if (catalogo == null) {
            return ResponseEntity.notFound().build();
        }

        service.eliminarProducto(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar mediante DTO", description = "Obtiene una lista resumida de productos mediante el DTO")
    @ApiResponse(responseCode = "200", description = "DTO obtenido correctamente")
    @GetMapping("/listar-dto")
    public List<CatalogoListadoDTO> listarDTO() {
        return service.listarDTO();
    }
}
