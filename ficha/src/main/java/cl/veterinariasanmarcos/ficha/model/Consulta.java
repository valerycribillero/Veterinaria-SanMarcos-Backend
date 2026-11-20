package cl.veterinariasanmarcos.ficha.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private Long veterinarioId; // ID del médico veterinario

    @Column(nullable = false, length = 120)
    private String nombreVeterinario;

    @Column(nullable = false)
    private LocalDateTime fechaConsulta;

    @Column(nullable = false, length = 100)
    private String tipoServicio; // Consulta general, Vacunación, Cirugía menor, Desparasitación, Control de peso

    private Double pesoKg;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivoConsulta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String medicamentosRecetados;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    private LocalDate proximoControl;

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VacunaRegistro> vacunasAplicadas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.fechaConsulta == null) {
            this.fechaConsulta = LocalDateTime.now();
        }
    }
}