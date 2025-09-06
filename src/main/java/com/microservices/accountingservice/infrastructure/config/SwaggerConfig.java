package com.microservices.accountingservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Accounting Service API")
                        .version("1.0.0")
                        .description("API para gestión de cuentas y movimientos contables")
                        .contact(new Contact()
                                .name("Microservices Team")
                                .email("team@microservices.com")));
    }
}
