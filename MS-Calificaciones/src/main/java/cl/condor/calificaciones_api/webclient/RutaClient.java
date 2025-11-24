package cl.condor.calificaciones_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RutaClient {

    private final WebClient webClient;

    public RutaClient(@Value("${ruta-service.url}") String rutaServiceURL) {
        this.webClient = WebClient.builder()
                .baseUrl(rutaServiceURL)
                .build();
    }

    public Map<String, Object> getRutaById(Integer id) {
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Ruta no encontrada"))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error en servicio Rutas"))
                )
                .bodyToMono(Map.class)
                .block();
    }
}
