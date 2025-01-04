package com.example.spring.configuration.swagger

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("api")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun customOpenAPI(): io.swagger.v3.oas.models.OpenAPI {
        return io.swagger.v3.oas.models.OpenAPI()
            .info(
                Info().title("Your API Title")
                    .description("API documentation for your application")
                    .version("v1.0")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("GitHub Repository")
                    .url("https://github.com/your-repository")
            )
    }
}
