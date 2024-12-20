package com.example.domain.products

object ProductFactory {
    fun createProduct(
        id: String? = null,
        title: String = "title",
        count: Int = 0
    ): Product {
        return Product(id = id, title = title, count = count)
    }
}