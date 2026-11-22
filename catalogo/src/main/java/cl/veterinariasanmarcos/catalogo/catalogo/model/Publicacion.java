package cl.veterinariasanmarcos.catalogo.catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "publicacion")
@Data
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 200)
    private String titulo;

    @NotBlank @Size(max = 1000)
    private String resumen;

    @NotBlank @Size(max = 150)
    private String autor;

    @NotBlank @Size(max = 30)
    private String fecha;

    @NotBlank @Size(max = 100)
    private String categoria;

    @NotBlank @Size(max = 500)
    private String imagen;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String contenido;
}
