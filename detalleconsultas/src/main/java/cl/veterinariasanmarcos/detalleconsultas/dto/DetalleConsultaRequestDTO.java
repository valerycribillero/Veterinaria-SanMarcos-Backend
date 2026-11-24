package cl.veterinariasanmarcos.detalleconsultas.dto;

import cl.veterinariasanmarcos.detalleconsultas.model.TipoDetalleClinico;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleConsultaRequestDTO {

    @NotNull(message = "El ID de la consulta es obligatorio")
    @Positive(message = "El ID de la consulta debe ser positivo")
    private Long consultaId;

    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    private Long pacienteId;

    @NotBlank(message = "El nombre del paciente es obligatorio")
    private String nombrePaciente;

    @NotNull(message = "El ID del dueño es obligatorio")
    @Positive(message = "El ID del dueño debe ser positivo")
    private Long duenoId;

    @NotNull(message = "El tipo de detalle clínico es obligatorio")
    private TipoDetalleClinico tipoDetalle;

    private String codigoCatalogo;

    @NotBlank(message = "La descripción del medicamento, vacuna o procedimiento es obligatoria")
    private String descripcionItem;

    private String dosis;
    private String frecuencia;

    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracionDias;

    private String viaAdministracion;

    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    private LocalDate fechaProximaDosis;
    private String observacionesMedicas;

    @NotNull(message = "El ID del profesional es obligatorio")
    @Positive(message = "El ID del profesional debe ser positivo")
    private Long profesionalId;

    @NotBlank(message = "El nombre del profesional es obligatorio")
    private String nombreProfesional;
}