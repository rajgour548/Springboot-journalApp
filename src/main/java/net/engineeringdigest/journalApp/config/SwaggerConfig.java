package net.engineeringdigest.journalApp.config;




import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080/swagger-ui/index.html").description("Local Server"),
                        new Server().url("https://springboot-journalapp-production.up.railway.app/swagger-ui/index.html").description("Production Server")))
                .info(new Info()
                        .title("Journal API")
                        .version("1.0.0")
                        .description("API documentation for JournalApp ")
                        .contact(new Contact()
                                .name("Raj Gour")
                                .email("gourraj548@gmail.com")
                        )


                );
    }
}

