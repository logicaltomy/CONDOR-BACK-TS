package cl.condor.rutas_api.config;

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
                        .title("Rutas API")
                        .description("""
                    Esta API esta dedicada a administrar
                    las rutas que un usuario puede realizar, el rol
                    de admin puede crear, banear, modificar, y borrar 
                    rutas como le plazca con este microservicio.
                    """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CONDOR Team")
                                .email("soporte@condor.cl")));
    }

    @Bean
    public GroupedOpenApi calificacionesGroup() {
        return GroupedOpenApi.builder()
                .group("rutas-v1")
                .pathsToMatch("/api/v1/rutas/**")
                .build();
    }

}
