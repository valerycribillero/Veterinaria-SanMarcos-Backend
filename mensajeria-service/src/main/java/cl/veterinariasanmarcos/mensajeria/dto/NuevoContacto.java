package cl.veterinariasanmarcos.mensajeria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NuevoContacto(
    @NotBlank
        @Size(max = 150)
        String nombreCompleto,
    @NotBlank
        @Email
        @Size(max = 150)
        String correo,
    @Pattern(regexp = "\\+?[0-9]+")
        @Size(max = 25)
        String telefono,
    @Size(max = 100)
        String asunto,
    @NotBlank
        @Size(max = 5000)
        String mensaje
) {
    public NuevoContacto {
        nombreCompleto = limpiar(nombreCompleto);
        correo = limpiar(correo);
        telefono = limpiar(telefono);
        asunto = limpiar(asunto);
        mensaje = limpiar(mensaje);
        if ("".equals(telefono)) {
            telefono = null;
        }
        if (asunto == null || asunto.isEmpty()) {
            asunto = "Consulta General";
        }
    }

    private static String limpiar(String texto) {
        return texto == null ? null : texto.strip();
    }
}
