package cl.veterinariasanmarcos.tienda.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false) private Pedido pedido;
    @Column(nullable = false, length = 30) private String codigo;
    @Column(nullable = false, length = 150) private String nombre;
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 0) private BigDecimal precioUnitario;
    @Column(nullable = false) private int cantidad;
    @Column(nullable = false, precision = 18, scale = 0) private BigDecimal subtotal;

    protected DetallePedido() {}
    public DetallePedido(Pedido pedido, Producto producto, int cantidad) {
        this.pedido = pedido;
        this.codigo = producto.getCodigo();
        this.nombre = producto.getNombre();
        this.precioUnitario = producto.getPrecio();
        this.cantidad = cantidad;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
}
