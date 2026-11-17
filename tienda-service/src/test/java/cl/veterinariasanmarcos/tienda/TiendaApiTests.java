package cl.veterinariasanmarcos.tienda;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import cl.veterinariasanmarcos.tienda.dto.DatosProducto;
import cl.veterinariasanmarcos.tienda.service.ProductoService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TiendaApiTests {
    @LocalServerPort int puerto;
    @Autowired JdbcTemplate jdbc;
    @Autowired ProductoService catalogo;
    final HttpClient cliente = HttpClient.newHttpClient();
    final ObjectMapper json = new ObjectMapper();

    @BeforeAll void migracionesCreanCatalogo() {
        assertEquals(22, jdbc.queryForObject("select count(*) from producto", Integer.class));
    }
    @BeforeEach void preparar() {
        jdbc.update("delete from detalle_pedido");
        jdbc.update("delete from pedido");
        jdbc.update("delete from producto");
        catalogo.crear(new DatosProducto("A", "Producto A", "Prueba", new BigDecimal("4200"), 5,
                "/img/Amoxibay.jpg", "", "Unidad", "Perro"));
        catalogo.crear(new DatosProducto("B", "Producto B", "Prueba", new BigDecimal("6800"), 1,
                "/img/Enrox.jpg", "", "Unidad", "Gato"));
    }
    HttpResponse<String> llamar(String metodo, String ruta, String cuerpo) throws Exception {
        return cliente.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());
    }
    String pedido(String lineas) {
        return """
            {"nombreCompleto":" Sofía Pérez ","correo":"sofia@example.com",
             "direccion":"Av. Brasil 2140","metodo":"Transferencia","productos":%s}
            """.formatted(lineas);
    }
    int stock(String codigo) { return catalogo.buscar(codigo).getStock(); }
    long crearPedido() throws Exception {
        var respuesta = llamar("POST", "/api/pedidos", pedido("[{\"codigo\":\"A\",\"cantidad\":2}]"));
        assertEquals(201, respuesta.statusCode(), respuesta.body());
        return json.readTree(respuesta.body()).get("id").asLong();
    }
    String producto(String codigo, int stock, int precio) {
        return """
            {"codigo":"%s","nombre":"Producto modificado","categoria":"Prueba","precio":%d,
             "stock":%d,"imagen":"/img/Amoxibay.jpg","principioActivo":"","presentacion":"Unidad","especie":"Perro"}
            """.formatted(codigo, precio, stock);
    }
    @Test void productosSeCreanEditanYDesactivan() throws Exception {
        assertEquals(201, llamar("POST", "/api/productos", producto("C", 3, 2500)).statusCode());
        assertEquals(409, llamar("POST", "/api/productos", producto("C", 3, 2500)).statusCode());
        assertEquals(200, llamar("PUT", "/api/productos/C", producto("C", 8, 3000)).statusCode());
        assertEquals(8, stock("C"));
        assertEquals(400, llamar("PUT", "/api/productos/C", producto("D", 8, 3000)).statusCode());
        assertEquals(200, llamar("PATCH", "/api/productos/C/stock", "{\"stock\":0}").statusCode());
        assertEquals(0, stock("C"));
        assertEquals(204, llamar("DELETE", "/api/productos/C", null).statusCode());
        assertEquals(404, llamar("GET", "/api/productos/C", null).statusCode());
        assertEquals(2, json.readTree(llamar("GET", "/api/productos", null).body()).size());
    }
    @Test void rechazaStockYPreciosInvalidos() throws Exception {
        for (String cuerpo : List.of("{\"stock\":-1}", "{\"stock\":1.5}", "{\"stock\":null}", "{\"stock\":1000001}")) {
            assertEquals(400, llamar("PATCH", "/api/productos/A/stock", cuerpo).statusCode());
        }
        assertEquals(400, llamar("POST", "/api/productos", producto("C", 0, 0)).statusCode());
        assertEquals(400, llamar("POST", "/api/productos", producto("C", -1, 100)).statusCode());
        assertEquals(400, llamar("POST", "/api/productos", producto("C", 0, 100).replace("100", "100.5")).statusCode());
        assertEquals(5, stock("A"));
    }
    @Test void pedidoCalculaTotalesYDescuentaStock() throws Exception {
        var respuesta = llamar("POST", "/api/pedidos", pedido("[{\"codigo\":\"A\",\"cantidad\":2},{\"codigo\":\"B\",\"cantidad\":1}]"));
        assertEquals(201, respuesta.statusCode(), respuesta.body());
        JsonNode datos = json.readTree(respuesta.body());
        assertEquals(15200, datos.get("total").asInt());
        assertEquals("Sofía Pérez", datos.get("nombreCompleto").asText());
        assertEquals("CONFIRMADO", datos.get("estado").asText());
        assertEquals(3, stock("A"));
        assertEquals(0, stock("B"));
        assertEquals(200, llamar("GET", "/api/pedidos/" + datos.get("id").asLong(), null).statusCode());
        assertEquals(1, json.readTree(llamar("GET", "/api/pedidos", null).body()).size());
    }
    @Test void stockInsuficienteRevierteTodoElPedido() throws Exception {
        var respuesta = llamar("POST", "/api/pedidos", pedido("[{\"codigo\":\"A\",\"cantidad\":2},{\"codigo\":\"B\",\"cantidad\":2}]"));
        assertEquals(409, respuesta.statusCode());
        assertEquals(5, stock("A"));
        assertEquals(1, stock("B"));
        assertEquals(0, jdbc.queryForObject("select count(*) from pedido", Integer.class));
        assertEquals(0, jdbc.queryForObject("select count(*) from detalle_pedido", Integer.class));
    }
    @Test void productoInexistenteRevierteStock() throws Exception {
        assertEquals(404, llamar("POST", "/api/pedidos", pedido("[{\"codigo\":\"A\",\"cantidad\":2},{\"codigo\":\"Z\",\"cantidad\":1}]")).statusCode());
        assertEquals(5, stock("A"));
    }
    @Test void rechazaPedidosInvalidosYPreciosDelCliente() throws Exception {
        for (String lineas : List.of("[]", "[null]", "[{\"codigo\":\"A\",\"cantidad\":0}]", "[{\"codigo\":\"A\",\"cantidad\":1.5}]",
                "[{\"codigo\":\"A\",\"cantidad\":-1}]", "[{\"codigo\":\"A\",\"cantidad\":null}]",
                "[{\"codigo\":\"A\",\"cantidad\":1,\"precio\":1}]",
                "[{\"codigo\":\"A\",\"cantidad\":1},{\"codigo\":\"A\",\"cantidad\":1}]")) {
            assertEquals(400, llamar("POST", "/api/pedidos", pedido(lineas)).statusCode(), lineas);
        }
        assertEquals(400, llamar("POST", "/api/pedidos", pedido("[{\"codigo\":\"A\",\"cantidad\":1}]").replace("sofia@example.com", "invalido")).statusCode());
        assertEquals(5, stock("A"));
    }
    @Test void cancelarDevuelveStockSoloUnaVezInclusoSiProductoEstaDesactivado() throws Exception {
        long id = crearPedido();
        catalogo.eliminar("A");
        assertEquals(200, llamar("PATCH", "/api/pedidos/" + id + "/estado", "{\"estado\":\"CANCELADO\"}").statusCode());
        assertEquals(200, llamar("PATCH", "/api/pedidos/" + id + "/estado", "{\"estado\":\"CANCELADO\"}").statusCode());
        assertEquals(5, jdbc.queryForObject("select stock from producto where codigo = 'A'", Integer.class));
        assertEquals(409, llamar("PATCH", "/api/pedidos/" + id + "/estado", "{\"estado\":\"ENVIADO\"}").statusCode());
    }
    @Test void entregaRespetaElOrdenDeEstados() throws Exception {
        long id = crearPedido();
        String ruta = "/api/pedidos/" + id + "/estado";
        assertEquals(409, llamar("PATCH", ruta, "{\"estado\":\"ENTREGADO\"}").statusCode());
        assertEquals(200, llamar("PATCH", ruta, "{\"estado\":\"ENVIADO\"}").statusCode());
        assertEquals(409, llamar("PATCH", ruta, "{\"estado\":\"CANCELADO\"}").statusCode());
        assertEquals(200, llamar("PATCH", ruta, "{\"estado\":\"ENTREGADO\"}").statusCode());
        assertEquals(400, llamar("PATCH", ruta, "{\"estado\":\"PAGADO\"}").statusCode());
        assertEquals(3, stock("A"));
    }
    @Test void pedidoConservaNombreYPrecioHistoricos() throws Exception {
        long id = crearPedido();
        assertEquals(200, llamar("PUT", "/api/productos/A", producto("A", 3, 9999)).statusCode());
        JsonNode detalle = json.readTree(llamar("GET", "/api/pedidos/" + id, null).body()).get("productos").get(0);
        assertEquals("Producto A", detalle.get("nombre").asText());
        assertEquals(4200, detalle.get("precioUnitario").asInt());
    }
    @Test void dosPedidosConcurrentesNoVendenLaMismaUltimaUnidad() throws Exception {
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        Callable<Integer> compra = () -> {
            inicio.await();
            return llamar("POST", "/api/pedidos", pedido("[{\"codigo\":\"B\",\"cantidad\":1}]")).statusCode();
        };
        try {
            Future<Integer> uno = ejecutor.submit(compra);
            Future<Integer> dos = ejecutor.submit(compra);
            inicio.countDown();
            var resultados = List.of(uno.get(15, TimeUnit.SECONDS), dos.get(15, TimeUnit.SECONDS)).stream().sorted().toList();
            assertEquals(List.of(201, 409), resultados);
            assertEquals(0, stock("B"));
            assertEquals(1, jdbc.queryForObject("select count(*) from pedido", Integer.class));
        } finally { ejecutor.shutdownNow(); }
    }
    @Test void corsPermiteReact() throws Exception {
        var respuesta = cliente.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + "/api/pedidos"))
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respuesta.statusCode());
        assertEquals("http://localhost:5173", respuesta.headers().firstValue("access-control-allow-origin").orElse(""));
    }
}
