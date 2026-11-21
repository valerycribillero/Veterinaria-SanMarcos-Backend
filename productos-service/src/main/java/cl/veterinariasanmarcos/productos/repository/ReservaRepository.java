package cl.veterinariasanmarcos.productos.repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import cl.veterinariasanmarcos.productos.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.id = :id")
    Optional<Reserva> bloquear(@Param("id") String id);
}
