package cl.veterinariasanmarcos.usuarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "registro")
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Debe ingresar un nombre")
    @Column(
        nullable = false,
        length = 50
    )
    private String nombre;

    @Email(message = "Debe ser un correo válido")
    @NotBlank(message = "El correo no puede estar vacío")
    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Se debe tener una contraseña")
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 20
    )
    private Rol rol;

    @Column(length = 25)
    private String telefono;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 150)
    private String especialidad;

    @Column(length = 100)
    private String experiencia;

    @Column(length = 2000)
    private String bio;

    @Column(length = 500)
    private String imagen;

}
