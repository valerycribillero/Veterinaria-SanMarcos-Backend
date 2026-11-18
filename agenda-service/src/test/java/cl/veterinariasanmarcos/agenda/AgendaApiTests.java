package cl.veterinariasanmarcos.agenda;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgendaApiTests {
    @LocalServerPort
    private int puerto;

    @Autowired
    private JdbcTemplate jdbc;

    private final HttpClient cliente = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();
    private final LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(2);
    private long horarioUno;
    private long horarioDos;

    @BeforeEach
    void preparar() throws Exception {
        jdbc.update("delete from cita");
        jdbc.update("delete from horario");
        horarioUno = crearHorario("09:00", "Dra. Veterinaria");
        horarioDos = crearHorario("10:00", "Dra. Veterinaria");
    }

    private HttpResponse<String> llamar(String metodo, String ruta, String cuerpo) throws Exception {
        HttpRequest peticion = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(cuerpo))
                .build();
        return cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode datos(HttpResponse<String> respuesta) {
        return json.readTree(respuesta.body());
    }

    private String horario(String hora, String veterinario) {
        return """
                {"fecha":"%s","hora":"%s","veterinario":"%s"}
                """.formatted(fecha, hora, veterinario);
    }

    private long crearHorario(String hora, String veterinario) throws Exception {
        var respuesta = llamar("POST", "/api/horarios", horario(hora, veterinario));
        assertEquals(201, respuesta.statusCode(), respuesta.body());
        return datos(respuesta).get("id").asLong();
    }

    private String cita(long horarioId) {
        return """
                {"horarioId":%d,"dueno":" Sofía Pérez ","correo":"sofia@example.com",
                 "mascota":"Luna","especie":"Perro","servicio":"Vacunación","motivo":"Control anual"}
                """.formatted(horarioId);
    }

    private long reservar(long horarioId) throws Exception {
        var respuesta = llamar("POST", "/api/citas", cita(horarioId));
        assertEquals(201, respuesta.statusCode(), respuesta.body());
        return datos(respuesta).get("id").asLong();
    }

    private boolean reservado(long horarioId) {
        return jdbc.queryForObject("select reservado from horario where id = ?", Boolean.class, horarioId);
    }

    @Test
    void creaHorarioYFiltraDisponibilidadPorFecha() throws Exception {
        assertEquals(2, datos(llamar("GET", "/api/horarios?fecha=" + fecha, null)).size());
        assertEquals(0, datos(llamar("GET", "/api/horarios?fecha=" + fecha.plusDays(1), null)).size());
    }

    @Test
    void rechazaHorarioDuplicadoParaElMismoVeterinario() throws Exception {
        assertEquals(409, llamar("POST", "/api/horarios", horario("09:00", "Dra. Veterinaria")).statusCode());
        assertEquals(201, llamar("POST", "/api/horarios", horario("09:00", "Dr. Otro")).statusCode());
    }

    @Test
    void rechazaFechaPasadaYSegundosEnElHorario() throws Exception {
        String pasado = horario("09:00", "Veterinario").replace(fecha.toString(), fecha.minusDays(5).toString());
        assertEquals(400, llamar("POST", "/api/horarios", pasado).statusCode());
        assertEquals(400, llamar("POST", "/api/horarios", horario("09:00:01", "Veterinario")).statusCode());
    }

    @Test
    void reservaCitaYRetiraElHorarioDeLosDisponibles() throws Exception {
        long id = reservar(horarioUno);
        JsonNode cita = datos(llamar("GET", "/api/citas/" + id, null));
        assertEquals("Sofía Pérez", cita.get("dueno").asText());
        assertEquals("RESERVADA", cita.get("estado").asText());
        assertEquals("Dra. Veterinaria", cita.get("veterinario").asText());
        assertEquals("Vacunación", cita.get("servicio").asText());
        assertEquals(fecha.toString(), cita.get("fecha").asText());
        assertTrue(reservado(horarioUno));
        assertEquals(1, datos(llamar("GET", "/api/horarios", null)).size());
    }

    @Test
    void noPermiteDosCitasEnElMismoHorario() throws Exception {
        reservar(horarioUno);
        assertEquals(409, llamar("POST", "/api/citas", cita(horarioUno)).statusCode());
        assertEquals(1, jdbc.queryForObject("select count(*) from cita", Integer.class));
    }

    @Test
    void dosSolicitudesConcurrentesReservanUnaSolaCita() throws Exception {
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        try {
            var uno = ejecutor.submit(() -> {
                inicio.await();
                return llamar("POST", "/api/citas", cita(horarioUno)).statusCode();
            });
            var dos = ejecutor.submit(() -> {
                inicio.await();
                return llamar("POST", "/api/citas", cita(horarioUno)).statusCode();
            });
            inicio.countDown();
            var estados = new ArrayList<>(List.of(uno.get(15, TimeUnit.SECONDS), dos.get(15, TimeUnit.SECONDS)));
            estados.sort(Integer::compareTo);
            assertEquals(List.of(201, 409), estados);
            assertEquals(1, jdbc.queryForObject("select count(*) from cita", Integer.class));
        } finally {
            ejecutor.shutdownNow();
        }
    }

    @Test
    void reprogramarLiberaElHorarioAnterior() throws Exception {
        long id = reservar(horarioUno);
        var respuesta = llamar("PUT", "/api/citas/" + id, cita(horarioDos));
        assertEquals(200, respuesta.statusCode(), respuesta.body());
        assertEquals(horarioDos, datos(respuesta).get("horarioId").asLong());
        assertEquals("10:00:00", datos(respuesta).get("hora").asText());
        assertFalse(reservado(horarioUno));
        assertTrue(reservado(horarioDos));
    }

    @Test
    void reprogramarAHorarioOcupadoConservaLaReservaAnterior() throws Exception {
        long id = reservar(horarioUno);
        reservar(horarioDos);
        assertEquals(409, llamar("PUT", "/api/citas/" + id, cita(horarioDos)).statusCode());
        assertTrue(reservado(horarioUno));
        assertTrue(reservado(horarioDos));
        assertEquals(horarioUno, datos(llamar("GET", "/api/citas/" + id, null)).get("horarioId").asLong());
    }

    @Test
    void permiteEditarDatosSinCambiarHorario() throws Exception {
        long id = reservar(horarioUno);
        assertEquals(200, llamar("PUT", "/api/citas/" + id,
                cita(horarioUno).replace("Control anual", "Cambio de motivo")).statusCode());
        assertEquals("Cambio de motivo", datos(llamar("GET", "/api/citas/" + id, null)).get("motivo").asText());
        assertTrue(reservado(horarioUno));
    }

    @Test
    void cancelarConservaHistorialYPermiteOtraReserva() throws Exception {
        long anterior = reservar(horarioUno);
        assertEquals(204, llamar("DELETE", "/api/citas/" + anterior, null).statusCode());
        assertFalse(reservado(horarioUno));
        long nueva = reservar(horarioUno);
        assertEquals(204, llamar("DELETE", "/api/citas/" + anterior, null).statusCode());
        assertTrue(reservado(horarioUno));
        assertEquals("CANCELADA", datos(llamar("GET", "/api/citas/" + anterior, null)).get("estado").asText());
        assertEquals("RESERVADA", datos(llamar("GET", "/api/citas/" + nueva, null)).get("estado").asText());
    }

    @Test
    void citaAtendidaMantieneElHorarioYRechazaCancelacion() throws Exception {
        long id = reservar(horarioUno);
        assertEquals(200, llamar("PUT", "/api/citas/" + id + "/atendida", null).statusCode());
        assertEquals(200, llamar("PUT", "/api/citas/" + id + "/atendida", null).statusCode());
        assertEquals(409, llamar("DELETE", "/api/citas/" + id, null).statusCode());
        assertEquals(409, llamar("PUT", "/api/citas/" + id, cita(horarioDos)).statusCode());
        assertEquals("ATENDIDA", datos(llamar("GET", "/api/citas/" + id, null)).get("estado").asText());
        assertTrue(reservado(horarioUno));
    }

    @Test
    void horarioDesactivadoPuedeReactivarseYNoSeReservaMientrasTanto() throws Exception {
        assertEquals(204, llamar("DELETE", "/api/horarios/" + horarioUno, null).statusCode());
        assertEquals(409, llamar("POST", "/api/citas", cita(horarioUno)).statusCode());
        assertEquals(1, datos(llamar("GET", "/api/horarios", null)).size());
        assertEquals(200, llamar("PUT", "/api/horarios/" + horarioUno + "/activar", null).statusCode());
        reservar(horarioUno);
        assertEquals(409, llamar("DELETE", "/api/horarios/" + horarioUno, null).statusCode());
    }

    @Test
    void rechazaDatosInvalidosSinOcuparHorario() throws Exception {
        for (String cuerpo : List.of(
                cita(horarioUno).replace("sofia@example.com", "correo-invalido"),
                cita(horarioUno).replace("Luna", ""),
                cita(horarioUno).replace("Control anual", "   "),
                cita(horarioUno).replace("\"horarioId\":" + horarioUno, "\"horarioId\":1.5"),
                cita(horarioUno).replace("\"motivo\":", "\"desconocido\":true,\"motivo\":"))) {
            var respuesta = llamar("POST", "/api/citas", cuerpo);
            assertEquals(400, respuesta.statusCode(), respuesta.body());
        }
        assertFalse(reservado(horarioUno));
        assertEquals(0, jdbc.queryForObject("select count(*) from cita", Integer.class));
    }

    @Test
    void devuelve404ParaRecursosInexistentesY400ParaFechaIncorrecta() throws Exception {
        assertEquals(404, llamar("GET", "/api/citas/999999", null).statusCode());
        assertEquals(404, llamar("POST", "/api/citas", cita(999999)).statusCode());
        assertEquals(400, llamar("GET", "/api/citas?fecha=incorrecta", null).statusCode());
        assertEquals(400, llamar("POST", "/api/horarios", horario("25:00", "Veterinario")).statusCode());
    }

    @Test
    void listaCitasYFiltraPorFecha() throws Exception {
        reservar(horarioUno);
        reservar(horarioDos);
        assertEquals(2, datos(llamar("GET", "/api/citas?fecha=" + fecha, null)).size());
        assertEquals(0, datos(llamar("GET", "/api/citas?fecha=" + fecha.plusDays(1), null)).size());
    }

    @Test
    void corsPermiteElFrontendEn5173() throws Exception {
        var peticion = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + "/api/citas"))
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        var respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respuesta.statusCode());
        assertEquals("http://localhost:5173", respuesta.headers().firstValue("access-control-allow-origin").orElse(""));
    }
}
