package cl.condor.logros_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceURL){
        this.webClient = WebClient.builder()
                .baseUrl(usuarioServiceURL)
                .build();
    }

    public Map<String, Object> getUsuariosById(Integer id){
        return this.webClient.get()
                .uri("/{id}",id)
                .retrieve()
                .onStatus(
                        status ->status.is4xxClientError(),
                        response ->response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Usuario no encontrado"))
                )
                .bodyToMono(Map.class)
                .block();
    }
}
