package cl.veterinariasanmarcos.ficha.service;

import cl.veterinariasanmarcos.ficha.dto.*;
import cl.veterinariasanmarcos.ficha.model.Paciente;
import cl.veterinariasanmarcos.ficha.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ConsultaService consultaService;

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findByActivoTrue().stream()
                .map(p -> toResponseDTO(p, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listarPorDueno(Long duenoId) {
        return pacienteRepository.findByDuenoIdAndActivoTrue(duenoId).stream()
                .map(p -> toResponseDTO(p, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO obtenerPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .filter(Paciente::getActivo)
                .orElseThrow(() -> new EntityNotFoundException("Ficha de paciente no encontrada con ID: " + id));
        return toResponseDTO(paciente, true);
    }

    @Transactional
    public PacienteResponseDTO crear(PacienteRequestDTO dto) {
        Paciente paciente = Paciente.builder()
                .nombre(dto.getNombre())
                .especie(dto.getEspecie())
                .raza(dto.getRaza())
                .sexo(dto.getSexo())
                .fechaNacimiento(dto.getFechaNacimiento())
                .duenoId(dto.getDuenoId())
                .nombreDueno(dto.getNombreDueno())
                .telefonoDueno(dto.getTelefonoDueno())
                .activo(true)
                .build();
        return toResponseDTO(pacienteRepository.save(paciente), false);
    }

    @Transactional
    public PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha de paciente no encontrada con ID: " + id));

        paciente.setNombre(dto.getNombre());
        paciente.setEspecie(dto.getEspecie());
        paciente.setRaza(dto.getRaza());
        paciente.setSexo(dto.getSexo());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setDuenoId(dto.getDuenoId());
        paciente.setNombreDueno(dto.getNombreDueno());
        paciente.setTelefonoDueno(dto.getTelefonoDueno());

        return toResponseDTO(pacienteRepository.save(paciente), false);
    }

    @Transactional
    public void desactivar(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha de paciente no encontrada con ID: " + id));
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public long contarPacientesNuevos(LocalDateTime inicio, LocalDateTime fin) {
        return pacienteRepository.countByFechaCreacionBetween(inicio, fin);
    }

    private PacienteResponseDTO toResponseDTO(Paciente p, boolean incluirHistorial) {
        return PacienteResponseDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .especie(p.getEspecie())
                .raza(p.getRaza())
                .sexo(p.getSexo())
                .fechaNacimiento(p.getFechaNacimiento())
                .duenoId(p.getDuenoId())
                .nombreDueno(p.getNombreDueno())
                .telefonoDueno(p.getTelefonoDueno())
                .activo(p.getActivo())
                .fechaCreacion(p.getFechaCreacion())
                .historialConsultas(incluirHistorial
                        ? p.getConsultas().stream().map(consultaService::toResponseDTO).collect(Collectors.toList())
                        : null)
                .build();
    }
}