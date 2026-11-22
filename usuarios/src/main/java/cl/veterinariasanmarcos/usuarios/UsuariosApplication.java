package cl.veterinariasanmarcos.usuarios;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Usuarios"))
@SpringBootApplication
public class UsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            UsuariosApplication.class,
            args
        );
    }
}
