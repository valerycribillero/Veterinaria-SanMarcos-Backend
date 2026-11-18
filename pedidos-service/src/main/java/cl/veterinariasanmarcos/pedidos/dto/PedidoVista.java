package cl.veterinariasanmarcos.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import cl.veterinariasanmarcos.pedidos.model.Pedido;

public record PedidoVista(Long id, String nombreCompleto, String correo, String direccion,
        String metodo, LocalDateTime fecha, Pedido.Estado estado, BigDecimal total, List<Detalle> productos) {
    public record Detalle(String codigo, String nombre, BigDecimal precioUnitario, int cantidad, BigDecimal subtotal) {
    }

    public static PedidoVista de(Pedido pedido) {
        List<Detalle> detalles = new ArrayList<>();
        for (var producto : pedido.getProductos()) {
            detalles.add(new Detalle(producto.getCodigo(), producto.getNombre(), producto.getPrecioUnitario(),
                    producto.getCantidad(), producto.getSubtotal()));
        }
        return new PedidoVista(pedido.getId(), pedido.getNombreCompleto(), pedido.getCorreo(),
                pedido.getDireccion(), pedido.getMetodo(), pedido.getFecha(), pedido.getEstado(),
                pedido.getTotal(), detalles);
    }
}
