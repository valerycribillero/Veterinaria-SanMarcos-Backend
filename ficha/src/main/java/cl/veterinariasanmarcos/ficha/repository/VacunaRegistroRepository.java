package cl.veterinariasanmarcos.ficha.repository;

import cl.veterinariasanmarcos.ficha.model.VacunaRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacunaRegistroRepository extends JpaRepository<VacunaRegistro, Long> {
    List<VacunaRegistro> findByConsultaPacienteIdOrderByFechaVencimientoAsc(Long pacienteId);
    List<VacunaRegistro> findByFechaVencimientoBetween(LocalDate hoy, LocalDate limiteAviso);
}