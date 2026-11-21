package cl.veterinariasanmarcos.agenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DatosCita(
    @NotNull
        @Positive
        Long horarioId,
    @NotBlank
        @Size(max = 150)
        String dueno,
    @NotBlank
        @Email
        @Size(max = 150)
        String correo,
    @NotBlank
        @Size(max = 100)
        String mascota,
    @NotBlank
        @Size(max = 50)
        String especie,
    @Size(max = 150)
        String servicio,
    @NotBlank
        @Size(max = 2000)
        String motivo
) {
    public DatosCita {
        dueno = limpiar(dueno);
        correo = limpiar(correo);
        mascota = limpiar(mascota);
        especie = limpiar(especie);
        servicio = limpiar(servicio);
        motivo = limpiar(motivo);
        if (servicio == null || servicio.isEmpty()) {
            servicio = "Consulta general";
        }
    }

    private static String limpiar(String texto) {
        return texto == null ? null : texto.strip();
    }
}
