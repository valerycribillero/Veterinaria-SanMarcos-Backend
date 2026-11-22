package cl.veterinariasanmarcos.catalogo.catalogo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.veterinariasanmarcos.catalogo.catalogo.model.Publicacion;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findAllByOrderByIdDesc();
}
