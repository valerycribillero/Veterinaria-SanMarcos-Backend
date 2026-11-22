package cl.veterinariasanmarcos.servicios.repository;

import cl.veterinariasanmarcos.servicios.model.ServicioCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioCatalogoRepository extends JpaRepository<ServicioCatalogo, String> {
    List<ServicioCatalogo> findByActivoTrue();
    List<ServicioCatalogo> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);
    List<ServicioCatalogo> findByEspecieContainingIgnoreCaseAndActivoTrue(String especie);
}