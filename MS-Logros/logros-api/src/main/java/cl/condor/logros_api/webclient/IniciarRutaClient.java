package cl.condor.logros_api.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class IniciarRutaClient {
    private final WebClient webClient;

    public IniciarRutaClient(@Value("${iniciarRuta-service.url}") String iniciarRutaServiceURL){
        this.webClient = WebClient.builder()
                .baseUrl(iniciarRutaServiceURL)
                .build();
    }

    public List<Map<String, Object>> getRutasByUsuario(Integer idUsuario) {
        return this.webClient.get()
                .uri("/usuario/{id}", idUsuario) // <--- apunta al endpoint que hiciste
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("No se encontraron rutas para el usuario con id " + idUsuario))
                )
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }

}
