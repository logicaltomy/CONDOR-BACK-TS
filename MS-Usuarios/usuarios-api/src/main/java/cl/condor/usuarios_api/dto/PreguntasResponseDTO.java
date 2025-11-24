package cl.condor.usuarios_api.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreguntasResponseDTO {
    private String correo;
    private String pregunta1;
    private String pregunta2;
}
