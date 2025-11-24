package cl.condor.calificaciones_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Calificaciones API")
                        .description("""
                    Microservicio de Calificaciones.
                    Permite crear, consultar por ID y listar calificaciones.
                    """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CONDOR Team")
                                .email("soporte@condor.cl")));
    }

    @Bean
    public GroupedOpenApi calificacionesGroup() {
        return GroupedOpenApi.builder()
                .group("calificaciones-v1")
                .pathsToMatch("/api/v1/calificaciones/**")
                .build();
    }
}
