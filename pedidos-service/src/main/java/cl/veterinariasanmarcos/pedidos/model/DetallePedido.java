package cl.veterinariasanmarcos.pedidos.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre = "";

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 0)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, precision = 18, scale = 0)
    private BigDecimal subtotal = BigDecimal.ZERO;
    protected DetallePedido() {
    }

    public DetallePedido(Pedido pedido, String codigo, int cantidad) {
        this.pedido = pedido;
        this.codigo = codigo;
        this.cantidad = cantidad;
    }

    public void fijarPrecio(String nombre, BigDecimal precioUnitario) {
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

}
