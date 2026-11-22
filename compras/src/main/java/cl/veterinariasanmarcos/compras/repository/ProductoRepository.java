package cl.veterinariasanmarcos.compras.repository;

import cl.veterinariasanmarcos.compras.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);

    List<Producto> findByEspecieContainingIgnoreCaseAndActivoTrue(String especie);

    List<Producto> findByStockActualLessThanEqualAndActivoTrue(Integer stockMinimo);
}
