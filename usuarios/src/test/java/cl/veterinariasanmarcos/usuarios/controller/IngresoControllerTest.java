package cl.veterinariasanmarcos.usuarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.veterinariasanmarcos.usuarios.dto.DatosIngreso;
import cl.veterinariasanmarcos.usuarios.dto.SesionUsuario;
import cl.veterinariasanmarcos.usuarios.model.Rol;
import cl.veterinariasanmarcos.usuarios.model.Usuario;
import cl.veterinariasanmarcos.usuarios.security.Seguridad;
import cl.veterinariasanmarcos.usuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(UsuarioController.class)
@Import(Seguridad.class)
@EnableWebSecurity
public class IngresoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void iniciarSesion() throws Exception {
        when(usuarioService.iniciarSesion(any(DatosIngreso.class)))
            .thenReturn(new SesionUsuario(1, "Cliente", "cliente@example.com", "cliente"));

        mockMvc.perform(post("/registro/login")
                .header("Origin", "http://localhost:5173")
                .contentType("application/json")
                .content("""
                    {"correo": "cliente@example.com", "password": "ClaveDePrueba"}
                    """))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(jsonPath("$.correo").value("cliente@example.com"))
            .andExpect(jsonPath("$.rol").value("cliente"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void rechazarCredencialesIncorrectas() throws Exception {
        when(usuarioService.iniciarSesion(any(DatosIngreso.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/registro/login")
                .contentType("application/json")
                .content("""
                    {"correo": "cliente@example.com", "password": "Incorrecta"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazarCorreoInvalido() throws Exception {
        mockMvc.perform(post("/registro/login")
                .contentType("application/json")
                .content("""
                    {"correo": "sin-arroba", "password": "ClaveDePrueba"}
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registrarClienteSinMostrarContrasena() throws Exception {
        Usuario cliente = new Usuario();
        cliente.setId(1);
        cliente.setNombre("Cliente");
        cliente.setEmail("cliente@example.com");
        cliente.setPassword("cifrada");
        cliente.setRol(Rol.CLIENTE);
        when(usuarioService.registrarCliente(any(Usuario.class))).thenReturn(cliente);

        mockMvc.perform(post("/registro/cliente")
                .contentType("application/json")
                .content("""
                    {
                        "nombre": "Cliente",
                        "email": "cliente@example.com",
                        "password": "ClaveDePrueba",
                        "rol": "CLIENTE"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("cliente@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }
}
