package com.kynsoft.gateway.infrastructure.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(apiInfo());
    }


   private Info apiInfo() {
        return new Info()
                .title("API Gateway")
                .description("API Documentation for the Quipux API Gateway")
                .version("1.0.0");
    }
}
