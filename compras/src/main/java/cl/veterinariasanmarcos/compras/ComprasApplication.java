package cl.veterinariasanmarcos.compras;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Compras e inventario"))
@SpringBootApplication
public class ComprasApplication {
    public static void main(String[] args) {
        SpringApplication.run(
            ComprasApplication.class,
            args
        );
    }
}
