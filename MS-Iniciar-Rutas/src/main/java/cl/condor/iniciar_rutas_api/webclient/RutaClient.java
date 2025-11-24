package cl.condor.iniciar_rutas_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RutaClient {

    private final WebClient webClient;

    // Inyecta la URL base del servicio de rutas desde application.properties
    public RutaClient(@Value("${ruta-service.url}") String rutaServiceURL) {
        this.webClient = WebClient.builder()
                .baseUrl(rutaServiceURL)
                .build();
    }

    public Map<String, Object> getRutaById(Integer id) {
        return this.webClient.get()
                // Construye la petición GET con el id como parte de la URI
                .uri("/{id}", id)

                // Envía la petición y prepara la respuesta
                .retrieve()

                // Si la respuesta es un error 4XX (por ejemplo 404: No encontrado),
                // lanza una excepción con un mensaje personalizado.
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Ruta no encontrada"))
                )

                // Convierte el JSON recibido en un Map<String, Object>
                .bodyToMono(Map.class)

                // Bloquea el flujo hasta obtener la respuesta y devuelve el resultado
                .block();
    }
}