package com.example.features.products.usecase

import com.example.features.products.models.Product as ProductModel

interface ProductsUseCase {
    fun createProduct(product: ProductModel): ProductModel
    fun getProductById(id: String): ProductModel
}