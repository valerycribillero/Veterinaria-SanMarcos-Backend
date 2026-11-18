package cl.veterinariasanmarcos.mascota.mascota.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "mascotas")
@AllArgsConstructor 
@NoArgsConstructor
@Getter 
@Setter 
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La mascota debe tener un nombre")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Especie especie;          

    @Size(max = 60)
    private String raza;              

    @Min(0)
    @Column(nullable = false)
    private Integer edad;            

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sexo sexo;                

    private Double pesoKg;            

    @NotNull(message = "La mascota debe contar con el id de su dueño")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Boolean activo = true;  

    @Column(updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public void setId(Integer id2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }


}
