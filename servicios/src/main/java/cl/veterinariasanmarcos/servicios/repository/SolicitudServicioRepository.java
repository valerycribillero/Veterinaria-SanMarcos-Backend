package cl.veterinariasanmarcos.servicios.repository;

import cl.veterinariasanmarcos.servicios.model.EstadoSolicitud;
import cl.veterinariasanmarcos.servicios.model.SolicitudServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitudServicioRepository extends JpaRepository<SolicitudServicio, Long> {
    List<SolicitudServicio> findByDuenoIdOrderByFechaHoraSolicitadaDesc(Long duenoId);
    List<SolicitudServicio> findByEstadoOrderByFechaHoraSolicitadaAsc(EstadoSolicitud estado);
    List<SolicitudServicio> findByFechaHoraSolicitadaBetweenOrderByFechaHoraSolicitadaAsc(
            LocalDateTime inicio, LocalDateTime fin);
    boolean existsByVeterinarioAsignadoIdAndFechaHoraSolicitadaAndEstado(
            Long veterinarioId, LocalDateTime fechaHora, EstadoSolicitud estado);
}