package cl.veterinariasanmarcos.mascota.mascota.dto;

import java.time.LocalDateTime;

import cl.veterinariasanmarcos.mascota.mascota.model.Especie;
import cl.veterinariasanmarcos.mascota.mascota.model.Sexo;

import lombok.Data;
@Data 
public class ListarMascotasDTO {

    private String nombre;
    private Especie especie;
    private String raza;
    private Integer edad;
    private Sexo sexo;
    private Double pesoKg;
    private Long clienteId;
    private Boolean activo = true;
    private LocalDateTime creadoEn = LocalDateTime.now();


}
