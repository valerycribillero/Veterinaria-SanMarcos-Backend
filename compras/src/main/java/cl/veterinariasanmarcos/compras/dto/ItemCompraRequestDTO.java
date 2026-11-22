package cl.veterinariasanmarcos.compras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCompraRequestDTO {

    @NotBlank(message = "El código del producto es obligatorio")
    private String codigoProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(
        value = 1,
        message = "La cantidad mínima es 1"
    )
    private Integer cantidad;
}
