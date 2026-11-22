package cl.veterinariasanmarcos.servicios.dto;

import cl.veterinariasanmarcos.servicios.model.EstadoSolicitud;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudServicioResponseDTO {
    private Long id;
    private String codigoServicio;
    private String categoriaServicio;
    private String nombreServicio;
    private String duracionAprox;
    private Long duenoId;
    private String nombreDueno;
    private String telefonoDueno;
    private Long pacienteId;
    private String nombrePaciente;
    private String especiePaciente;
    private String motivo;
    private LocalDateTime fechaHoraSolicitada;
    private LocalDateTime fechaHoraPropuesta;
    private Long veterinarioAsignadoId;
    private String nombreVeterinarioAsignado;
    private EstadoSolicitud estado;
    private String observacionRecepcion;
    private Integer precioReferencialClp;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}