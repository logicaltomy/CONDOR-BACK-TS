package cl.condor.logros_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class EstadoClient {

    private final WebClient webClient;

    public EstadoClient(@Value("${estado-service.url}") String estadoServiceURL){
        this.webClient = WebClient.builder()
                .baseUrl(estadoServiceURL)
                .build();
    }

    public Map<String, Object> getEstadosById(Integer id){
        return this.webClient.get()
                .uri("/{id}",id)
                .retrieve()
                .onStatus(
                        status ->status.is4xxClientError(),
                        response ->response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Estado no encontrado"))
                )
                .bodyToMono(Map.class)
                .block();
    }
}