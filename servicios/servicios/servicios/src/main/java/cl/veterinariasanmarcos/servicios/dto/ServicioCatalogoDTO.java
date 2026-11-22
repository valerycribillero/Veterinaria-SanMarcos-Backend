package cl.veterinariasanmarcos.servicios.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ServicioCatalogoDTO {

    @NotBlank(message = "El código del servicio es obligatorio (ej. SV001)")
    private String codigo;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String nombreServicio;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    @NotBlank(message = "La duración aproximada es obligatoria")
    private String duracionAprox;

    @NotNull(message = "El precio en CLP es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Integer precioClp;

    private String observaciones;
    private Boolean activo;
}