package cl.veterinariasanmarcos.productos.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.veterinariasanmarcos.productos.dto.DatosProducto;
import cl.veterinariasanmarcos.productos.model.Producto;
import cl.veterinariasanmarcos.productos.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ProductoController {
    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    public record Stock(
            @NotNull
            @Min(0)
            @Max(1000000)
            Integer stock
    ) {
    }

    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @GetMapping("/{codigo}")
    public Producto buscar(@PathVariable String codigo) {
        return service.buscar(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody DatosProducto datos) {
        return service.crear(datos);
    }

    @PutMapping("/{codigo}")
    public Producto actualizar(
        @PathVariable String codigo,
        @Valid @RequestBody DatosProducto datos
    ) {
        return service.actualizar(
            codigo,
            datos
        );
    }

    @PatchMapping("/{codigo}/stock")
    public Producto stock(
        @PathVariable String codigo,
        @Valid @RequestBody Stock datos
    ) {
        return service.actualizarStock(
            codigo,
            datos.stock()
        );
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigo) {
        service.eliminar(codigo);
    }
}
