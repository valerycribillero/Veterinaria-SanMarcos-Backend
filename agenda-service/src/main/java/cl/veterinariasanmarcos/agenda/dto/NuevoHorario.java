package cl.veterinariasanmarcos.agenda.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NuevoHorario(
    @NotNull
        LocalDate fecha,
    @NotNull
        LocalTime hora,
    @NotBlank
        @Size(max = 150)
        String veterinario
) {
    public NuevoHorario {
        if (veterinario != null) {
            veterinario = veterinario.strip();
        }
    }
}
