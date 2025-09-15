package com.campick.server.common.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campick Secondhand Camping Car API")
                        .description("REST API documentation for Campick server")
                        .version("0.0.1")
                        .license(new License().name("MIT"))
                        .contact(new Contact().name("Campick Team"))
                );
    }
}
