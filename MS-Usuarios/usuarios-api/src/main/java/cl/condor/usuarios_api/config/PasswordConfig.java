package cl.condor.usuarios_api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


// marca la clase como de tipo configuracion
@Configuration
public class PasswordConfig {
    // define un bean para que spring lo maneje
    @Bean
    // función de tipo bean para manejarla en cualquier lugar
    // que retorne un codificador de contraseñas con BCrypt (coste 12)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
