package cl.veterinariasanmarcos.pedidos.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.pedidos.dto.*;
import cl.veterinariasanmarcos.pedidos.model.Pedido;
import cl.veterinariasanmarcos.pedidos.model.Pedido.Estado;
import cl.veterinariasanmarcos.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {
    private final PedidoRepository pedidos;
    private final RestClient conexionProductos;
    private final TransactionTemplate transaccion;
    public PedidoService(PedidoRepository pedidos, TransactionTemplate transaccion,
            @Value("${productos.url}") String urlProductos) {
        this.pedidos = pedidos;
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        var peticiones = new JdkClientHttpRequestFactory(http);
        peticiones.setReadTimeout(Duration.ofSeconds(5));
        this.conexionProductos = RestClient.builder()
                .baseUrl(urlProductos)
                .requestFactory(peticiones)
                .build();
        this.transaccion = transaccion;
    }

    public PedidoVista crear(String referencia, NuevoPedido datos) {
        var lineas = new ArrayList<>(datos.productos());
        lineas.sort(Comparator.comparing(NuevoPedido.Linea::codigo));
        var codigos = new HashSet<String>();
        for (var linea : lineas) {
            if (!codigos.add(linea.codigo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No repitas productos");
            }
        }
        NuevoPedido solicitud = new NuevoPedido(datos.nombreCompleto(), datos.correo(), datos.direccion(), datos.metodo(), lineas);
        Long id;
        try {
            id = transaccion.execute(estado -> {
                var existente = pedidos.findByReferencia(referencia);
                if (existente.isPresent()) {
                    return existente.get().getId();
                }
                return pedidos.saveAndFlush(new Pedido(referencia, solicitud)).getId();
            });
        } catch (DataIntegrityViolationException error) {
            // Otro envío con la misma referencia pudo guardarse mientras tanto.
            id = pedidos.findByReferencia(referencia).orElseThrow(() -> error).getId();
        }
        Long pedidoId = id;
        transaccion.executeWithoutResult(estado -> {
            Pedido pedido = buscarBloqueado(pedidoId);
            if (!pedido.solicitud().equals(solicitud)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La referencia pertenece a otra compra");
            }
        });
        return confirmar(id);
    }

    public PedidoVista confirmar(Long id) {
        try {
            return transaccion.execute(estado -> {
                Pedido pedido = buscarBloqueado(id);
                if (pedido.getEstado() == Estado.CANCELADO || pedido.getEstado() == Estado.CANCELACION_PENDIENTE) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El pedido está cancelado o cancelándose");
                }
                if (pedido.getEstado() == Estado.PENDIENTE) {
                    var reserva = reservarProductos(pedido);
                    pedido.confirmar(reserva);
                }
                return PedidoVista.de(pedido);
            });
        } catch (RestClientResponseException error) {
            throw new PedidoPendienteException(id, error.getStatusCode(),
                    "Productos rechazó la reserva. Consulta o reintenta el pedido " + id + ". " + error.getResponseBodyAsString());
        } catch (RestClientException error) {
            throw new PedidoPendienteException(id, HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo confirmar el stock. El pedido " + id + " quedó pendiente; reintenta su confirmación.");
        }
    }

    public List<PedidoVista> listar() {
        return transaccion.execute(estado -> {
            List<PedidoVista> resultado = new ArrayList<>();
            for (Pedido pedido : pedidos.findAllByOrderByFechaDescIdDesc()) {
                resultado.add(PedidoVista.de(pedido));
            }
            return resultado;
        });
    }

    public PedidoVista buscar(Long id) {
        return transaccion.execute(estado -> PedidoVista.de(pedidos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"))));
    }

    public PedidoVista cambiarEstado(Long id, Estado nuevoEstado) {
        if (nuevoEstado == Estado.CANCELADO) {
            return cancelar(id);
        }
        return transaccion.execute(estado -> {
            Pedido pedido = buscarBloqueado(id);
            Estado anterior = pedido.getEstado();
            if (anterior == nuevoEstado) {
                return PedidoVista.de(pedido);
            }
            boolean permitido = anterior == Estado.CONFIRMADO && nuevoEstado == Estado.ENVIADO
                    || anterior == Estado.ENVIADO && nuevoEstado == Estado.ENTREGADO;
            if (!permitido) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cambio de estado no permitido");
            }
            pedido.cambiarEstado(nuevoEstado);
            return PedidoVista.de(pedido);
        });
    }

    private PedidoVista cancelar(Long id) {
        // Se conserva la intención antes de pedir a Productos que devuelva el stock.
        transaccion.executeWithoutResult(estado -> {
            Pedido pedido = buscarBloqueado(id);
            if (pedido.getEstado() == Estado.ENVIADO || pedido.getEstado() == Estado.ENTREGADO) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El pedido ya fue enviado");
            }
            if (pedido.getEstado() != Estado.CANCELADO) {
                pedido.cambiarEstado(Estado.CANCELACION_PENDIENTE);
            }
        });
        try {
            return transaccion.execute(estado -> {
                Pedido pedido = buscarBloqueado(id);
                if (pedido.getEstado() != Estado.CANCELADO) {
                    devolverProductos(pedido.getReferencia());
                    pedido.cambiarEstado(Estado.CANCELADO);
                }
                return PedidoVista.de(pedido);
            });
        } catch (RestClientException error) {
            throw new PedidoPendienteException(id, HttpStatus.SERVICE_UNAVAILABLE,
                    "La cancelación del pedido " + id + " quedó pendiente. Repite la cancelación para completarla.");
        }
    }

    // Solicita el descuento de stock al servicio de Productos.
    private ReservaProducto reservarProductos(Pedido pedido) {
        return conexionProductos.put()
                .uri("/api/reservas-stock/{id}", pedido.getReferencia())
                .body(Map.of("productos", pedido.solicitud().productos()))
                .retrieve()
                .body(ReservaProducto.class);
    }

    // Solicita devolver las unidades de un pedido cancelado.
    private void devolverProductos(String referencia) {
        conexionProductos.delete()
                .uri("/api/reservas-stock/{id}", referencia)
                .retrieve()
                .toBodilessEntity();
    }

    private Pedido buscarBloqueado(Long id) {
        return pedidos.bloquear(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }
}
