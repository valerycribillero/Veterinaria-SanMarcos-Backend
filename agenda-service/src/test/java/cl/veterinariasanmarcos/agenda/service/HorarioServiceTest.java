package cl.veterinariasanmarcos.agenda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.agenda.dto.NuevoHorario;
import cl.veterinariasanmarcos.agenda.repository.HorarioRepository;

class HorarioServiceTest {
    private final HorarioRepository repository = mock(HorarioRepository.class);
    private final HorarioService service = new HorarioService(repository);

    @ParameterizedTest
    @CsvSource({"MONDAY, 08:30", "FRIDAY, 21:00", "SATURDAY, 09:00", "SATURDAY, 19:30"})
    void permiteCrearHorariosEnLosLimitesDeAtencion(DayOfWeek dia, LocalTime hora) {
        when(repository.saveAndFlush(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        assertEquals(hora, service.crear(nuevoHorario(dia, hora)).getHora());
    }

    @ParameterizedTest
    @CsvSource({"MONDAY, 08:29", "FRIDAY, 21:01", "SATURDAY, 08:59", "SATURDAY, 19:31", "SUNDAY, 12:00"})
    void rechazaCrearHorariosFueraDeAtencion(DayOfWeek dia, LocalTime hora) {
        var error = assertThrows(
            ResponseStatusException.class,
            () -> service.crear(nuevoHorario(dia, hora))
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    private NuevoHorario nuevoHorario(DayOfWeek dia, LocalTime hora) {
        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago"))
                .with(TemporalAdjusters.next(dia));
        return new NuevoHorario(fecha, hora, "Dra. Valeria");
    }
}
