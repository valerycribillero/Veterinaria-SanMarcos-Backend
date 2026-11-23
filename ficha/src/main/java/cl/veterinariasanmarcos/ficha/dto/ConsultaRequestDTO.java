package cl.veterinariasanmarcos.ficha.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaRequestDTO {
    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    private Long pacienteId;

    @NotNull(message = "El ID del veterinario es obligatorio")
    @Positive(message = "El ID del veterinario debe ser positivo")
    private Long veterinarioId;

    @NotBlank(message = "El nombre del veterinario es obligatorio")
    private String nombreVeterinario;

    @NotBlank(message = "El tipo de servicio es obligatorio")
    private String tipoServicio;

    @Positive(message = "El peso debe ser mayor que cero")
    private Double pesoKg;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    private String motivoConsulta;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    private String medicamentosRecetados;
    private String indicaciones;
    private LocalDate proximoControl;

    @Valid private List<VacunaRegistroDTO> vacunasAplicadas;
}
