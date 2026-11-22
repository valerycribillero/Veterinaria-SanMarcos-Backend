package cl.veterinariasanmarcos.servicios.dto;

import cl.veterinariasanmarcos.servicios.model.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GestionRecepcionDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoSolicitud estado; // CONFIRMADA, REAGENDAMIENTO_PROPUESTO, RECHAZADA, COMPLETADA

    private LocalDateTime fechaHoraPropuesta;
    private Long veterinarioAsignadoId;
    private String nombreVeterinarioAsignado;
    private String observacionRecepcion;
}