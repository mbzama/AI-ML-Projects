package zama.learning.procureai.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;    @Bean
    public OpenAPI procureApiOpenAPI() {
        // Define security scheme for JWT (optional for endpoints that need it)
        SecurityScheme jwtSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"");

        return new OpenAPI()
                .info(new Info()
                        .title("Procure AI API")
                        .description("Comprehensive Procurement Management System with RFQ, Auctions, and Vendor Management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Procure AI Team")
                                .email("support@procure-ai.com")
                                .url("https://procure-ai.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.procure-ai.com")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", jwtSecurityScheme));
                // Removed global security requirement - individual endpoints can specify if needed
    }
}
