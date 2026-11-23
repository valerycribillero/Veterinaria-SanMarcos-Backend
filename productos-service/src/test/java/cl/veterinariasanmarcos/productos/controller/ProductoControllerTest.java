package cl.veterinariasanmarcos.productos.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.veterinariasanmarcos.productos.dto.DatosProducto;
import cl.veterinariasanmarcos.productos.model.Producto;
import cl.veterinariasanmarcos.productos.service.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void prepararProducto() {
        producto = new Producto("A");
        producto.setNombre("Alimento para perro");
        producto.setCategoria("Alimentos");
        producto.setPrecio(new BigDecimal("4200"));
        producto.setStock(5);
        producto.setImagen("/img/alimento.jpg");
        producto.setPrincipioActivo("");
        producto.setPresentacion("Bolsa");
        producto.setEspecie("Perro");
    }

    @Test
    void listarProductos() throws Exception {
        when(productoService.listar()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("A"));
    }

    @Test
    void obtenerProductoPorCodigo() throws Exception {
        when(productoService.buscar("A")).thenReturn(producto);

        mockMvc.perform(get("/api/productos/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Alimento para perro"));
    }

    @Test
    void crearProducto() throws Exception {
        String productoJson = """
                {
                    "codigo": "A",
                    "nombre": "Alimento para perro",
                    "categoria": "Alimentos",
                    "precio": 4200,
                    "stock": 5,
                    "imagen": "/img/alimento.jpg",
                    "principioActivo": "",
                    "presentacion": "Bolsa",
                    "especie": "Perro"
                }
                """;

        when(productoService.crear(any(DatosProducto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType("application/json")
                .content(productoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("A"))
                .andExpect(jsonPath("$.precio").value(4200));
    }

    @Test
    void actualizarProducto() throws Exception {
        String productoJson = """
                {
                    "codigo": "A",
                    "nombre": "Alimento actualizado",
                    "categoria": "Alimentos",
                    "precio": 4200,
                    "stock": 5,
                    "imagen": "/img/alimento.jpg",
                    "principioActivo": "",
                    "presentacion": "Bolsa",
                    "especie": "Perro"
                }
                """;

        producto.setNombre("Alimento actualizado");
        when(productoService.actualizar(eq("A"), any(DatosProducto.class)))
                .thenReturn(producto);

        mockMvc.perform(put("/api/productos/A")
                .contentType("application/json")
                .content(productoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Alimento actualizado"));

        verify(productoService).actualizar(eq("A"), any(DatosProducto.class));
    }

    @Test
    void actualizarStock() throws Exception {
        String stockJson = """
                {
                    "stock": 8
                }
                """;

        producto.setStock(8);
        when(productoService.actualizarStock("A", 8)).thenReturn(producto);

        mockMvc.perform(patch("/api/productos/A/stock")
                .contentType("application/json")
                .content(stockJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));
    }

    @Test
    void desactivarProducto() throws Exception {
        doNothing().when(productoService).eliminar("A");

        mockMvc.perform(delete("/api/productos/A"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar("A");
    }

    @Test
    void rechazarStockNegativo() throws Exception {
        String stockJson = """
                {
                    "stock": -1
                }
                """;

        mockMvc.perform(patch("/api/productos/A/stock")
                .contentType("application/json")
                .content(stockJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productoService);
    }
}
