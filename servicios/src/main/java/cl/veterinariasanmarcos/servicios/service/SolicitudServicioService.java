package cl.veterinariasanmarcos.servicios.service;

import cl.veterinariasanmarcos.servicios.dto.*;
import cl.veterinariasanmarcos.servicios.integration.FichaClient;
import cl.veterinariasanmarcos.servicios.integration.FichaPacienteResponse;
import cl.veterinariasanmarcos.servicios.model.*;
import cl.veterinariasanmarcos.servicios.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudServicioService {

    private final SolicitudServicioRepository solicitudRepository;
    private final ServicioCatalogoRepository catalogoRepository;
    private final FichaClient fichaClient;

    @Transactional
    public SolicitudServicioResponseDTO crearSolicitud(SolicitudServicioRequestDTO dto) {
        ServicioCatalogo servicio = catalogoRepository.findById(dto.getCodigoServicio().toUpperCase())
                .filter(ServicioCatalogo::getActivo)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no disponible: " + dto.getCodigoServicio()));
        FichaPacienteResponse paciente = fichaClient.obtenerPaciente(dto.getPacienteId());

        if (!Boolean.TRUE.equals(paciente.activo())) {
            throw new EntityNotFoundException("Paciente no activo con ID: " + dto.getPacienteId());
        }
        if (!Objects.equals(paciente.duenoId(), dto.getDuenoId())) {
            throw new IllegalArgumentException("El paciente no pertenece al dueño indicado");
        }

        SolicitudServicio solicitud = SolicitudServicio.builder()
                .servicio(servicio)
                .duenoId(paciente.duenoId())
                .nombreDueno(paciente.nombreDueno())
                .telefonoDueno(paciente.telefonoDueno())
                .pacienteId(paciente.id())
                .nombrePaciente(paciente.nombre())
                .especiePaciente(paciente.especie())
                .motivo(dto.getMotivo())
                .fechaHoraSolicitada(dto.getFechaHoraSolicitada())
                .precioReferencialClp(servicio.getPrecioClp())
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        return toResponseDTO(solicitudRepository.save(solicitud));
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioResponseDTO> listarTodas() {
        return solicitudRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioResponseDTO> listarPorDueno(Long duenoId) {
        return solicitudRepository.findByDuenoIdOrderByFechaHoraSolicitadaDesc(duenoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioResponseDTO> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstadoOrderByFechaHoraSolicitadaAsc(estado).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SolicitudServicioResponseDTO obtenerPorId(Long id) {
        SolicitudServicio s = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id));
        return toResponseDTO(s);
    }

    @Transactional
    public SolicitudServicioResponseDTO gestionarPorRecepcion(Long id, GestionRecepcionDTO dto) {
        SolicitudServicio solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id));

        if (dto.getEstado() == EstadoSolicitud.REAGENDAMIENTO_PROPUESTO && dto.getFechaHoraPropuesta() == null) {
            throw new IllegalArgumentException("Debe indicar una nueva fecha y hora al proponer reagendamiento");
        }

        if (dto.getEstado() == EstadoSolicitud.CONFIRMADA && dto.getVeterinarioAsignadoId() != null) {
            boolean ocupado = solicitudRepository.existsByVeterinarioAsignadoIdAndFechaHoraSolicitadaAndEstado(
                    dto.getVeterinarioAsignadoId(),
                    solicitud.getFechaHoraSolicitada(),
                    EstadoSolicitud.CONFIRMADA
            );
            if (ocupado) {
                throw new IllegalStateException("El médico veterinario ya tiene otra atención confirmada en ese horario");
            }
        }

        solicitud.setEstado(dto.getEstado());
        solicitud.setFechaHoraPropuesta(dto.getFechaHoraPropuesta());
        solicitud.setVeterinarioAsignadoId(dto.getVeterinarioAsignadoId());
        solicitud.setNombreVeterinarioAsignado(dto.getNombreVeterinarioAsignado());
        solicitud.setObservacionRecepcion(dto.getObservacionRecepcion());

        return toResponseDTO(solicitudRepository.save(solicitud));
    }

    @Transactional
    public SolicitudServicioResponseDTO aceptarReagendamiento(Long id, Long duenoId) {
        SolicitudServicio solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id));

        if (!solicitud.getDuenoId().equals(duenoId)) {
            throw new IllegalStateException("No tiene permisos para modificar esta solicitud");
        }
        if (solicitud.getEstado() != EstadoSolicitud.REAGENDAMIENTO_PROPUESTO || solicitud.getFechaHoraPropuesta() == null) {
            throw new IllegalStateException("No existe una propuesta de reagendamiento pendiente");
        }

        solicitud.setFechaHoraSolicitada(solicitud.getFechaHoraPropuesta());
        solicitud.setFechaHoraPropuesta(null);
        solicitud.setEstado(EstadoSolicitud.CONFIRMADA);

        return toResponseDTO(solicitudRepository.save(solicitud));
    }

    @Transactional
    public void cancelarSolicitud(Long id) {
        SolicitudServicio solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id));
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitudRepository.save(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioResponseDTO> agendaPorRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return solicitudRepository.findByFechaHoraSolicitadaBetweenOrderByFechaHoraSolicitadaAsc(inicio, fin).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private SolicitudServicioResponseDTO toResponseDTO(SolicitudServicio s) {
        return SolicitudServicioResponseDTO.builder()
                .id(s.getId())
                .codigoServicio(s.getServicio().getCodigo())
                .categoriaServicio(s.getServicio().getCategoria())
                .nombreServicio(s.getServicio().getNombreServicio())
                .duracionAprox(s.getServicio().getDuracionAprox())
                .duenoId(s.getDuenoId())
                .nombreDueno(s.getNombreDueno())
                .telefonoDueno(s.getTelefonoDueno())
                .pacienteId(s.getPacienteId())
                .nombrePaciente(s.getNombrePaciente())
                .especiePaciente(s.getEspeciePaciente())
                .motivo(s.getMotivo())
                .fechaHoraSolicitada(s.getFechaHoraSolicitada())
                .fechaHoraPropuesta(s.getFechaHoraPropuesta())
                .veterinarioAsignadoId(s.getVeterinarioAsignadoId())
                .nombreVeterinarioAsignado(s.getNombreVeterinarioAsignado())
                .estado(s.getEstado())
                .observacionRecepcion(s.getObservacionRecepcion())
                .precioReferencialClp(s.getPrecioReferencialClp())
                .fechaCreacion(s.getFechaCreacion())
                .fechaActualizacion(s.getFechaActualizacion())
                .build();
    }
}