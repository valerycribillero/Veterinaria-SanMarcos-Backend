package cl.veterinariasanmarcos.ficha.repository;

import cl.veterinariasanmarcos.ficha.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);
    List<Consulta> findByFechaConsultaBetweenOrderByFechaConsultaDesc(LocalDateTime inicio, LocalDateTime fin);
}