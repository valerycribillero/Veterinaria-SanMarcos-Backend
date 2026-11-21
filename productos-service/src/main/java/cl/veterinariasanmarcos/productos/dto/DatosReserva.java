package cl.veterinariasanmarcos.productos.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record DatosReserva(
        @NotEmpty
        @Size(max = 100)
        List<@NotNull
        @Valid
        Linea> productos
) {
    public record Linea(
        @NotBlank
            @Pattern(regexp = "[A-Z0-9_-]{1,30}")
            String codigo,
        @NotNull
            @Min(1)
            @Max(1000000)
            Integer cantidad
    ) {
            }
}
