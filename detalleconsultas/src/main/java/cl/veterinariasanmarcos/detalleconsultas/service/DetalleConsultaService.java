package cl.veterinariasanmarcos.detalleconsultas.service;

import cl.veterinariasanmarcos.detalleconsultas.dto.*;
import cl.veterinariasanmarcos.detalleconsultas.model.DetalleConsulta;
import cl.veterinariasanmarcos.detalleconsultas.model.TipoDetalleClinico;
import cl.veterinariasanmarcos.detalleconsultas.repository.DetalleConsultaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleConsultaService {

    private final DetalleConsultaRepository repository;

    @Transactional
    public DetalleConsultaResponseDTO registrarDetalle(DetalleConsultaRequestDTO dto) {
        DetalleConsulta detalle = DetalleConsulta.builder()
                .consultaId(dto.getConsultaId())
                .pacienteId(dto.getPacienteId())
                .nombrePaciente(dto.getNombrePaciente())
                .duenoId(dto.getDuenoId())
                .tipoDetalle(dto.getTipoDetalle())
                .codigoCatalogo(dto.getCodigoCatalogo() != null ? dto.getCodigoCatalogo().toUpperCase() : null)
                .descripcionItem(dto.getDescripcionItem())
                .dosis(dto.getDosis())
                .frecuencia(dto.getFrecuencia())
                .duracionDias(dto.getDuracionDias())
                .viaAdministracion(dto.getViaAdministracion())
                .cantidad(dto.getCantidad() != null ? dto.getCantidad() : 1)
                .fechaProximaDosis(dto.getFechaProximaDosis())
                .observacionesMedicas(dto.getObservacionesMedicas())
                .profesionalId(dto.getProfesionalId())
                .nombreProfesional(dto.getNombreProfesional())
                .build();

        return toResponseDTO(repository.save(detalle));
    }

    @Transactional
    public List<DetalleConsultaResponseDTO> registrarLote(List<DetalleConsultaRequestDTO> listaDto) {
        return listaDto.stream()
                .map(this::registrarDetalle)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetalleConsultaResponseDTO obtenerPorId(Long id) {
        DetalleConsulta detalle = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de consulta no encontrado con ID: " + id));
        return toResponseDTO(detalle);
    }

    @Transactional(readOnly = true)
    public ResumenConsultaClinicaDTO obtenerResumenConsulta(Long consultaId) {
        List<DetalleConsultaResponseDTO> todos = repository.findByConsultaIdOrderByIdAsc(consultaId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        if (todos.isEmpty()) {
            throw new EntityNotFoundException("No existen detalles clínicos registrados para la consulta ID: " + consultaId);
        }

        DetalleConsultaResponseDTO primero = todos.get(0);

        return ResumenConsultaClinicaDTO.builder()
                .consultaId(consultaId)
                .pacienteId(primero.getPacienteId())
                .nombrePaciente(primero.getNombrePaciente())
                .medicamentosRecetados(filtrarPorTipo(todos, TipoDetalleClinico.MEDICAMENTO_RECETADO))
                .vacunasAplicadas(filtrarPorTipo(todos, TipoDetalleClinico.VACUNA_APLICADA))
                .procedimientosYExamenes(filtrarPorTipo(todos, TipoDetalleClinico.PROCEDIMIENTO_EXAMEN))
                .insumosAplicados(filtrarPorTipo(todos, TipoDetalleClinico.INSUMO_APLICADO))
                .build();
    }

    @Transactional(readOnly = true)
    public List<DetalleConsultaResponseDTO> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteIdOrderByFechaRegistroDesc(pacienteId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleConsultaResponseDTO> listarRecetasPorPaciente(Long pacienteId) {
        return repository.findByPacienteIdAndTipoDetalleOrderByFechaRegistroDesc(
                        pacienteId, TipoDetalleClinico.MEDICAMENTO_RECETADO).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleConsultaResponseDTO> listarPorDueno(Long duenoId) {
        return repository.findByDuenoIdOrderByFechaRegistroDesc(duenoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleConsultaResponseDTO> obtenerProximasDosis(int diasAnticipacion) {
        LocalDate hoy = LocalDate.now();
        return repository.findByFechaProximaDosisBetween(hoy, hoy.plusDays(diasAnticipacion)).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetalleConsultaResponseDTO actualizarDetalle(Long id, DetalleConsultaRequestDTO dto) {
        DetalleConsulta detalle = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de consulta no encontrado con ID: " + id));

        detalle.setTipoDetalle(dto.getTipoDetalle());
        detalle.setCodigoCatalogo(dto.getCodigoCatalogo() != null ? dto.getCodigoCatalogo().toUpperCase() : null);
        detalle.setDescripcionItem(dto.getDescripcionItem());
        detalle.setDosis(dto.getDosis());
        detalle.setFrecuencia(dto.getFrecuencia());
        detalle.setDuracionDias(dto.getDuracionDias());
        detalle.setViaAdministracion(dto.getViaAdministracion());
        detalle.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 1);
        detalle.setFechaProximaDosis(dto.getFechaProximaDosis());
        detalle.setObservacionesMedicas(dto.getObservacionesMedicas());

        return toResponseDTO(repository.save(detalle));
    }

    @Transactional
    public void eliminarDetalle(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Detalle de consulta no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private List<DetalleConsultaResponseDTO> filtrarPorTipo(
            List<DetalleConsultaResponseDTO> lista, TipoDetalleClinico tipo) {
        return lista.stream()
                .filter(d -> d.getTipoDetalle() == tipo)
                .collect(Collectors.toList());
    }

    private DetalleConsultaResponseDTO toResponseDTO(DetalleConsulta d) {
        return DetalleConsultaResponseDTO.builder()
                .id(d.getId())
                .consultaId(d.getConsultaId())
                .pacienteId(d.getPacienteId())
                .nombrePaciente(d.getNombrePaciente())
                .duenoId(d.getDuenoId())
                .tipoDetalle(d.getTipoDetalle())
                .codigoCatalogo(d.getCodigoCatalogo())
                .descripcionItem(d.getDescripcionItem())
                .dosis(d.getDosis())
                .frecuencia(d.getFrecuencia())
                .duracionDias(d.getDuracionDias())
                .viaAdministracion(d.getViaAdministracion())
                .cantidad(d.getCantidad())
                .fechaProximaDosis(d.getFechaProximaDosis())
                .observacionesMedicas(d.getObservacionesMedicas())
                .profesionalId(d.getProfesionalId())
                .nombreProfesional(d.getNombreProfesional())
                .fechaRegistro(d.getFechaRegistro())
                .build();
    }
}