package cl.veterinariasanmarcos.pedidos.client;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import cl.veterinariasanmarcos.pedidos.dto.NuevoPedido;
import cl.veterinariasanmarcos.pedidos.dto.ReservaProducto;
@Component
public class ProductosClient {
    private final RestClient cliente;
    public ProductosClient(@Value("${productos.url}") String url) {
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        var peticiones = new JdkClientHttpRequestFactory(http);
        peticiones.setReadTimeout(Duration.ofSeconds(5));
        cliente = RestClient.builder().baseUrl(url).requestFactory(peticiones).build();
    }

    public ReservaProducto reservar(String referencia, List<NuevoPedido.Linea> productos) {
        return cliente.put().uri("/api/reservas-stock/{id}", referencia)
                .body(Map.of("productos", productos)).retrieve().body(ReservaProducto.class);
    }

    public void liberar(String referencia) {
        cliente.delete().uri("/api/reservas-stock/{id}", referencia).retrieve().toBodilessEntity();
    }
}
