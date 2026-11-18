package cl.veterinariasanmarcos.agenda.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.veterinariasanmarcos.agenda.model.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    @Query("""
            select c from Cita c
            where (:fecha is null or c.fecha = :fecha)
            order by c.fecha, c.hora, c.id
            """)
    List<Cita> listar(@Param("fecha") LocalDate fecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cita c where c.id = :id")
    Optional<Cita> bloquear(@Param("id") Long id);
}
