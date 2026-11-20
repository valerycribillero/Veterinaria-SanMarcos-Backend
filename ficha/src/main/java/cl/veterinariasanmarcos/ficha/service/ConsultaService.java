package cl.veterinariasanmarcos.ficha.service;

import cl.veterinariasanmarcos.ficha.dto.*;
import cl.veterinariasanmarcos.ficha.model.*;
import cl.veterinariasanmarcos.ficha.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final VacunaRegistroRepository vacunaRepository;

    @Transactional
    public ConsultaResponseDTO registrarConsulta(ConsultaRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con ID: " + dto.getPacienteId()));

        Consulta consulta = Consulta.builder()
                .paciente(paciente)
                .veterinarioId(dto.getVeterinarioId())
                .nombreVeterinario(dto.getNombreVeterinario())
                .tipoServicio(dto.getTipoServicio())
                .pesoKg(dto.getPesoKg())
                .motivoConsulta(dto.getMotivoConsulta())
                .diagnostico(dto.getDiagnostico())
                .medicamentosRecetados(dto.getMedicamentosRecetados())
                .indicaciones(dto.getIndicaciones())
                .proximoControl(dto.getProximoControl())
                .build();

        if (dto.getVacunasAplicadas() != null) {
            List<VacunaRegistro> vacunas = dto.getVacunasAplicadas().stream()
                    .map(v -> VacunaRegistro.builder()
                            .consulta(consulta)
                            .nombreVacuna(v.getNombreVacuna())
                            .lote(v.getLote())
                            .fechaAplicacion(v.getFechaAplicacion())
                            .fechaVencimiento(v.getFechaVencimiento())
                            .build())
                    .collect(Collectors.toList());
            consulta.setVacunasAplicadas(vacunas);
        }

        return toResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarPorPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteIdOrderByFechaConsultaDesc(pacienteId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacunaRegistroDTO> obtenerHistorialVacunas(Long pacienteId) {
        return vacunaRepository.findByConsultaPacienteIdOrderByFechaVencimientoAsc(pacienteId).stream()
                .map(this::toVacunaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacunaRegistroDTO> obtenerVacunasPorVencer(int diasAnticipacion) {
        LocalDate hoy = LocalDate.now();
        return vacunaRepository.findByFechaVencimientoBetween(hoy, hoy.plusDays(diasAnticipacion)).stream()
                .map(this::toVacunaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> reporteAtencionesPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return consultaRepository.findByFechaConsultaBetweenOrderByFechaConsultaDesc(inicio, fin).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ConsultaResponseDTO toResponseDTO(Consulta c) {
        List<VacunaRegistroDTO> vacunas = c.getVacunasAplicadas() != null
                ? c.getVacunasAplicadas().stream().map(this::toVacunaDTO).collect(Collectors.toList())
                : new ArrayList<>();

        return ConsultaResponseDTO.builder()
                .id(c.getId())
                .pacienteId(c.getPaciente().getId())
                .nombrePaciente(c.getPaciente().getNombre())
                .veterinarioId(c.getVeterinarioId())
                .nombreVeterinario(c.getNombreVeterinario())
                .fechaConsulta(c.getFechaConsulta())
                .tipoServicio(c.getTipoServicio())
                .pesoKg(c.getPesoKg())
                .motivoConsulta(c.getMotivoConsulta())
                .diagnostico(c.getDiagnostico())
                .medicamentosRecetados(c.getMedicamentosRecetados())
                .indicaciones(c.getIndicaciones())
                .proximoControl(c.getProximoControl())
                .vacunasAplicadas(vacunas)
                .build();
    }

    private VacunaRegistroDTO toVacunaDTO(VacunaRegistro v) {
        return VacunaRegistroDTO.builder()
                .id(v.getId())
                .nombreVacuna(v.getNombreVacuna())
                .lote(v.getLote())
                .fechaAplicacion(v.getFechaAplicacion())
                .fechaVencimiento(v.getFechaVencimiento())
                .build();
    }
}