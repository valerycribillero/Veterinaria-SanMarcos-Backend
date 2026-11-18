package cl.veterinariasanmarcos.productos.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
@Embeddable
public class DetalleReserva {

    @Column(length = 30, nullable = false)
    private String codigo;

    @Column(length = 150, nullable = false)
    private String nombre;

    @Column(name = "precio_unitario", precision = 10, scale = 0, nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private int cantidad;
    protected DetalleReserva() {
    }

    public DetalleReserva(Producto producto, int cantidad) {
        this.codigo = producto.getCodigo();
        this.nombre = producto.getNombre();
        this.precioUnitario = producto.getPrecio();
        this.cantidad = cantidad;
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

}
