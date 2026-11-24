package cl.veterinariasanmarcos.agenda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.agenda.dto.DatosCita;
import cl.veterinariasanmarcos.agenda.model.Cita;
import cl.veterinariasanmarcos.agenda.model.Horario;
import cl.veterinariasanmarcos.agenda.repository.CitaRepository;

class CitaServiceTest {
    private final CitaRepository repository = mock(CitaRepository.class);
    private final CitaService service = new CitaService(repository, mock(HorarioService.class));
    private final LocalDate hoy = LocalDate.now(ZoneId.of("America/Santiago"));

    @Test
    void recibeLlamaYAtiendeLaCitaDelDia() {
        prepararCita(hoy);
        assertEquals(HttpStatus.CONFLICT, assertThrows(
            ResponseStatusException.class, () -> service.llamar(1L)
        ).getStatusCode());
        assertEquals(Cita.Estado.EN_ESPERA, service.confirmarLlegada(1L).getEstado());
        assertEquals(Cita.Estado.EN_ESPERA, service.confirmarLlegada(1L).getEstado());
        assertEquals(Cita.Estado.EN_ATENCION, service.llamar(1L).getEstado());
        assertEquals(Cita.Estado.EN_ATENCION, service.llamar(1L).getEstado());
        assertEquals(Cita.Estado.ATENDIDA, service.atender(1L).getEstado());
    }

    @Test
    void rechazaConfirmarLaLlegadaAntesDelDiaDeAtencion() {
        Cita cita = prepararCita(hoy.plusDays(1));
        assertEquals(HttpStatus.CONFLICT, assertThrows(
            ResponseStatusException.class, () -> service.confirmarLlegada(1L)
        ).getStatusCode());
        assertEquals(Cita.Estado.RESERVADA, cita.getEstado());
    }

    private Cita prepararCita(LocalDate fecha) {
        var horario = new Horario(fecha, LocalTime.of(10, 0), "Dr. Fabián");
        var datos = new DatosCita(1L, "Ana", "ana@example.com", "Luna", "GATO", "Control", "Seguimiento");
        var cita = new Cita(datos, horario, hoy.atStartOfDay());
        when(repository.bloquear(1L)).thenReturn(Optional.of(cita));
        return cita;
    }
}
