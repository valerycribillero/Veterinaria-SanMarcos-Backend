package cl.veterinariasanmarcos.compras.dto;

import cl.veterinariasanmarcos.compras.model.EstadoCompra;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompraResponseDTO {
    private Long id;
    private Long duenoId;
    private String nombreDueno;
    private Long pacienteId;
    private String nombrePaciente;
    private LocalDateTime fechaCompra;
    private EstadoCompra estado;
    private Integer totalClp;
    private String observacionCliente;
    private List<DetalleCompraResponseDTO> detalles;
}