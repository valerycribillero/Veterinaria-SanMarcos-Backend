package cl.veterinariasanmarcos.servicios.service;

import cl.veterinariasanmarcos.servicios.dto.SolicitudServicioRequestDTO;
import cl.veterinariasanmarcos.servicios.integration.FichaClient;
import cl.veterinariasanmarcos.servicios.integration.FichaPacienteResponse;
import cl.veterinariasanmarcos.servicios.model.ServicioCatalogo;
import cl.veterinariasanmarcos.servicios.repository.ServicioCatalogoRepository;
import cl.veterinariasanmarcos.servicios.repository.SolicitudServicioRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SolicitudServicioServiceTest {

    @Test
    void agendaPorRangoRechazaInicioPosteriorAlFin() {
        SolicitudServicioRepository solicitudRepository = mock(SolicitudServicioRepository.class);
        ServicioCatalogoRepository catalogoRepository = mock(ServicioCatalogoRepository.class);
        FichaClient fichaClient = mock(FichaClient.class);
        SolicitudServicioService service = new SolicitudServicioService(
                solicitudRepository, catalogoRepository, fichaClient);

        assertThrows(IllegalArgumentException.class, () -> service.agendaPorRango(
                LocalDateTime.of(2026, 11, 24, 0, 0),
                LocalDateTime.of(2026, 11, 23, 0, 0)
        ));
        verifyNoInteractions(solicitudRepository, catalogoRepository);
    }

    @Test
    void crearSolicitudUsaLosDatosOficialesDeLaFicha() {
        SolicitudServicioRepository solicitudRepository = mock(SolicitudServicioRepository.class);
        ServicioCatalogoRepository catalogoRepository = mock(ServicioCatalogoRepository.class);
        FichaClient fichaClient = mock(FichaClient.class);
        SolicitudServicioService service = new SolicitudServicioService(
                solicitudRepository, catalogoRepository, fichaClient);

        when(catalogoRepository.findById("SV001")).thenReturn(Optional.of(servicioActivo()));
        when(fichaClient.obtenerPaciente(10L)).thenReturn(
                new FichaPacienteResponse(10L, "Luna", "Gato", 20L, "Ana", "123", true));
        when(solicitudRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.crearSolicitud(solicitud());

        assertEquals(20L, response.getDuenoId());
        assertEquals("Ana", response.getNombreDueno());
        assertEquals("Luna", response.getNombrePaciente());
        assertEquals("Gato", response.getEspeciePaciente());
        verify(fichaClient).obtenerPaciente(10L);
    }

    @Test
    void crearSolicitudRechazaPacienteDeOtroDueno() {
        SolicitudServicioRepository solicitudRepository = mock(SolicitudServicioRepository.class);
        ServicioCatalogoRepository catalogoRepository = mock(ServicioCatalogoRepository.class);
        FichaClient fichaClient = mock(FichaClient.class);
        SolicitudServicioService service = new SolicitudServicioService(
                solicitudRepository, catalogoRepository, fichaClient);

        when(catalogoRepository.findById("SV001")).thenReturn(Optional.of(servicioActivo()));
        when(fichaClient.obtenerPaciente(10L)).thenReturn(
                new FichaPacienteResponse(10L, "Luna", "Gato", 99L, "Otra persona", "456", true));

        assertThrows(IllegalArgumentException.class, () -> service.crearSolicitud(solicitud()));
        verify(solicitudRepository, never()).save(any());
    }

    private ServicioCatalogo servicioActivo() {
        return ServicioCatalogo.builder()
                .codigo("SV001")
                .categoria("Consultas")
                .nombreServicio("Consulta general")
                .especie("Perro / Gato")
                .duracionAprox("30 min")
                .precioClp(15000)
                .activo(true)
                .build();
    }

    private SolicitudServicioRequestDTO solicitud() {
        return SolicitudServicioRequestDTO.builder()
                .codigoServicio("SV001")
                .duenoId(20L)
                .nombreDueno("Nombre enviado")
                .telefonoDueno("000")
                .pacienteId(10L)
                .nombrePaciente("Nombre enviado")
                .especiePaciente("Especie enviada")
                .motivo("Control")
                .fechaHoraSolicitada(LocalDateTime.now().plusDays(1))
                .build();
    }
}
