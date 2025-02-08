package com.example.domain.products

object ProductRepositoryFake : ProductRepository {
    private val products: MutableMap<String, Product> = hashMapOf()
    private var id = 0

    override fun save(product: Product): Product {
        val id = product.id ?: generateId()
        val savedProduct = product.copy(id = id)
        products[id] = savedProduct
        return savedProduct
    }

    override fun findById(id: String): Product {
        return products[id] ?: throw IndexOutOfBoundsException("No such product with id: $id")
    }

    override fun findAllBy(minCount: Int?, maxCount: Int?, limit: Int, selfId: Long?): ProductPage {
        TODO("Not yet implemented")
    }

    override fun update(productId: String, product: Product) {
        products[productId] = product
    }

    fun clear() {
        id = 0
        products.clear()
    }

    private fun generateId(): String {
        id++
        return id.toString()
    }
}