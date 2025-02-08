package com.example.domain.products

object ProductFactory {
    fun createProduct(
        id: String? = null,
        _id: Long = -1L,
        title: String = "title",
        count: Int = 0,
        nutrition: ProductNutrition? = null,
    ): Product {
        return Product(id = id, _id = _id, title = title, count = count, nutrition = nutrition)
    }

    fun createProductNutrition(
        productId: String = "1",
        proteins: Int = 0,
        fats: Int = 0,
        carbohydrates: Int = 0,
        calories: Int = 0,
    ): ProductNutrition {
        return ProductNutrition(
            productId = productId,
            proteins = proteins,
            fats = fats,
            carbohydrates = carbohydrates,
            calories = calories,
        )
    }
}