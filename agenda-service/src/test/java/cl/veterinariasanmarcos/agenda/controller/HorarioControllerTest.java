package cl.veterinariasanmarcos.agenda.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.veterinariasanmarcos.agenda.dto.NuevoHorario;
import cl.veterinariasanmarcos.agenda.model.Horario;
import cl.veterinariasanmarcos.agenda.service.HorarioService;

@WebMvcTest(HorarioController.class)
public class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HorarioService horarioService;

    @Test
    void listarHorarios() throws Exception {
        Horario horario = new Horario(
                LocalDate.of(2030, 1, 15),
                LocalTime.of(9, 0),
                "Dra. Valeria"
        );

        when(horarioService.listarDisponibles(null)).thenReturn(List.of(horario));

        mockMvc.perform(get("/api/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].veterinario").value("Dra. Valeria"));
    }

    @Test
    void crearHorario() throws Exception {
        String horarioJson = """
                {
                    "fecha": "2030-01-15",
                    "hora": "09:00",
                    "veterinario": "Dra. Valeria"
                }
                """;

        Horario horario = new Horario(
                LocalDate.of(2030, 1, 15),
                LocalTime.of(9, 0),
                "Dra. Valeria"
        );

        when(horarioService.crear(any(NuevoHorario.class))).thenReturn(horario);

        mockMvc.perform(post("/api/horarios")
                .contentType("application/json")
                .content(horarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.veterinario").value("Dra. Valeria"));
    }

    @Test
    void desactivarHorario() throws Exception {
        doNothing().when(horarioService).desactivar(1L);

        mockMvc.perform(delete("/api/horarios/1"))
                .andExpect(status().isNoContent());

        verify(horarioService).desactivar(1L);
    }

    @Test
    void activarHorario() throws Exception {
        Horario horario = new Horario(
                LocalDate.of(2030, 1, 15),
                LocalTime.of(9, 0),
                "Dra. Valeria"
        );

        when(horarioService.activar(1L)).thenReturn(horario);

        mockMvc.perform(put("/api/horarios/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void rechazarHorarioSinVeterinario() throws Exception {
        String horarioJson = """
                {
                    "fecha": "2030-01-15",
                    "hora": "09:00",
                    "veterinario": ""
                }
                """;

        mockMvc.perform(post("/api/horarios")
                .contentType("application/json")
                .content(horarioJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(horarioService);
    }
}
