package com.example.domain.products

class ProductServiceImpl(
    private val productValidator: ProductValidator,
    private val productRepository: ProductRepository
): ProductService {
    /** Attempts to create a product */
    override fun createProduct(product: Product): Product {
        productValidator.validate(product)
        return productRepository.save(product)
    }

    /** Get product */
    override fun getProductById(id: String): Product {
        return productRepository.findById(id)
    }

    /** Attempts to decrease product count */
    override fun decreaseProductCount(productId: String, count: Int) {
        val product = productRepository.findById(productId)
        val newProduct = product.copy(count = product.count - count)
        productValidator.validate(newProduct)
        productRepository.update(productId, newProduct)
    }
}