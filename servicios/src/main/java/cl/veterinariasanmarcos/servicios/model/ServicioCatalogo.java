package cl.veterinariasanmarcos.servicios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicios_catalogo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServicioCatalogo {

    @Id
    @Column(length = 10)
    private String codigo; 

    @Column(nullable = false, length = 50)
    private String categoria; 

    @Column(nullable = false, length = 120)
    private String nombreServicio;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(nullable = false, length = 25)
    private String duracionAprox; 

    @Column(nullable = false)
    private Integer precioClp;

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}