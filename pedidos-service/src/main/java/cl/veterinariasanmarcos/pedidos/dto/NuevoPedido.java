package cl.veterinariasanmarcos.pedidos.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record NuevoPedido(
        @NotBlank
        @Size(max = 150)
        String nombreCompleto,

        @NotBlank
        @Email
        @Size(max = 150)
        String correo,

        @NotBlank
        @Size(max = 300)
        String direccion,

        @NotBlank
        @Pattern(regexp = "Debito|Credito|Transferencia")
        String metodo,

        @NotEmpty
        @Size(max = 100)
        List<@NotNull
        @Valid
        Linea> productos
) {
    public NuevoPedido {
        nombreCompleto = limpiar(nombreCompleto);
        correo = limpiar(correo);
        direccion = limpiar(direccion);
        metodo = limpiar(metodo);
    }

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

    private static String limpiar(String texto) {
        return texto == null ? null : texto.strip();
    }
}
