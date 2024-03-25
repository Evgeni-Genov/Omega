package com.example.omega.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI OmegaOpenApi() {
        var info = new Info().title("OMEGA").version("1.0");
        return new OpenAPI().info(info).components(new Components()
                .addSecuritySchemes("BearerTokenAuthentication",
                        new SecurityScheme()
                                .name("Authentication for 'api' APIs")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
