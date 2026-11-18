package cl.veterinariasanmarcos.productos.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "reserva_stock")
public class Reserva {
    public enum Estado { NUEVA, RESERVADA, LIBERADA }

    @Id

    @Column(length = 64)
    private String id;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false, length = 20)
    private Estado estado = Estado.NUEVA;

    @Column(length = 5000)
    private String firma;

    @ElementCollection

    @CollectionTable(name = "reserva_detalle", joinColumns =
    @JoinColumn(name = "reserva_id"))

    @OrderColumn(name = "posicion")
    private List<DetalleReserva> productos = new ArrayList<>();
    protected Reserva() {
    }

    public Reserva(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getFirma() {
        return firma;
    }

    public List<DetalleReserva> getProductos() {
        return productos;
    }

    public void confirmar(String firma, List<DetalleReserva> productos) {
        this.firma = firma;
        this.productos.addAll(productos);
        this.estado = Estado.RESERVADA;
    }

    public void liberar() {
        this.estado = Estado.LIBERADA;
    }

}
