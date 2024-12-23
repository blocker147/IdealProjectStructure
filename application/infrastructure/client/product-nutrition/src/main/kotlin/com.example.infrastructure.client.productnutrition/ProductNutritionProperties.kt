package com.example.infrastructure.client.productnutrition

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties
class ProductNutritionProperties(
    @Value("\${product-nutrition.base-path}") val basePath: String,
)
