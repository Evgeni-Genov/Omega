package com.example.omega.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI OmegaOpenApi() {
        var info = new Info().title("test").version("1.0");
    return new OpenAPI().info(info);
    }
}
