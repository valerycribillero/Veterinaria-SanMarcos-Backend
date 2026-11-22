package cl.veterinariasanmarcos.detalleconsultas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Detalle de consultas"))
@SpringBootApplication
public class DetalleConsultasApplication {
    public static void main(String[] args) {
        SpringApplication.run(DetalleConsultasApplication.class, args);
    }
}
