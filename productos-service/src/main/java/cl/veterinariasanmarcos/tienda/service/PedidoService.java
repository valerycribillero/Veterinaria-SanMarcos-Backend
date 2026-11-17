package cl.veterinariasanmarcos.tienda.service;

import java.util.HashSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.tienda.dto.*;
import cl.veterinariasanmarcos.tienda.model.*;
import cl.veterinariasanmarcos.tienda.model.Pedido.Estado;
import cl.veterinariasanmarcos.tienda.repository.*;

@Service
@Transactional
public class PedidoService {
    private final PedidoRepository pedidos;
    private final ProductoRepository productos;
    private final ProductoService catalogo;

    public PedidoService(PedidoRepository pedidos, ProductoRepository productos, ProductoService catalogo) {
        this.pedidos = pedidos;
        this.productos = productos;
        this.catalogo = catalogo;
    }

    public PedidoVista crear(NuevoPedido datos) {
        var codigos = new HashSet<String>();
        for (var linea : datos.productos()) {
            if (!codigos.add(linea.codigo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No repitas un código de producto; indica su cantidad total");
            }
        }
        Pedido pedido = new Pedido(datos.nombreCompleto(), datos.correo(), datos.direccion(), datos.metodo());
        // Todas las operaciones bloquean productos en el mismo orden para evitar interbloqueos.
        for (var linea : datos.productos().stream().sorted(java.util.Comparator.comparing(NuevoPedido.Linea::codigo)).toList()) {
            Producto producto = catalogo.bloquearActivo(linea.codigo());
            if (producto.getStock() < linea.cantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para " + linea.codigo());
            }
            pedido.agregar(producto, linea.cantidad());
            producto.setStock(producto.getStock() - linea.cantidad());
        }
        return PedidoVista.de(pedidos.saveAndFlush(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoVista> listar() {
        return pedidos.findAllByOrderByFechaDescIdDesc().stream().map(PedidoVista::de).toList();
    }
    @Transactional(readOnly = true)
    public PedidoVista buscar(Long id) {
        return PedidoVista.de(pedidos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado")));
    }
    public PedidoVista cambiarEstado(Long id, Estado nuevoEstado) {
        Pedido pedido = pedidos.bloquear(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        Estado anterior = pedido.getEstado();
        // Repetir una actualización no debe devolver el stock dos veces.
        if (anterior == nuevoEstado) return PedidoVista.de(pedido);
        boolean permitido = anterior == Estado.CONFIRMADO && (nuevoEstado == Estado.ENVIADO || nuevoEstado == Estado.CANCELADO)
                || anterior == Estado.ENVIADO && nuevoEstado == Estado.ENTREGADO;
        if (!permitido) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se permite pasar de " + anterior + " a " + nuevoEstado);
        }
        if (nuevoEstado == Estado.CANCELADO) {
            for (var detalle : pedido.getProductos().stream().sorted(java.util.Comparator.comparing(DetallePedido::getCodigo)).toList()) {
                // También se devuelve el stock de productos desactivados.
                Producto producto = productos.bloquear(detalle.getCodigo()).orElseThrow();
                producto.setStock(Math.addExact(producto.getStock(), detalle.getCantidad()));
            }
        }
        pedido.cambiarEstado(nuevoEstado);
        return PedidoVista.de(pedido);
    }
}
