package cl.veterinariasanmarcos.pedidos.service;

import org.springframework.http.HttpStatusCode;

public class PedidoPendienteException extends RuntimeException {
    private final Long pedidoId;
    private final HttpStatusCode estadoHttp;
    public PedidoPendienteException(Long pedidoId, HttpStatusCode estadoHttp, String mensaje) {
        super(mensaje);
        this.pedidoId = pedidoId;
        this.estadoHttp = estadoHttp;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public HttpStatusCode getEstadoHttp() {
        return estadoHttp;
    }

}
