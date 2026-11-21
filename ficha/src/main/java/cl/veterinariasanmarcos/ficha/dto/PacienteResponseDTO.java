package cl.veterinariasanmarcos.ficha.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PacienteResponseDTO {
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private Long duenoId;
    private String nombreDueno;
    private String telefonoDueno;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private List<ConsultaResponseDTO> historialConsultas;
}