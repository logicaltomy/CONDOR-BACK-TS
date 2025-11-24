package cl.condor.iniciar_rutas_api.config;

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
                        .title("Iniciar Rutas API")
                        .description("""
                    Microservicio que sirve para iniciar un
                    trayecto de ruta, es una api que maneja una 
                    entidad intermedia que conecta el usuario 
                    con la ruta que realizo.
                    """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CONDOR Team")
                                .email("soporte@condor.cl")));
    }

    @Bean
    public GroupedOpenApi calificacionesGroup() {
        return GroupedOpenApi.builder()
                .group("iniciar-rutas-v1")
                .pathsToMatch("/api/v1/abrir-ruta/**")
                .build();
    }

}