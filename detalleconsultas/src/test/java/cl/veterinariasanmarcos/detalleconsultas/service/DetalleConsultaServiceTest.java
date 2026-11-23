package cl.veterinariasanmarcos.detalleconsultas.service;

import cl.veterinariasanmarcos.detalleconsultas.dto.DetalleConsultaRequestDTO;
import cl.veterinariasanmarcos.detalleconsultas.integration.FichaClient;
import cl.veterinariasanmarcos.detalleconsultas.integration.FichaConsultaResponse;
import cl.veterinariasanmarcos.detalleconsultas.model.TipoDetalleClinico;
import cl.veterinariasanmarcos.detalleconsultas.repository.DetalleConsultaRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DetalleConsultaServiceTest {

    @Test
    void registrarDetalleUsaElNombreDePacienteDeLaConsulta() {
        DetalleConsultaRepository repository = mock(DetalleConsultaRepository.class);
        FichaClient fichaClient = mock(FichaClient.class);
        DetalleConsultaService service = new DetalleConsultaService(repository, fichaClient);

        when(fichaClient.obtenerConsulta(30L)).thenReturn(
                new FichaConsultaResponse(30L, 10L, 20L, "Luna"));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registrarDetalle(detalle());

        assertEquals("Luna", response.getNombrePaciente());
        verify(fichaClient).obtenerConsulta(30L);
    }

    @Test
    void registrarDetalleRechazaUnaConsultaQueNoCorrespondeAlPaciente() {
        DetalleConsultaRepository repository = mock(DetalleConsultaRepository.class);
        FichaClient fichaClient = mock(FichaClient.class);
        DetalleConsultaService service = new DetalleConsultaService(repository, fichaClient);

        when(fichaClient.obtenerConsulta(30L)).thenReturn(
                new FichaConsultaResponse(30L, 11L, 20L, "Luna"));

        assertThrows(IllegalArgumentException.class, () -> service.registrarDetalle(detalle()));
        verify(repository, never()).save(any());
    }

    private DetalleConsultaRequestDTO detalle() {
        return DetalleConsultaRequestDTO.builder()
                .consultaId(30L)
                .pacienteId(10L)
                .nombrePaciente("Nombre enviado")
                .duenoId(20L)
                .tipoDetalle(TipoDetalleClinico.MEDICAMENTO_RECETADO)
                .descripcionItem("Tratamiento")
                .profesionalId(40L)
                .nombreProfesional("Veterinario")
                .build();
    }
}
