package cl.veterinariasanmarcos.productos.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import cl.veterinariasanmarcos.productos.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findByActivoTrueOrderByCodigoAsc();
    Optional<Producto> findByCodigoAndActivoTrue(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)

    @Query("select p from Producto p where p.codigo = :codigo")
    Optional<Producto> bloquear(@Param("codigo") String codigo);
}
