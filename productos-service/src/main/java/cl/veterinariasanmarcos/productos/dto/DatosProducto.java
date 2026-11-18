package cl.veterinariasanmarcos.productos.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record DatosProducto(
        @NotBlank @Pattern(regexp = "[A-Z0-9_-]{1,30}") String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 100) String categoria,
        @NotNull @DecimalMin("1") @Digits(integer = 10, fraction = 0) BigDecimal precio,
        @NotNull @Min(0) @Max(1000000) Integer stock,
        @NotBlank @Size(max = 500) String imagen,
        @NotNull @Size(max = 150) String principioActivo,
        @NotNull @Size(max = 100) String presentacion,
        @NotNull @Size(max = 100) String especie
) {
    public DatosProducto {
        codigo = limpiar(codigo);
        nombre = limpiar(nombre);
        categoria = limpiar(categoria);
        imagen = limpiar(imagen);
        principioActivo = limpiar(principioActivo);
        presentacion = limpiar(presentacion);
        especie = limpiar(especie);
    }

    private static String limpiar(String texto) {
        return texto == null ? null : texto.strip();
    }

}
