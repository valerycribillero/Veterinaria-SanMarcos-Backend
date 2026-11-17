package cl.veterinariasanmarcos.tienda.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {
    public enum Estado { CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "nombre_completo", nullable = false, length = 150) private String nombreCompleto;
    @Column(nullable = false, length = 150) private String correo;
    @Column(nullable = false, length = 300) private String direccion;
    @Column(nullable = false, length = 30) private String metodo;
    @Column(nullable = false) private LocalDateTime fecha;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Estado estado;
    @Column(nullable = false, precision = 18, scale = 0) private BigDecimal total = BigDecimal.ZERO;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @OrderBy("id ASC") private List<DetallePedido> productos = new ArrayList<>();

    protected Pedido() {}
    public Pedido(String nombreCompleto, String correo, String direccion, String metodo) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.direccion = direccion;
        this.metodo = metodo;
        this.fecha = LocalDateTime.now();
        this.estado = Estado.CONFIRMADO;
    }
    public void agregar(Producto producto, int cantidad) {
        DetallePedido detalle = new DetallePedido(this, producto, cantidad);
        productos.add(detalle);
        total = total.add(detalle.getSubtotal());
    }
    public void cambiarEstado(Estado estado) { this.estado = estado; }
    public Long getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getMetodo() { return metodo; }
    public LocalDateTime getFecha() { return fecha; }
    public Estado getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public List<DetallePedido> getProductos() { return productos; }
}
