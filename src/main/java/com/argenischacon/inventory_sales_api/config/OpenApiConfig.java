package com.argenischacon.inventory_sales_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(
                        new Info()
                                .title("InventorySalesAPI")
                                .version("1.0.0")
                                .description("A RESTful API for managing inventory and sales," +
                                        " featuring entities like Category, Product, Customer, Sale, and SaleDetail" +
                                        " with CRUD operations and business logic for e-commerce applications")
                                .contact(new Contact()
                                        .name("Argenis Chacon B.")
                                        .email("argenischaconb@gmail.com")
                                        .url("https://github.com/argenischacon"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ))
                .components(new Components()
                        .addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
