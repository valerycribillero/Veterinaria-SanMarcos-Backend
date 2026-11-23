package cl.veterinariasanmarcos.detalleconsultas.integration;

public record FichaConsultaResponse(
        Long id,
        Long pacienteId,
        Long duenoId,
        String nombrePaciente
) {
}
