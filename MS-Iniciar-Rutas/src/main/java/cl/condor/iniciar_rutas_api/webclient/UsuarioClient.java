package cl.condor.iniciar_rutas_api.webclient;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Component

public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceURL) {
        this.webClient = WebClient.builder()
                .baseUrl(usuarioServiceURL)
                .build();
    }

    public Map<String, Object> getUsuarioById(Integer id) {
        // Inicia una solicitud HTTP GET al endpoint "/{id}" del servicio de usuarios
        // El {id} en la URI se reemplaza dinámicamente con el valor de la variable "id".
        return this.webClient.get()
                .uri("/{id}", id)
                // Envía la solicitud y prepara la respuesta
                .retrieve()

                // Maneja el caso en que el servicio externo devuelve un error 4xx (ej: 404 Not Found).
                // Si pasa, transformamos la respuesta en un RuntimeException con un mensaje personalizado.
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("id de Usuario no encontrada"))
                )

                // Convierte el cuerpo de la respuesta (JSON) en un objeto Java de tipo Map<String, Object>.
                // Es decir, convierte el JSON a un mapa clave:valor.
                .bodyToMono(Map.class)
                // Hace que la llamada sea sincrónica (bloquea hasta que llega la respuesta).
                // Sin este "block()", el método devolvería un "Mono" (programación reactiva).
                .block();
    }
}