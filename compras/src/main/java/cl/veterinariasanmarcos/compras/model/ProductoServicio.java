package cl.veterinariasanmarcos.compras.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "catalogo_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoServicio {

    @Id
    @Column(length = 15)
    private String codigo; // Ej: ME001, SV001, VA001

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 25
    )
    private TipoItem tipoItem;

    @Column(
        nullable = false,
        length = 60
    )
    private String categoria;

    @Column(
        nullable = false,
        length = 120
    )
    private String nombre;

    @Column(length = 120)
    private String principioActivo;

    @Column(length = 80)
    private String presentacion;

    @Column(length = 30)
    private String duracionAprox;

    @Column(
        nullable = false,
        length = 50
    )
    private String especie;

    private Integer stockActual;

    @Column(nullable = false)
    private Integer precioClp;

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
