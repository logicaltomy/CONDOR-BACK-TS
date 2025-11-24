package cl.condor.usuarios_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la petición de Login (POST).
 * Se utiliza para mapear el JSON de entrada (ID y password).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    // Campo para identificar al usuario
    private String correo;

    // Contraseña en texto plano
    private String password;
}