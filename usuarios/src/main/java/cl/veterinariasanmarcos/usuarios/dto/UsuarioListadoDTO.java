package cl.veterinariasanmarcos.usuarios.dto;

import lombok.Data;

@Data
public class UsuarioListadoDTO {

    private Integer id;
    private String nombre;
    private String email;
    private cl.veterinariasanmarcos.usuarios.model.Rol rol;
}
