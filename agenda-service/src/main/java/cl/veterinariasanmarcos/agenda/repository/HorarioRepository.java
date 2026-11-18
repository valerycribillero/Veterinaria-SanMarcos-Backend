package cl.veterinariasanmarcos.agenda.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.veterinariasanmarcos.agenda.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    boolean existsByFechaAndHoraAndVeterinario(LocalDate fecha, LocalTime hora, String veterinario);

    @Query("""
            select h from Horario h
            where h.activo = true and h.reservado = false
            and (h.fecha > :hoy or (h.fecha = :hoy and h.hora > :ahora))
            and (:fecha is null or h.fecha = :fecha)
            order by h.fecha, h.hora, h.veterinario
            """)
    List<Horario> listarDisponibles(@Param("hoy") LocalDate hoy, @Param("ahora") LocalTime ahora,
            @Param("fecha") LocalDate fecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Horario h where h.id = :id")
    Optional<Horario> bloquear(@Param("id") Long id);
}
