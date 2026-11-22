package cl.veterinariasanmarcos.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MiembroEquipo {

    private Integer id;
    private String nombre;
    private String especialidad;
    private String experiencia;
    private String bio;
    private String imagen;
}
