package com.vlad.reservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Reservation API")
                        .version("1.0.0")
                        .description("Professional REST API for reservation management, built with Spring Boot 3 and Java 21.")
                        .contact(new Contact()
                                .name("Vlad Dregan")
                                .email("vladdregan2004a@gmail.com")));
    }
}