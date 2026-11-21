package cl.veterinariasanmarcos.usuarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.veterinariasanmarcos.usuarios.dto.UsuarioListadoDTO;
import cl.veterinariasanmarcos.usuarios.model.Usuario;
import cl.veterinariasanmarcos.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {
    private final UsuarioRepository repository; // para poder acceder a la base de datos

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
        UsuarioRepository repository,
        PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardarUsuario(Usuario usuario) {
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        return repository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public void eliminarPorId(Integer id) {
        repository.deleteById(id);
    }

    public Usuario actualizarUsuario(
        Integer id,
        Usuario usuario
    ) {
        usuario.setId(id);
        return repository.save(usuario);
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public List<UsuarioListadoDTO> listarDTO() {

        List<Usuario> usuarios = repository.findAll();
        List<UsuarioListadoDTO> lista = new ArrayList<>();

        for (Usuario u : usuarios) {
            UsuarioListadoDTO dto = new UsuarioListadoDTO();
            dto.setNombre((u.getNombre()));
            dto.setEmail(u.getEmail());

            lista.add(dto);
        }
        return lista;
    }
}
