package org.example.com.domain.product

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProductRepository {
    private val products: MutableMap<String, Product> = hashMapOf()

    /** Saves product to database */
    fun save(product: Product): Product {
        val id = UUID.randomUUID().toString()
        val savedProduct = product.copy(id = id)
        products[id] = savedProduct
        return savedProduct
    }

    /** Attempts to find product by id */
    fun findById(id: String): Product {
        return products[id] ?: throw IndexOutOfBoundsException("No such product with id: $id")
    }

    /** Update product by id */
    fun updateProduct(productId: String, product: Product) {
        products[productId] = product
    }
}