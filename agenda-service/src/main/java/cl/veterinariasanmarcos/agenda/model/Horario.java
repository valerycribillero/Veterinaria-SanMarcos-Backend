package cl.veterinariasanmarcos.agenda.model;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "horario", uniqueConstraints = @UniqueConstraint(
        name = "uq_horario_veterinario", columnNames = {"fecha", "hora", "veterinario"}))
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false, length = 150)
    private String veterinario;

    @Column(nullable = false)
    private boolean reservado = false;

    @Column(nullable = false)
    private boolean activo = true;

    protected Horario() {
    }

    public Horario(LocalDate fecha, LocalTime hora, String veterinario) {
        this.fecha = fecha;
        this.hora = hora;
        this.veterinario = veterinario;
    }

    public void reservar() {
        reservado = true;
    }

    public void liberar() {
        reservado = false;
    }

    public void desactivar() {
        activo = false;
    }

    public void activar() {
        activo = true;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public boolean isReservado() {
        return reservado;
    }

    public boolean isActivo() {
        return activo;
    }
}
