package com.example.domain.products

interface ProductService {
    fun createProduct(product: Product): Product
    fun getProductById(id: String): Product
    fun getProducts(minCount: Int?, maxCount: Int?, limit: Int, currentProductId: Long?): ProductPage
    fun decreaseProductCount(productId: String, count: Int)
}