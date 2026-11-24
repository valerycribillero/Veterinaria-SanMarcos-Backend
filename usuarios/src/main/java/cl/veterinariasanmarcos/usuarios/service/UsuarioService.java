package cl.veterinariasanmarcos.usuarios.service;

import cl.veterinariasanmarcos.usuarios.dto.UsuarioListadoDTO;
import cl.veterinariasanmarcos.usuarios.dto.DatosIngreso;
import cl.veterinariasanmarcos.usuarios.dto.MiembroEquipo;
import cl.veterinariasanmarcos.usuarios.dto.SesionUsuario;
import cl.veterinariasanmarcos.usuarios.model.Rol;
import cl.veterinariasanmarcos.usuarios.model.Usuario;
import cl.veterinariasanmarcos.usuarios.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {
    private final UsuarioRepository repository; // para poder acceder a la base de datos

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardarUsuario(Usuario usuario) {
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        return repository.save(usuario);
    }

    public Usuario registrarCliente(Usuario usuario) {
        String correo = usuario.getEmail().trim().toLowerCase(Locale.ROOT);
        if (repository.findByEmailIgnoreCase(correo).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ese correo ya está registrado"
            );
        }
        usuario.setId(null);
        usuario.setEmail(correo);
        usuario.setRol(Rol.CLIENTE);
        return guardarUsuario(usuario);
    }

    public SesionUsuario iniciarSesion(DatosIngreso datos) {
        Usuario usuario = repository.findByEmailIgnoreCase(datos.getCorreo().trim())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos"
            ));

        if (!passwordEncoder.matches(datos.getPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos"
            );
        }

        String rol = usuario.getRol().name().toLowerCase(Locale.ROOT);

        return new SesionUsuario(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            rol
        );
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public void eliminarPorId(Integer id) {
        repository.deleteById(id);
    }

    public Usuario actualizarUsuario(Integer id, Usuario usuario) {
        Usuario existente = repository.findById(id).orElse(null);
        if (existente == null) return null;
        existente.setNombre(usuario.getNombre());
        existente.setEmail(usuario.getEmail());
        existente.setRol(usuario.getRol());
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return repository.save(existente);
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public List<UsuarioListadoDTO> listarDTO() {

        List<Usuario> usuarios = repository.findAll();
        List<UsuarioListadoDTO> lista = new ArrayList<>();

        for (Usuario u : usuarios) {
            UsuarioListadoDTO dto = new UsuarioListadoDTO();
            dto.setId(u.getId());
            dto.setNombre((u.getNombre()));
            dto.setEmail(u.getEmail());
            dto.setRol(u.getRol());

            lista.add(dto);
        }
        return lista;
    }

    public List<MiembroEquipo> obtenerEquipo() {
        return repository.findAll().stream()
            .filter(usuario -> usuario.getRol() == Rol.VETERINARIO)
            .map(usuario -> new MiembroEquipo(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEspecialidad(),
                usuario.getExperiencia(),
                usuario.getBio(),
                usuario.getImagen()
            ))
            .toList();
    }

}
