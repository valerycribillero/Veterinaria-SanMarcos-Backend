package cl.veterinariasanmarcos.compras.repository;

import cl.veterinariasanmarcos.compras.model.Compra;
import cl.veterinariasanmarcos.compras.model.EstadoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByDuenoIdOrderByFechaCompraDesc(Long duenoId);

    List<Compra> findByEstadoOrderByFechaCompraDesc(EstadoCompra estado);

    List<Compra> findByFechaCompraBetweenOrderByFechaCompraDesc(
        LocalDateTime inicio,
        LocalDateTime fin
    );
}
