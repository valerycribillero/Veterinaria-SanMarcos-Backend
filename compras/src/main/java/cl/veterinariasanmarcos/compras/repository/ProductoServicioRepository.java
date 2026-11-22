package cl.veterinariasanmarcos.compras.repository;

import cl.veterinariasanmarcos.compras.model.ProductoServicio;
import cl.veterinariasanmarcos.compras.model.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoServicioRepository extends JpaRepository<ProductoServicio, String> {
    List<ProductoServicio> findByActivoTrue();

    List<ProductoServicio> findByTipoItemAndActivoTrue(TipoItem tipoItem);

    List<ProductoServicio> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);
}
