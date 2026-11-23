package cl.veterinariasanmarcos.ficha.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import cl.veterinariasanmarcos.ficha.dto.ConsultaResponseDTO;
import cl.veterinariasanmarcos.ficha.service.ConsultaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConsultaController.class)
public class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaService consultaService;

    @Test
    void listarConsultasDelVeterinario() throws Exception {
        ConsultaResponseDTO consulta = new ConsultaResponseDTO();
        consulta.setId(1L);
        consulta.setNombrePaciente("Paciente de ejemplo");
        consulta.setVeterinarioId(5L);
        when(consultaService.listarConsultas(5L)).thenReturn(List.of(consulta));

        mockMvc.perform(get("/api/consultas").param("veterinarioId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].veterinarioId").value(5));
    }

    @Test
    void obtenerDetalleDeConsulta() throws Exception {
        ConsultaResponseDTO consulta = new ConsultaResponseDTO();
        consulta.setId(1L);
        consulta.setDiagnostico("Control registrado");
        when(consultaService.obtenerPorId(1L)).thenReturn(consulta);

        mockMvc.perform(get("/api/consultas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnostico").value("Control registrado"));
    }
}
