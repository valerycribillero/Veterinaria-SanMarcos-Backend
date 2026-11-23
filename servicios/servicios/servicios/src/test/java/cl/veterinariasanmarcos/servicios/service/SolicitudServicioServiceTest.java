package cl.veterinariasanmarcos.servicios.service;

import cl.veterinariasanmarcos.servicios.repository.ServicioCatalogoRepository;
import cl.veterinariasanmarcos.servicios.repository.SolicitudServicioRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SolicitudServicioServiceTest {

    @Test
    void agendaPorRangoRechazaInicioPosteriorAlFin() {
        SolicitudServicioRepository solicitudRepository = mock(SolicitudServicioRepository.class);
        ServicioCatalogoRepository catalogoRepository = mock(ServicioCatalogoRepository.class);
        SolicitudServicioService service = new SolicitudServicioService(solicitudRepository, catalogoRepository);

        assertThrows(IllegalArgumentException.class, () -> service.agendaPorRango(
                LocalDateTime.of(2026, 11, 24, 0, 0),
                LocalDateTime.of(2026, 11, 23, 0, 0)
        ));
        verifyNoInteractions(solicitudRepository, catalogoRepository);
    }
}
