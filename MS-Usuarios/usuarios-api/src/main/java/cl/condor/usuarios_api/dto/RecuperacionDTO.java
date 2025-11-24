package cl.condor.usuarios_api.dto;
import lombok.Data;

@Data
public class RecuperacionDTO {
    private String correo;
    // Respuestas que escribe el usuario
    private String respuestaSeguridad1; 
    private String respuestaSeguridad2;
    private String nuevaPassword;
}
