package cl.veterinariasanmarcos.detalleconsultas.integration;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FichaClient {

    private final RestClient restClient;

    public FichaClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.ficha.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public FichaConsultaResponse obtenerConsulta(Long consultaId) {
        try {
            FichaConsultaResponse response = restClient.get()
                    .uri("/api/consultas/{id}", consultaId)
                    .retrieve()
                    .body(FichaConsultaResponse.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Ficha devolvió una respuesta vacía");
            }
            return response;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new EntityNotFoundException("Consulta no encontrada con ID: " + consultaId);
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Ficha respondió con HTTP " + ex.getStatusCode().value(),
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No fue posible conectar con ficha", ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta inválida desde ficha", ex);
        }
    }
}
