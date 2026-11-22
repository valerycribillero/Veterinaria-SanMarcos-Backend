package cl.veterinariasanmarcos.detalleconsultas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_consultas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long consultaId;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false, length = 80)
    private String nombrePaciente;

    @Column(nullable = false)
    private Long duenoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoDetalleClinico tipoDetalle;

    @Column(length = 15)
    private String codigoCatalogo;

    @Column(nullable = false, length = 120)
    private String descripcionItem; 

    @Column(length = 80)
    private String dosis; 

    @Column(length = 80)
    private String frecuencia; 

    private Integer duracionDias;

    @Column(length = 50)
    private String viaAdministracion; 

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    private LocalDate fechaProximaDosis;

    @Column(columnDefinition = "TEXT")
    private String observacionesMedicas;

    @Column(nullable = false)
    private Long profesionalId;

    @Column(nullable = false, length = 120)
    private String nombreProfesional;

    @Column(updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.cantidad == null) {
            this.cantidad = 1;
        }
    }
}