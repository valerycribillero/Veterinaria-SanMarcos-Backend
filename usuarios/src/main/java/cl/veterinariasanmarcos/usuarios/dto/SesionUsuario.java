package cl.veterinariasanmarcos.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SesionUsuario {

    private Integer id;
    private String nombre;
    private String correo;
    private String rol;
}
