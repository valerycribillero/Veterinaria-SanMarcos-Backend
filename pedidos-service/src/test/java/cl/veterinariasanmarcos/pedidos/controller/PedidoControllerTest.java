package cl.veterinariasanmarcos.pedidos.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import cl.veterinariasanmarcos.pedidos.dto.NuevoPedido;
import cl.veterinariasanmarcos.pedidos.dto.PedidoVista;
import cl.veterinariasanmarcos.pedidos.model.Pedido;
import cl.veterinariasanmarcos.pedidos.service.PedidoService;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    private PedidoVista pedido;

    @BeforeEach
    void prepararPedido() {
        PedidoVista.Detalle detalle = new PedidoVista.Detalle(
                "A",
                "Alimento para perro",
                new BigDecimal("4200"),
                2,
                new BigDecimal("8400")
        );

        pedido = new PedidoVista(
                1L,
                "Sofía Pérez",
                "sofia@example.com",
                "Av. Brasil 2140",
                "Transferencia",
                LocalDateTime.of(2030, 1, 15, 12, 0),
                Pedido.Estado.CONFIRMADO,
                new BigDecimal("8400"),
                List.of(detalle)
        );
    }

    @Test
    void listarPedidos() throws Exception {
        when(pedidoService.listar()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void crearPedido() throws Exception {
        String pedidoJson = """
                {
                    "nombreCompleto": "Sofía Pérez",
                    "correo": "sofia@example.com",
                    "direccion": "Av. Brasil 2140",
                    "metodo": "Transferencia",
                    "productos": [
                        {
                            "codigo": "A",
                            "cantidad": 2
                        }
                    ]
                }
                """;

        when(pedidoService.crear(eq("compra-1"), any(NuevoPedido.class)))
                .thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                .header("Idempotency-Key", "compra-1")
                .contentType("application/json")
                .content(pedidoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(8400));
    }

    @Test
    void obtenerPedidoPorId() throws Exception {
        when(pedidoService.buscar(1L)).thenReturn(pedido);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Sofía Pérez"));
    }

    @Test
    void confirmarPedido() throws Exception {
        when(pedidoService.confirmar(1L)).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void cambiarEstadoDelPedido() throws Exception {
        String estadoJson = """
                {
                    "estado": "ENVIADO"
                }
                """;

        PedidoVista pedidoEnviado = new PedidoVista(
                pedido.id(),
                pedido.nombreCompleto(),
                pedido.correo(),
                pedido.direccion(),
                pedido.metodo(),
                pedido.fecha(),
                Pedido.Estado.ENVIADO,
                pedido.total(),
                pedido.productos()
        );

        when(pedidoService.cambiarEstado(1L, Pedido.Estado.ENVIADO))
                .thenReturn(pedidoEnviado);

        mockMvc.perform(patch("/api/pedidos/1/estado")
                .contentType("application/json")
                .content(estadoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    void rechazarPedidoSinProductos() throws Exception {
        String pedidoJson = """
                {
                    "nombreCompleto": "Sofía Pérez",
                    "correo": "sofia@example.com",
                    "direccion": "Av. Brasil 2140",
                    "metodo": "Transferencia",
                    "productos": []
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                .contentType("application/json")
                .content(pedidoJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pedidoService);
    }

    @Test
    void pedidoInexistenteDevuelve404() throws Exception {
        when(pedidoService.buscar(99L)).thenThrow(
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                )
        );

        mockMvc.perform(get("/api/pedidos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado"));
    }
}
