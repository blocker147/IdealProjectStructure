package com.example.domain.products

data class ProductNutrition(
    val productId: String,
    val proteins: Int,
    val fats: Int,
    val carbohydrates: Int,
    val calories: Int,
)
