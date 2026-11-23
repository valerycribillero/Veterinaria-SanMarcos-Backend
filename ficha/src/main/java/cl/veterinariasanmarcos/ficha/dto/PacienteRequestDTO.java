package cl.veterinariasanmarcos.ficha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PacienteRequestDTO {
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;

    @NotNull(message = "El ID del dueño es obligatorio")
    @Positive(message = "El ID del dueño debe ser positivo")
    private Long duenoId;

    @NotBlank(message = "El nombre del dueño es obligatorio")
    private String nombreDueno;

    private String telefonoDueno;
}