package cl.veterinariasanmarcos.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.veterinariasanmarcos.usuarios.dto.DatosIngreso;
import cl.veterinariasanmarcos.usuarios.model.Rol;
import cl.veterinariasanmarcos.usuarios.model.Usuario;
import cl.veterinariasanmarcos.usuarios.repository.UsuarioRepository;

class UsuarioServiceTest {
    @Test
    void iniciarSesionConservaElRolDeRecepcionista() {
        var repository = mock(UsuarioRepository.class);
        var encoder = mock(PasswordEncoder.class);
        var service = new UsuarioService(repository, encoder);
        var usuario = new Usuario();
        usuario.setRol(Rol.RECEPCIONISTA);
        usuario.setPassword("hash");
        var credenciales = new DatosIngreso();
        credenciales.setCorreo("recepcion@example.com");
        credenciales.setPassword("clave-de-prueba");
        when(repository.findByEmailIgnoreCase(credenciales.getCorreo())).thenReturn(Optional.of(usuario));
        when(encoder.matches(credenciales.getPassword(), usuario.getPassword())).thenReturn(true);

        assertEquals("recepcionista", service.iniciarSesion(credenciales).getRol());
    }
}
