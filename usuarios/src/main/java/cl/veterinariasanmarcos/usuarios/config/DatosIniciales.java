package cl.veterinariasanmarcos.usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.veterinariasanmarcos.usuarios.model.Rol;
import cl.veterinariasanmarcos.usuarios.model.Usuario;
import cl.veterinariasanmarcos.usuarios.repository.UsuarioRepository;
import cl.veterinariasanmarcos.usuarios.service.UsuarioService;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final UsuarioService service;

    public DatosIniciales(
        UsuarioRepository repository,
        UsuarioService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        crearUsuario(
            "Sofía",
            "sofia@gmail.com",
            Rol.CLIENTE
        );

        crearUsuario(
            "Fabián Gómez",
            "fabian@vetsanmarcos.cl",
            Rol.VETERINARIO
        );

        crearUsuario(
            "Administrador",
            "admin@vetsanmarcos.cl",
            Rol.ADMINISTRADOR
        );
    }

    private void crearUsuario(
        String nombre,
        String email,
        Rol rol
    ) {
        // Si la cuenta existe, no la modifica ni la duplica.
        if (repository.findByEmailIgnoreCase(email).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword("1234");
        usuario.setRol(rol);

        // El servicio encripta la contraseña antes de guardarla.
        service.guardarUsuario(usuario);
    }
}
