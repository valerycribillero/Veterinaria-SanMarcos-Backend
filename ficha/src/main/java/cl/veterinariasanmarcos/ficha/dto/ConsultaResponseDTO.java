package cl.veterinariasanmarcos.ficha.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsultaResponseDTO {
    private Long id;
    private Long pacienteId;
    private Long duenoId;
    private String nombrePaciente;
    private Long veterinarioId;
    private String nombreVeterinario;
    private LocalDateTime fechaConsulta;
    private String tipoServicio;
    private Double pesoKg;
    private String motivoConsulta;
    private String diagnostico;
    private String medicamentosRecetados;
    private String indicaciones;
    private LocalDate proximoControl;
    private List<VacunaRegistroDTO> vacunasAplicadas;
}