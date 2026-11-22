package cl.veterinariasanmarcos.servicios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_servicio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_codigo", nullable = false)
    private ServicioCatalogo servicio;

    @Column(nullable = false)
    private Long duenoId; 

    @Column(nullable = false, length = 120)
    private String nombreDueno;

    @Column(length = 20)
    private String telefonoDueno;

    @Column(nullable = false)
    private Long pacienteId; 

    @Column(nullable = false, length = 80)
    private String nombrePaciente;

    @Column(nullable = false, length = 40)
    private String especiePaciente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fechaHoraSolicitada;

    private LocalDateTime fechaHoraPropuesta;

    private Long veterinarioAsignadoId;

    @Column(length = 120)
    private String nombreVeterinarioAsignado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;

    @Column(columnDefinition = "TEXT")
    private String observacionRecepcion;

    @Column(nullable = false)
    private Integer precioReferencialClp;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoSolicitud.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}