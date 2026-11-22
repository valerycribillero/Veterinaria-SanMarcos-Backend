package cl.veterinariasanmarcos.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import cl.veterinariasanmarcos.pedidos.dto.NuevoPedido;
import cl.veterinariasanmarcos.pedidos.dto.ReservaProducto;

@Entity
@Table(name = "pedido")
public class Pedido {
    public enum Estado {
        PENDIENTE,
        CONFIRMADO,
        ENVIADO,
        ENTREGADO,
        CANCELACION_PENDIENTE,
        CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        unique = true,
        length = 64
    )
    private String referencia;

    @Column(
        name = "nombre_completo",
        nullable = false,
        length = 150
    )
    private String nombreCompleto;

    @Column(
        nullable = false,
        length = 150
    )
    private String correo;

    @Column(
        nullable = false,
        length = 300
    )
    private String direccion;

    @Column(
        nullable = false,
        length = 30
    )
    private String metodo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 30
    )
    private Estado estado = Estado.PENDIENTE;

    @Column(
        nullable = false,
        precision = 18,
        scale = 0
    )
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(
        mappedBy = "pedido",
        cascade = CascadeType.ALL
    )
    @OrderBy("codigo ASC")
    private List<DetallePedido> productos = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(
        String referencia,
        NuevoPedido datos
    ) {
        this.referencia = referencia;
        this.nombreCompleto = datos.nombreCompleto();
        this.correo = datos.correo();
        this.direccion = datos.direccion();
        this.metodo = datos.metodo();
        this.fecha = LocalDateTime.now();
        for (var linea : datos.productos()) {
            productos.add(new DetallePedido(
                this,
                linea.codigo(),
                linea.cantidad()
            ));
        }
    }

    public NuevoPedido solicitud() {
        List<NuevoPedido.Linea> lineas = new ArrayList<>();
        for (var producto : productos) {
            lineas.add(new NuevoPedido.Linea(
                producto.getCodigo(),
                producto.getCantidad()
            ));
        }
        return new NuevoPedido(
            nombreCompleto,
            correo,
            direccion,
            metodo,
            lineas
        );
    }

    public void confirmar(ReservaProducto reserva) {
        total = BigDecimal.ZERO;
        for (var producto : productos) {
            ReservaProducto.Linea precio = null;
            for (var linea : reserva.productos()) {
                if (linea.codigo().equals(producto.getCodigo())) {
                    precio = linea;
                    break;
                }
            }
            if (precio == null) {
                throw new IllegalStateException("Falta un producto en la reserva");
            }
            if (producto.getCantidad() != precio.cantidad()) {
                throw new IllegalStateException("La reserva no coincide con el pedido");
            }
            producto.fijarPrecio(
                precio.nombre(),
                precio.precioUnitario()
            );
            total = total.add(producto.getSubtotal());
        }
        estado = Estado.CONFIRMADO;
    }

    public void cambiarEstado(Estado estado) {
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getMetodo() {
        return metodo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<DetallePedido> getProductos() {
        return productos;
    }
}
