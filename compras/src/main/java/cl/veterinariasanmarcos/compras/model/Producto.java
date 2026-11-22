package cl.veterinariasanmarcos.compras.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @Column(length = 10)
    private String codigo; // Ej: ME001 .. ME022

    @Column(
        nullable = false,
        length = 60
    )
    private String
            categoria; // Antibióticos, Antiparasitarios, Antiinflamatorios, Dermatología,
                       // Digestivo, Cardíaco, Analgésicos, Vacunas, Suplementos

    @Column(
        nullable = false,
        length = 120
    )
    private String nombreComercial;

    @Column(
        nullable = false,
        length = 120
    )
    private String principioActivo;

    @Column(
        nullable = false,
        length = 80
    )
    private String presentacion; // Ej: Blíster 10 comp., Masticable 1 unid., Vial 1 dosis

    @Column(
        nullable = false,
        length = 50
    )
    private String especie; // Perro, Gato, Perro / Gato

    @Column(nullable = false)
    private Integer stockActual;

    @Column(nullable = false)
    private Integer precioUnitarioClp;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
