package cl.veterinariasanmarcos.tienda.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import cl.veterinariasanmarcos.tienda.model.Pedido;

public record PedidoVista(Long id, String nombreCompleto, String correo, String direccion,
        String metodo, LocalDateTime fecha, Pedido.Estado estado, BigDecimal total, List<Detalle> productos) {
    public record Detalle(String codigo, String nombre, BigDecimal precioUnitario, int cantidad, BigDecimal subtotal) {}
    public static PedidoVista de(Pedido pedido) {
        return new PedidoVista(pedido.getId(), pedido.getNombreCompleto(), pedido.getCorreo(),
                pedido.getDireccion(), pedido.getMetodo(), pedido.getFecha(), pedido.getEstado(),
                pedido.getTotal(), pedido.getProductos().stream().map(d -> new Detalle(d.getCodigo(),
                d.getNombre(), d.getPrecioUnitario(), d.getCantidad(), d.getSubtotal())).toList());
    }
}
