package cl.veterinariasanmarcos.detalleconsultas.repository;

import cl.veterinariasanmarcos.detalleconsultas.model.DetalleConsulta;
import cl.veterinariasanmarcos.detalleconsultas.model.TipoDetalleClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleConsultaRepository extends JpaRepository<DetalleConsulta, Long> {

    List<DetalleConsulta> findByConsultaIdOrderByIdAsc(Long consultaId);

    List<DetalleConsulta> findByPacienteIdOrderByFechaRegistroDesc(Long pacienteId);

    List<DetalleConsulta> findByPacienteIdAndTipoDetalleOrderByFechaRegistroDesc(
            Long pacienteId, TipoDetalleClinico tipoDetalle);

    // Privacidad: consultar solo detalles de las mascotas de un dueño
    List<DetalleConsulta> findByDuenoIdOrderByFechaRegistroDesc(Long duenoId);

    // Alertas de próximas dosis / refuerzos de vacunas
    List<DetalleConsulta> findByFechaProximaDosisBetween(LocalDate hoy, LocalDate fechaLimite);
}