package cl.veterinariasanmarcos.mensajeria.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contacto")
public class Contacto {

    public enum Estado {
        PENDIENTE,
        ATENDIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(length = 25)
    private String telefono;

    @Column(
        nullable = false,
        length = 100
    )
    private String asunto;

    @Column(
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String mensaje;

    @Column(
        name = "fecha_creacion",
        nullable = false,
        updatable = false
    )
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 20
    )
    private Estado estado;

    protected Contacto() {
    }

    public Contacto(
        String nombreCompleto,
        String correo,
        String telefono,
        String asunto,
        String mensaje
    ) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.telefono = telefono;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = Estado.PENDIENTE;
    }

    public Long getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void marcarAtendido() {
        this.estado = Estado.ATENDIDO;
    }
}
