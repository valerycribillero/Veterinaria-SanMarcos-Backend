package cl.veterinariasanmarcos.servicios.integration;

public record FichaPacienteResponse(
        Long id,
        String nombre,
        String especie,
        Long duenoId,
        String nombreDueno,
        String telefonoDueno,
        Boolean activo
) {
}
