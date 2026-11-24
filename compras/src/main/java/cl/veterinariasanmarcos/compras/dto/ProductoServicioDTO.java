package cl.veterinariasanmarcos.compras.dto;

import cl.veterinariasanmarcos.compras.model.TipoItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoServicioDTO {

    @NotBlank(message = "El código del ítem es obligatorio")
    private String codigo;

    @NotNull(message = "El tipo de ítem es obligatorio")
    private TipoItem tipoItem;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String principioActivo;
    private String presentacion;
    private String duracionAprox;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;

    @NotNull(message = "El precio en CLP es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Integer precioClp;

    private String observaciones;
    private Boolean activo;
}
