package com.example.products

class ProductService(
    private val productValidator: ProductValidator,
    private val productRepository: ProductRepository
) {
    /** Attempts to create a product */
    fun createProduct(product: Product): Product {
        productValidator.validate(product)
        return productRepository.save(product)
    }

    /** Get product */
    fun getProductById(id: String): Product {
        return productRepository.findById(id)
    }

    /** Attempts to decrease product count */
    fun decreaseProductCount(productId: String, count: Int) {
        val product = productRepository.findById(productId)
        val newProduct = product.copy(count = product.count - count)
        productValidator.validate(newProduct)
        productRepository.update(productId, newProduct)
    }
}