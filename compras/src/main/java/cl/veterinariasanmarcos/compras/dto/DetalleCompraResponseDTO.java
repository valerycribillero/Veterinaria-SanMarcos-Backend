package cl.veterinariasanmarcos.compras.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompraResponseDTO {
    private Long id;
    private String codigoProducto;
    private String nombreComercial;
    private String principioActivo;
    private String presentacion;
    private Integer cantidad;
    private Integer precioUnitarioClp;
    private Integer subtotalClp;
}
