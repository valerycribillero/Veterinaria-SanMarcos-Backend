package cl.veterinariasanmarcos.agenda.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.veterinariasanmarcos.agenda.dto.DatosCita;
import cl.veterinariasanmarcos.agenda.model.Cita;
import cl.veterinariasanmarcos.agenda.model.Horario;
import cl.veterinariasanmarcos.agenda.service.CitaService;

@WebMvcTest(CitaController.class)
public class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaService citaService;

    private Cita cita;

    @BeforeEach
    void prepararCita() {
        Horario horario = new Horario(
                LocalDate.of(2030, 1, 15),
                LocalTime.of(9, 0),
                "Dra. Valeria"
        );

        DatosCita datos = new DatosCita(
                1L,
                "Sofía Pérez",
                "sofia@example.com",
                "Luna",
                "Perro",
                "Vacunación",
                "Control anual"
        );

        cita = new Cita(
                datos,
                horario,
                LocalDateTime.of(2030, 1, 10, 12, 0)
        );
    }

    @Test
    void listarCitas() throws Exception {
        when(citaService.listar(null)).thenReturn(List.of(cita));

        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mascota").value("Luna"));
    }

    @Test
    void filtrarCitasPorFecha() throws Exception {
        LocalDate fecha = LocalDate.of(2030, 1, 15);
        when(citaService.listar(fecha)).thenReturn(List.of(cita));

        mockMvc.perform(get("/api/citas")
                .param("fecha", "2030-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fecha").value("2030-01-15"));

        verify(citaService).listar(fecha);
    }

    @Test
    void reservarCita() throws Exception {
        String citaJson = """
                {
                    "horarioId": 1,
                    "dueno": "Sofía Pérez",
                    "correo": "sofia@example.com",
                    "mascota": "Luna",
                    "especie": "Perro",
                    "servicio": "Vacunación",
                    "motivo": "Control anual"
                }
                """;

        when(citaService.reservar(any(DatosCita.class))).thenReturn(cita);

        mockMvc.perform(post("/api/citas")
                .contentType("application/json")
                .content(citaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mascota").value("Luna"))
                .andExpect(jsonPath("$.estado").value("RESERVADA"));
    }

    @Test
    void obtenerCitaPorId() throws Exception {
        when(citaService.buscar(1L)).thenReturn(cita);

        mockMvc.perform(get("/api/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dueno").value("Sofía Pérez"));
    }

    @Test
    void actualizarCita() throws Exception {
        String citaJson = """
                {
                    "horarioId": 1,
                    "dueno": "Sofía Pérez",
                    "correo": "sofia@example.com",
                    "mascota": "Luna",
                    "especie": "Perro",
                    "servicio": "Vacunación",
                    "motivo": "Control anual"
                }
                """;

        when(citaService.actualizar(eq(1L), any(DatosCita.class)))
                .thenReturn(cita);

        mockMvc.perform(put("/api/citas/1")
                .contentType("application/json")
                .content(citaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Control anual"));

        verify(citaService).actualizar(eq(1L), any(DatosCita.class));
    }

    @Test
    void marcarCitaAtendida() throws Exception {
        cita.atender();
        when(citaService.atender(1L)).thenReturn(cita);

        mockMvc.perform(put("/api/citas/1/atendida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATENDIDA"));
    }

    @Test
    void cancelarCita() throws Exception {
        cita.cancelar();
        when(citaService.cancelar(1L)).thenReturn(cita);

        mockMvc.perform(delete("/api/citas/1"))
                .andExpect(status().isNoContent());

        verify(citaService).cancelar(1L);
    }

    @Test
    void rechazarCitaSinMascota() throws Exception {
        String citaJson = """
                {
                    "horarioId": 1,
                    "dueno": "Sofía Pérez",
                    "correo": "sofia@example.com",
                    "mascota": "",
                    "especie": "Perro",
                    "servicio": "Vacunación",
                    "motivo": "Control anual"
                }
                """;

        mockMvc.perform(post("/api/citas")
                .contentType("application/json")
                .content(citaJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(citaService);
    }
}
