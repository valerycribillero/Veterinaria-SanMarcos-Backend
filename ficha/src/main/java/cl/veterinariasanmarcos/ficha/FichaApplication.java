package cl.veterinariasanmarcos.ficha;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Ficha clínica"))
@SpringBootApplication
public class FichaApplication {
    public static void main(String[] args) {
        SpringApplication.run(
            FichaApplication.class,
            args
        );
    }
}
