package cl.condor.logros_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RutaClient {
    private final WebClient webClient;

    public RutaClient(@Value("${ruta-service.url}") String rutaServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(rutaServiceUrl)
                .build();
    }

    public Map<String, Object> getRutaById(Integer id){
        return this.webClient.get()
                .uri("/{id}",id)
                .retrieve()
                .onStatus(
                        status ->status.is4xxClientError(),
                        response ->response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Region no encontrada"))
                )
                .bodyToMono(Map.class)
                .block();
    }
}
