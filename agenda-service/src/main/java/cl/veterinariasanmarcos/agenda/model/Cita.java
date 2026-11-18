package cl.veterinariasanmarcos.agenda.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import cl.veterinariasanmarcos.agenda.dto.DatosCita;

@Entity
@Table(name = "cita")
public class Cita {
    public enum Estado {
        RESERVADA, ATENDIDA, CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "horario_id", nullable = false)
    private Long horarioId;

    @Column(nullable = false, length = 150)
    private String dueno;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(nullable = false, length = 100)
    private String mascota;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(nullable = false, length = 150)
    private String servicio;

    @Column(nullable = false, length = 2000)
    private String motivo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false, length = 150)
    private String veterinario;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.RESERVADA;

    protected Cita() {
    }

    public Cita(DatosCita datos, Horario horario, LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        actualizar(datos, horario);
    }

    public void actualizar(DatosCita datos, Horario horario) {
        horarioId = horario.getId();
        dueno = datos.dueno();
        correo = datos.correo();
        mascota = datos.mascota();
        especie = datos.especie();
        servicio = datos.servicio();
        motivo = datos.motivo();
        fecha = horario.getFecha();
        hora = horario.getHora();
        veterinario = horario.getVeterinario();
    }

    public void cancelar() {
        estado = Estado.CANCELADA;
    }

    public void atender() {
        estado = Estado.ATENDIDA;
    }

    public Long getId() {
        return id;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public String getDueno() {
        return dueno;
    }

    public String getCorreo() {
        return correo;
    }

    public String getMascota() {
        return mascota;
    }

    public String getEspecie() {
        return especie;
    }

    public String getServicio() {
        return servicio;
    }

    public String getMotivo() {
        return motivo;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Estado getEstado() {
        return estado;
    }
}
