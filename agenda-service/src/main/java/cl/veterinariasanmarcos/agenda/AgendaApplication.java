package cl.veterinariasanmarcos.agenda;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Agenda veterinaria"))
@SpringBootApplication
public class AgendaApplication {
    public static void main(String[] args) {
        SpringApplication.run(
            AgendaApplication.class,
            args
        );
    }
}
