package cl.veterinariasanmarcos.ficha.repository;

import cl.veterinariasanmarcos.ficha.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByActivoTrue();
    List<Paciente> findByDuenoIdAndActivoTrue(Long duenoId);
    List<Paciente> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}