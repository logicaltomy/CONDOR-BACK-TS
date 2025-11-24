package cl.condor.rutas_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RegionClient {

    private final WebClient webClient;

    public RegionClient(@Value("${region-service.url}") String regionServiceURL){
        this.webClient = WebClient.builder()
                .baseUrl(regionServiceURL)
                .build();
    }

    public Map<String, Object> getRegionesById(Integer id){
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