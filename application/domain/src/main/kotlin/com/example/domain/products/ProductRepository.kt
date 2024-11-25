package com.example.domain.products

interface ProductRepository {
    fun save(product: Product): Product
    fun findById(id: String): Product
    fun update(productId: String, product: Product)
}