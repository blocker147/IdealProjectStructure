package com.example.infrastructure.client.productnutrition

import com.example.infrastructure.client.productnutrition.models.ProductNutrition as ProductNutritionClient
import com.example.domain.products.ProductNutrition as ProductNutritionDomain
import org.springframework.stereotype.Component

@Component
class ProductNutritionMapper {
    fun map(productNutrition: ProductNutritionClient): ProductNutritionDomain {
        return ProductNutritionDomain(
            productId = productNutrition.productId,
            proteins = productNutrition.proteins,
            fats = productNutrition.fats,
            carbohydrates = productNutrition.carbohydrates,
            calories = productNutrition.calories,
        )
    }
}