package cl.veterinariasanmarcos.compras.repository;

import cl.veterinariasanmarcos.compras.model.ProductoServicio;
import cl.veterinariasanmarcos.compras.model.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoServicioRepository extends JpaRepository<ProductoServicio, String> {
    List<ProductoServicio> findByActivoTrue();
    List<ProductoServicio> findByTipoItemAndActivoTrue(TipoItem tipoItem);
    List<ProductoServicio> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);
}
