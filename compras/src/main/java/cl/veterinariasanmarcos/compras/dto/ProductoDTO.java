package cl.veterinariasanmarcos.compras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoDTO {

    @NotBlank(message = "El código del producto es obligatorio (ej. ME001)")
    private String codigo;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;

    @NotBlank(message = "El principio activo es obligatorio")
    private String principioActivo;

    @NotBlank(message = "La presentación es obligatoria")
    private String presentacion;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;

    @NotNull(message = "El precio unitario en CLP es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Integer precioUnitarioClp;

    private Boolean activo;
}