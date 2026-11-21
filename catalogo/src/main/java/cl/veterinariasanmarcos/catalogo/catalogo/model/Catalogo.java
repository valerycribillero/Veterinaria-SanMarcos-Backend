package cl.veterinariasanmarcos.catalogo.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "producto")
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Debe ingresar un código")
    @Column(
        nullable = false,
        length = 20,
        unique = true
    )
    private String codigo;

    @NotBlank(message = "Debe ingresar una categoría")
    @Column(
        nullable = false,
        length = 50
    )
    private String categoria;

    @NotBlank(message = "Debe ingresar un nombre")
    @Column(
        nullable = false,
        length = 100
    )
    private String nombre;

    @Column(length = 100)
    private String principioActivo;

    @Column(length = 80)
    private String presentacion;

    @NotBlank(message = "Debe ingresar la especie")
    @Column(
        nullable = false,
        length = 30
    )
    private String especie;

    @NotNull(message = "Debe ingresar el stock")
    @Min(
        value = 0,
        message = "El stock no puede ser negativo"
    )
    @Column(nullable = false)
    private Integer stock;

    @NotNull(message = "Debe ingresar el precio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(nullable = false)
    private Integer precio;
}
