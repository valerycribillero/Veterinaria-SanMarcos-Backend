package cl.veterinariasanmarcos.pedidos;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import cl.veterinariasanmarcos.productos.ProductosApplication;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiciosApiTests {
    private ConfigurableApplicationContext productos;
    private ConfigurableApplicationContext pedidos;
    private HttpServer conexion;
    private int puertoProductos;
    private int puertoPedidos;
    private final HttpClient cliente = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();
    private final AtomicBoolean fallarReserva = new AtomicBoolean();
    private final AtomicBoolean fallarLiberacion = new AtomicBoolean();
    private final AtomicBoolean sinConexion = new AtomicBoolean();

    private ConfigurableApplicationContext iniciar(Class<?> aplicacion, String nombre, String... extra) {
        var argumentos = new java.util.ArrayList<>(List.of(
                "--server.port=0",
                "--server.address=127.0.0.1",
                "--spring.application.name=" + nombre,
                "--spring.datasource.url=jdbc:h2:mem:" + nombre + ";MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000",
                "--spring.datasource.username=sa",
                "--spring.datasource.password=",
                "--spring.jpa.hibernate.ddl-auto=validate",
                "--spring.jpa.open-in-view=false",
                "--spring.jackson.deserialization.fail-on-unknown-properties=true",
                "--spring.jackson.deserialization.accept-float-as-int=false",
                "--spring.flyway.locations=filesystem:" + Path.of("..", nombre + "-service",
                        "src/main/resources/db/migracion").toAbsolutePath().normalize(),
                "--logging.level.root=WARN",
                "--spring.main.banner-mode=off"
        ));
        argumentos.addAll(List.of(extra));
        return SpringApplication.run(aplicacion, argumentos.toArray(String[]::new));
    }

    @BeforeAll
    void iniciarServicios() throws Exception {
        try {
            productos = iniciar(ProductosApplication.class, "productos");
            puertoProductos = ((WebServerApplicationContext) productos).getWebServer().getPort();
            assertEquals(22, productos.getBean(JdbcTemplate.class)
                    .queryForObject("select count(*) from producto", Integer.class));
            conexion = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            conexion.createContext("/", intercambio -> {
                try {
                    if (sinConexion.get()) {
                        intercambio.sendResponseHeaders(503, -1);
                        return;
                    }
                    String metodo = intercambio.getRequestMethod();
                    var solicitud = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puertoProductos
                            + intercambio.getRequestURI()))
                            .header("Content-Type", "application/json")
                            .method(metodo, HttpRequest.BodyPublishers.ofByteArray(intercambio.getRequestBody().readAllBytes()))
                            .build();
                    var respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofByteArray());
                    if (metodo.equals("PUT") && fallarReserva.getAndSet(false)
                            || metodo.equals("DELETE") && fallarLiberacion.getAndSet(false)) {
                        // Productos ya confirmó, pero Pedidos no recibe la confirmación.
                        intercambio.sendResponseHeaders(503, -1);
                        return;
                    }
                    intercambio.getResponseHeaders().set("Content-Type", "application/json");
                    if (respuesta.statusCode() == 204) {
                        intercambio.sendResponseHeaders(204, -1);
                    } else {
                        intercambio.sendResponseHeaders(respuesta.statusCode(), respuesta.body().length);
                        intercambio.getResponseBody().write(respuesta.body());
                    }
                } catch (Exception error) {
                    intercambio.sendResponseHeaders(503, -1);
                } finally {
                    intercambio.close();
                }
            });
            conexion.start();
            pedidos = iniciar(PedidosApplication.class, "pedidos",
                    "--productos.url=http://127.0.0.1:" + conexion.getAddress().getPort());
            puertoPedidos = ((WebServerApplicationContext) pedidos).getWebServer().getPort();
        } catch (Exception error) {
            cerrar();
            throw error;
        }
    }

    @AfterAll
    void cerrar() {
        if (pedidos != null) pedidos.close();
        if (conexion != null) conexion.stop(0);
        if (productos != null) productos.close();
    }

    @BeforeEach
    void preparar() {
        fallarReserva.set(false);
        fallarLiberacion.set(false);
        sinConexion.set(false);
        JdbcTemplate compras = pedidos.getBean(JdbcTemplate.class);
        compras.update("delete from detalle_pedido");
        compras.update("delete from pedido");
        JdbcTemplate catalogo = productos.getBean(JdbcTemplate.class);
        catalogo.update("delete from reserva_detalle");
        catalogo.update("delete from reserva_stock");
        catalogo.update("delete from producto");
        var servicio = productos.getBean(cl.veterinariasanmarcos.productos.service.ProductoService.class);
        for (String codigo : List.of("A", "B")) {
            servicio.crear(new cl.veterinariasanmarcos.productos.dto.DatosProducto(codigo, "Producto " + codigo,
                    "Prueba", new BigDecimal("4200"), 5, "/img/Amoxibay.jpg", "", "Unidad", "Perro"));
        }
    }

    HttpResponse<String> llamar(int puerto, String metodo, String ruta, String cuerpo, String... cabeceras) throws Exception {
        var solicitud = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta))
                .header("Content-Type", "application/json");
        for (int i = 0; i < cabeceras.length; i += 2) {
            solicitud.header(cabeceras[i], cabeceras[i + 1]);
        }
        return cliente.send(solicitud.method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(cuerpo)).build(), HttpResponse.BodyHandlers.ofString());
    }

    String compra(String lineas) {
        return """
                {"nombreCompleto":"Sofía Pérez","correo":"sofia@example.com",
                 "direccion":"Av. Brasil 2140","metodo":"Transferencia","productos":%s}
                """.formatted(lineas);
    }

    String compra() { return compra("[{\"codigo\":\"A\",\"cantidad\":2}]"); }
    JsonNode datos(HttpResponse<String> respuesta) { return json.readTree(respuesta.body()); }
    int stock(String codigo) {
        return productos.getBean(JdbcTemplate.class)
                .queryForObject("select stock from producto where codigo = ?", Integer.class, codigo);
    }
    long crear() throws Exception {
        var respuesta = llamar(puertoPedidos, "POST", "/api/pedidos", compra());
        assertEquals(201, respuesta.statusCode(), respuesta.body());
        return datos(respuesta).get("id").asLong();
    }

    @Test
    void listadoYDetalleUsanLosMismosProductos() throws Exception {
        var lista = llamar(puertoProductos, "GET", "/api/productos", null);
        assertEquals(2, datos(lista).size());
        assertEquals("A", datos(llamar(puertoProductos, "GET", "/api/productos/A", null)).get("codigo").asText());
        assertEquals(404, llamar(puertoProductos, "GET", "/api/productos/Z", null).statusCode());
    }

    @Test
    void actualizarStockYDesactivarProducto() throws Exception {
        assertEquals(200, llamar(puertoProductos, "PATCH", "/api/productos/A/stock", "{\"stock\":8}").statusCode());
        assertEquals(8, stock("A"));
        assertEquals(204, llamar(puertoProductos, "DELETE", "/api/productos/A", null).statusCode());
        assertEquals(404, llamar(puertoProductos, "GET", "/api/productos/A", null).statusCode());
        assertEquals(1, datos(llamar(puertoProductos, "GET", "/api/productos", null)).size());
    }

    @Test
    void compraCalculaPrecioDelServidorYDescuentaStock() throws Exception {
        long id = crear();
        var pedido = datos(llamar(puertoPedidos, "GET", "/api/pedidos/" + id, null));
        assertEquals("CONFIRMADO", pedido.get("estado").asText());
        assertEquals(8400, pedido.get("total").asInt());
        assertEquals(3, stock("A"));
    }

    @Test
    void falloEnUnaLineaNoDescuentaLasDemas() throws Exception {
        var respuesta = llamar(puertoPedidos, "POST", "/api/pedidos",
                compra("[{\"codigo\":\"A\",\"cantidad\":2},{\"codigo\":\"B\",\"cantidad\":6}]"));
        assertEquals(409, respuesta.statusCode());
        assertTrue(datos(respuesta).has("pedidoId"));
        assertEquals(5, stock("A"));
        assertEquals(5, stock("B"));
    }

    @Test
    void confirmacionPerdidaSeRecuperaSinCobrarStockDosVeces() throws Exception {
        fallarReserva.set(true);
        var fallo = llamar(puertoPedidos, "POST", "/api/pedidos", compra());
        assertEquals(503, fallo.statusCode());
        long id = datos(fallo).get("pedidoId").asLong();
        assertEquals(3, stock("A"));
        // El precio reservado debe conservarse aunque el catálogo cambie antes del reintento.
        productos.getBean(JdbcTemplate.class).update("update producto set precio=9999 where codigo='A'");
        var respuesta = llamar(puertoPedidos, "POST", "/api/pedidos/" + id + "/confirmar", null);
        assertEquals(200, respuesta.statusCode(), respuesta.body());
        assertEquals("CONFIRMADO", datos(respuesta).get("estado").asText());
        assertEquals(8400, datos(respuesta).get("total").asInt());
        assertEquals(3, stock("A"));
    }

    @Test
    void reenvioConMismaReferenciaReutilizaPedido() throws Exception {
        var uno = llamar(puertoPedidos, "POST", "/api/pedidos", compra(), "Idempotency-Key", "compra-1");
        var dos = llamar(puertoPedidos, "POST", "/api/pedidos", compra(), "Idempotency-Key", "compra-1");
        assertEquals(201, uno.statusCode(), uno.body());
        assertEquals(datos(uno).get("id"), datos(dos).get("id"));
        assertEquals(3, stock("A"));
        var distinto = llamar(puertoPedidos, "POST", "/api/pedidos",
                compra("[{\"codigo\":\"A\",\"cantidad\":1}]"), "Idempotency-Key", "compra-1");
        assertEquals(409, distinto.statusCode());
    }

    @Test
    void cancelacionPerdidaSeRecuperaSinDevolverStockDosVeces() throws Exception {
        long id = crear();
        llamar(puertoProductos, "DELETE", "/api/productos/A", null);
        fallarLiberacion.set(true);
        String ruta = "/api/pedidos/" + id + "/estado";
        var fallo = llamar(puertoPedidos, "PATCH", ruta, "{\"estado\":\"CANCELADO\"}");
        assertEquals(503, fallo.statusCode());
        assertEquals(5, stock("A"));
        assertEquals("CANCELACION_PENDIENTE", datos(llamar(puertoPedidos, "GET", "/api/pedidos/" + id, null)).get("estado").asText());
        var respuesta = llamar(puertoPedidos, "PATCH", ruta, "{\"estado\":\"CANCELADO\"}");
        assertEquals("CANCELADO", datos(respuesta).get("estado").asText());
        assertEquals(5, stock("A"));
        assertEquals(409, llamar(puertoPedidos, "POST", "/api/pedidos/" + id + "/confirmar", null).statusCode());
    }

    @Test
    void servicioCaidoConservaSolicitudYPermiteReintentar() throws Exception {
        sinConexion.set(true);
        var fallo = llamar(puertoPedidos, "POST", "/api/pedidos", compra());
        assertEquals(503, fallo.statusCode());
        long id = datos(fallo).get("pedidoId").asLong();
        assertEquals("PENDIENTE", datos(llamar(puertoPedidos, "GET", "/api/pedidos/" + id, null)).get("estado").asText());
        assertEquals(5, stock("A"));
        sinConexion.set(false);
        assertEquals(200, llamar(puertoPedidos, "POST", "/api/pedidos/" + id + "/confirmar", null).statusCode());
        assertEquals(3, stock("A"));
    }

    @Test
    void cancelarPendienteImpideReservarMasTarde() throws Exception {
        sinConexion.set(true);
        long id = datos(llamar(puertoPedidos, "POST", "/api/pedidos", compra())).get("pedidoId").asLong();
        sinConexion.set(false);
        assertEquals(200, llamar(puertoPedidos, "PATCH", "/api/pedidos/" + id + "/estado", "{\"estado\":\"CANCELADO\"}").statusCode());
        assertEquals(409, llamar(puertoPedidos, "POST", "/api/pedidos/" + id + "/confirmar", null).statusCode());
        assertEquals(5, stock("A"));
    }

    @Test
    void dosComprasConcurrentesRespetanStock() throws Exception {
        llamar(puertoProductos, "PATCH", "/api/productos/A/stock", "{\"stock\":2}");
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        try {
            var uno = ejecutor.submit(() -> llamar(puertoPedidos, "POST", "/api/pedidos", compra()).statusCode());
            var dos = ejecutor.submit(() -> llamar(puertoPedidos, "POST", "/api/pedidos", compra()).statusCode());
            var estados = new java.util.ArrayList<>(List.of(uno.get(20, TimeUnit.SECONDS), dos.get(20, TimeUnit.SECONDS)));
            estados.sort(Integer::compareTo);
            assertEquals(List.of(201, 409), estados);
            assertEquals(0, stock("A"));
        } finally {
            ejecutor.shutdownNow();
        }
    }

    @Test
    void respetaEstadosDeEntrega() throws Exception {
        String ruta = "/api/pedidos/" + crear() + "/estado";
        assertEquals(409, llamar(puertoPedidos, "PATCH", ruta, "{\"estado\":\"ENTREGADO\"}").statusCode());
        assertEquals(200, llamar(puertoPedidos, "PATCH", ruta, "{\"estado\":\"ENVIADO\"}").statusCode());
        assertEquals(409, llamar(puertoPedidos, "PATCH", ruta, "{\"estado\":\"CANCELADO\"}").statusCode());
        assertEquals(200, llamar(puertoPedidos, "PATCH", ruta, "{\"estado\":\"ENTREGADO\"}").statusCode());
    }

    @Test
    void validaEntradaYRechazaPreciosDelNavegador() throws Exception {
        for (String lineas : List.of("[]", "[null]", "[{\"codigo\":\"A\",\"cantidad\":0}]",
                "[{\"codigo\":\"A\",\"cantidad\":1.5}]", "[{\"codigo\":\"A\",\"cantidad\":1,\"precio\":1}]",
                "[{\"codigo\":\"A\",\"cantidad\":1},{\"codigo\":\"A\",\"cantidad\":1}]")) {
            var respuesta = llamar(puertoPedidos, "POST", "/api/pedidos", compra(lineas));
            assertEquals(400, respuesta.statusCode(), respuesta.body());
        }
        assertEquals(400, llamar(puertoProductos, "PATCH", "/api/productos/A/stock", "{\"stock\":-1}").statusCode());
        assertEquals(5, stock("A"));
    }

    @Test
    void corsPermiteReactEnAmbosServicios() throws Exception {
        for (int puerto : List.of(puertoProductos, puertoPedidos)) {
            String ruta = puerto == puertoProductos ? "/api/productos" : "/api/pedidos";
            var respuesta = llamar(puerto, "OPTIONS", ruta, null, "Origin", "http://localhost:5173",
                    "Access-Control-Request-Method", "POST", "Access-Control-Request-Headers", "content-type,idempotency-key");
            assertEquals(200, respuesta.statusCode());
            assertEquals("http://localhost:5173", respuesta.headers().firstValue("access-control-allow-origin").orElse(""));
        }
    }
}
