package cl.veterinariasanmarcos.productos.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.veterinariasanmarcos.productos.dto.DatosReserva;
import cl.veterinariasanmarcos.productos.dto.ReservaVista;
import cl.veterinariasanmarcos.productos.model.Reserva;
import cl.veterinariasanmarcos.productos.service.ReservaService;

@WebMvcTest(ReservaController.class)
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Test
    void reservarStock() throws Exception {
        String reservaJson = """
                {
                    "productos": [
                        {
                            "codigo": "A",
                            "cantidad": 2
                        }
                    ]
                }
                """;

        ReservaVista reserva = new ReservaVista(
                "compra-1",
                Reserva.Estado.RESERVADA,
                List.of()
        );

        when(reservaService.reservar(eq("compra-1"), any(DatosReserva.class)))
                .thenReturn(reserva);

        mockMvc.perform(put("/api/reservas-stock/compra-1")
                .contentType("application/json")
                .content(reservaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESERVADA"));

        verify(reservaService).reservar(eq("compra-1"), any(DatosReserva.class));
    }

    @Test
    void liberarStock() throws Exception {
        doNothing().when(reservaService).liberar("compra-1");

        mockMvc.perform(delete("/api/reservas-stock/compra-1"))
                .andExpect(status().isNoContent());

        verify(reservaService).liberar("compra-1");
    }

    @Test
    void rechazarReservaSinProductos() throws Exception {
        String reservaJson = """
                {
                    "productos": []
                }
                """;

        mockMvc.perform(put("/api/reservas-stock/compra-1")
                .contentType("application/json")
                .content(reservaJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reservaService);
    }
}
