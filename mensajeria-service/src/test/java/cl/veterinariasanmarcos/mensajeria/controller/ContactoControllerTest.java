package cl.veterinariasanmarcos.mensajeria.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import cl.veterinariasanmarcos.mensajeria.dto.NuevoContacto;
import cl.veterinariasanmarcos.mensajeria.model.Contacto;
import cl.veterinariasanmarcos.mensajeria.service.ContactoService;

@WebMvcTest(ContactoController.class)
public class ContactoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactoService contactoService;

    private Contacto contacto;

    @BeforeEach
    void prepararContacto() {
        contacto = new Contacto(
                "Sofía Pérez",
                "sofia@example.com",
                "+56912345678",
                "Consulta General",
                "Quisiera consultar por los horarios"
        );
    }

    @Test
    void listarContactos() throws Exception {
        when(contactoService.listar()).thenReturn(List.of(contacto));

        mockMvc.perform(get("/api/contactos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompleto").value("Sofía Pérez"));
    }

    @Test
    void agregarContacto() throws Exception {
        String contactoJson = """
                {
                    "nombreCompleto": "Sofía Pérez",
                    "correo": "sofia@example.com",
                    "telefono": "+56912345678",
                    "asunto": "Consulta General",
                    "mensaje": "Quisiera consultar por los horarios"
                }
                """;

        when(contactoService.crear(any(NuevoContacto.class))).thenReturn(contacto);

        mockMvc.perform(post("/api/contactos")
                .contentType("application/json")
                .content(contactoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("sofia@example.com"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void marcarContactoAtendido() throws Exception {
        contacto.marcarAtendido();
        when(contactoService.marcarAtendido(1L)).thenReturn(contacto);

        mockMvc.perform(put("/api/contactos/1/atendido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATENDIDO"));
    }

    @Test
    void eliminarContacto() throws Exception {
        doNothing().when(contactoService).eliminar(1L);

        mockMvc.perform(delete("/api/contactos/1"))
                .andExpect(status().isNoContent());

        verify(contactoService).eliminar(1L);
    }

    @Test
    void rechazarCorreoInvalido() throws Exception {
        String contactoJson = """
                {
                    "nombreCompleto": "Sofía Pérez",
                    "correo": "correo-invalido",
                    "telefono": "+56912345678",
                    "asunto": "Consulta General",
                    "mensaje": "Quisiera consultar por los horarios"
                }
                """;

        mockMvc.perform(post("/api/contactos")
                .contentType("application/json")
                .content(contactoJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contactoService);
    }

    @Test
    void contactoInexistenteDevuelve404() throws Exception {
        when(contactoService.marcarAtendido(99L)).thenThrow(
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contacto no encontrado"
                )
        );

        mockMvc.perform(put("/api/contactos/99/atendido"))
                .andExpect(status().isNotFound());
    }
}
