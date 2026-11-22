package cl.veterinariasanmarcos.servicios;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Servicios veterinarios"))
@SpringBootApplication
public class ServiciosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiciosApplication.class, args);
    }
}
