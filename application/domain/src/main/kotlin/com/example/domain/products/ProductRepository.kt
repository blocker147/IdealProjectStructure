package com.example.domain.products

interface ProductRepository {
    fun save(product: Product): Product

    /**
     * Finds a product by its ID.
     *
     * @param id the ID of the product to find
     * @return the found product in domain model
     * @throws RuntimeException if the product with the given ID is not found
     */
    fun findById(id: String): Product
    fun update(productId: String, product: Product)
}