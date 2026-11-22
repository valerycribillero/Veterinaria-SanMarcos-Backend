package cl.veterinariasanmarcos.productos.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Listar productos de la tienda",
        description = "Devuelve los productos de la tienda con sus precios y existencias."
    )
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @Operation(
        summary = "Buscar producto de la tienda",
        description = "Devuelve los datos del producto identificado por su código."
    )
    @GetMapping("/{codigo}")
    public Producto buscar(@PathVariable String codigo) {
        return service.buscar(codigo);
    }

    @Operation(
        summary = "Crear producto de la tienda",
        description = "Agrega un producto al catálogo de la tienda con los datos enviados."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody DatosProducto datos) {
        return service.crear(datos);
    }

    @Operation(
        summary = "Actualizar producto de la tienda",
        description = "Modifica los datos del producto de la tienda identificado por su código."
    )
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

    @Operation(
        summary = "Actualizar stock de la tienda",
        description = "Establece las existencias del producto indicado usando el valor stock del cuerpo de la petición."
    )
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

    @Operation(
        summary = "Eliminar producto de la tienda",
        description = "Retira del catálogo de la tienda el producto identificado por su código."
    )
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigo) {
        service.eliminar(codigo);
    }
}
