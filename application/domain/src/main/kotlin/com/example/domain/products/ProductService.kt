package com.example.domain.products

interface ProductService {
    fun createProduct(product: Product): Product
    fun getProductById(id: String): Product
    fun decreaseProductCount(productId: String, count: Int)
}