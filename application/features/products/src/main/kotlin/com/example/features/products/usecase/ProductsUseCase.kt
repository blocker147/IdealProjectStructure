package com.example.features.products.usecase

import com.example.features.products.models.ProductPage
import com.example.features.products.models.Product

interface ProductsUseCase {
    fun createProduct(product: Product): Product
    fun getProductById(id: String): Product
    fun getProducts(minCount: Int?, maxCount: Int?, limit: Int, cursor: String?): ProductPage
}