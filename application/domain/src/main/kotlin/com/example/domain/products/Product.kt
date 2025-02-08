package com.example.domain.products

data class Product(
    val id: String? = null,
    val _id: Long = -1,
    val title: String,
    val count: Int,
    val nutrition: ProductNutrition? = null,
)
