package com.example.domain.products

interface ProductNutritionClient {
    fun findById(id: String): Product
}