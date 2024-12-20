package com.example.domain.products

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductServiceImplTest {
    private val productValidatorMock: ProductValidator = mockk()
    private val productRepositoryFake = ProductRepositoryFake

    private val target: ProductService = ProductServiceImpl(
        productValidatorMock,
        productRepositoryFake
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        productRepositoryFake.clear()
        every { productValidatorMock.validate(any()) } just Runs
    }

    @Test
    fun `when product is created - then return saved product`() {
        val product = ProductFactory.createProduct("1")

        val actual = target.createProduct(product)

        actual shouldBe product
    }

    @Test
    fun `when product doesn't exist - then throw an exception`() {
        val exception = shouldThrow<IndexOutOfBoundsException> { target.getProductById("-1") }

        exception.message shouldBe "No such product with id: -1"
    }

    @Test
    fun `when product is retrieved - then return product`() {
        val productId = "1"
        val product = ProductFactory.createProduct(id = productId)
        productRepositoryFake.save(product)

        val actual = target.getProductById(productId)

        actual shouldBe product
    }

    @Test
    fun `when product count is decreased - then update product count`() {
        val productId = "1"
        val product = ProductFactory.createProduct(id = productId, count = 10)
        productRepositoryFake.save(product)

        target.decreaseProductCount(productId, 3)

        val actual = productRepositoryFake.findById(productId)
        actual.count shouldBe 7
    }
}