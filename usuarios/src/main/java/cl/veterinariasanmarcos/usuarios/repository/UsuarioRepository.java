package cl.veterinariasanmarcos.usuarios.repository;

import cl.veterinariasanmarcos.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
