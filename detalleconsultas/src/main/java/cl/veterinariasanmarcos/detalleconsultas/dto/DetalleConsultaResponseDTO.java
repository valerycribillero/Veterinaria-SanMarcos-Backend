package cl.veterinariasanmarcos.detalleconsultas.dto;

import cl.veterinariasanmarcos.detalleconsultas.model.TipoDetalleClinico;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleConsultaResponseDTO {
    private Long id;
    private Long consultaId;
    private Long pacienteId;
    private String nombrePaciente;
    private Long duenoId;
    private TipoDetalleClinico tipoDetalle;
    private String codigoCatalogo;
    private String descripcionItem;
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;
    private String viaAdministracion;
    private Integer cantidad;
    private LocalDate fechaProximaDosis;
    private String observacionesMedicas;
    private Long profesionalId;
    private String nombreProfesional;
    private LocalDateTime fechaRegistro;
}