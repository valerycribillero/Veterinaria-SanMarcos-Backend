package cl.veterinariasanmarcos.ficha.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "vacunas_registro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunaRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "consulta_id",
        nullable = false
    )
    private Consulta consulta;

    @Column(
        nullable = false,
        length = 100
    )
    private String nombreVacuna;

    @Column(length = 50)
    private String lote;

    @Column(nullable = false)
    private LocalDate fechaAplicacion;

    @Column(nullable = false)
    private LocalDate fechaVencimiento; // Permite avisar al dueño antes del vencimiento
}
