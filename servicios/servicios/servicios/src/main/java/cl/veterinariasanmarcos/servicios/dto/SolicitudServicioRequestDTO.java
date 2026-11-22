package cl.veterinariasanmarcos.servicios.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudServicioRequestDTO {

    @NotBlank(message = "El código del servicio es obligatorio")
    private String codigoServicio;

    @NotNull(message = "El ID del dueño es obligatorio")
    private Long duenoId;

    @NotBlank(message = "El nombre del dueño es obligatorio")
    private String nombreDueno;

    private String telefonoDueno;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotBlank(message = "El nombre del paciente es obligatorio")
    private String nombrePaciente;

    @NotBlank(message = "La especie del paciente es obligatoria")
    private String especiePaciente;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "La fecha y hora solicitada es obligatoria")
    @Future(message = "La fecha de la cita debe ser futura")
    private LocalDateTime fechaHoraSolicitada;
}