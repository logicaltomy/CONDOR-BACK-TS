package cl.condor.usuarios_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Condor · API Usuarios")
                        .version("v1.0.0")
                        .description("""
                    API del microservicio de Usuarios para el proyecto Cóndor.
                    Endpoints para registro, consulta y mantenimiento de usuarios.
                    Las contraseñas se almacenan con hash (BCrypt).
                    """)
                        .contact(new Contact()
                                .name("Equipo Cóndor")
                                .email("equipo.condor@example.com")));
    }
}
