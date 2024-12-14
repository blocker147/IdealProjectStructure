package com.example.infrastructure.inmemory

import com.example.domain.products.Product
import com.example.domain.products.ProductRepository
import java.util.UUID

class InMemoryProductRepository : ProductRepository {
    private val products: MutableMap<String, Product> = hashMapOf()

    /** Saves product to database */
    override fun save(product: Product): Product {
        val id = UUID.randomUUID().toString()
        val savedProduct = product.copy(id = id)
        products[id] = savedProduct
        return savedProduct
    }

    /** Attempts to find product by id */
    override fun findById(id: String): Product {
        return products[id] ?: throw IndexOutOfBoundsException("No such product with id: $id")
    }

    /** Update product by id */
    override fun update(productId: String, product: Product) {
        products[productId] = product
    }
}