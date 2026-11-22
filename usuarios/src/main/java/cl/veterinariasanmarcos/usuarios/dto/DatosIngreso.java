package cl.veterinariasanmarcos.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DatosIngreso {

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String password;
}
