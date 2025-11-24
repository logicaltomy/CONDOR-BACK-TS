package cl.condor.contacto_api.config;

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
                        .title("Contacto API")
                        .description("""
                    Microservicio de Contacto.
                    Permite postear los mensajes que los usuarios quieran
                    dejar al equipo de trabajo, y permite que un usuario con el
                    rol adecuado pueda revisarlos.
                    """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CONDOR Team")
                                .email("soporte@condor.cl")));
    }

    @Bean
    public GroupedOpenApi calificacionesGroup() {
        return GroupedOpenApi.builder()
                .group("contacto-v1")
                .pathsToMatch("/api/v1/contacto/**")
                .build();
    }

}
