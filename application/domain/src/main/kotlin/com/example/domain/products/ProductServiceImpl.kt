package com.example.domain.products

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class ProductServiceImpl(
    private val productValidator: ProductValidator,
    private val productRepository: ProductRepository,
    private val productNutritionClient: ProductNutritionClient,
) : ProductService {
    /** Attempts to create a product */
    override fun createProduct(product: Product): Product {
        productValidator.validate(product)
        return productRepository.save(product)
    }

    /** Get product */
    override fun getProductById(id: String): Product {
        return try {
            productRepository.findById(id)
        } catch (e: RuntimeException) {
            log.warn("Product with id: $id not found in database. Attempting to request it.", e)
            fetchAndCacheProduct(id)
        }
    }

    private fun fetchAndCacheProduct(id: String): Product {
        return try {
            val product = productNutritionClient.findById(id)
            log.info("Product with id: $id found in external service. Caching it.")
            productRepository.save(product)
        } catch (e: RuntimeException) {
            log.error("Product with id: $id cannot be requested.", e)
            throw IllegalStateException("No such product with id: $id. Neither in DB nor in external service.")
        }
    }

    /** Attempts to decrease product count */
    override fun decreaseProductCount(productId: String, count: Int) {
        val product = productRepository.findById(productId)
        val newProduct = product.copy(count = product.count - count)
        productValidator.validate(newProduct)
        productRepository.update(productId, newProduct)
    }
}