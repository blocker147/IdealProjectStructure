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

    // todo: improve method signature to receive single object for filters instead of multiple nullable parameters
    fun findAllBy(minCount: Int?, maxCount: Int?, limit: Int, selfId: Long?): ProductPage
    fun update(productId: String, product: Product)
}