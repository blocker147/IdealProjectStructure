package com.example.domain.products

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductServiceImplTest {
    private val productValidatorMock: ProductValidator = mockk()
    private val productNutritionClientMock: ProductNutritionClient = mockk()
    private val productRepositoryFake = ProductRepositoryFake

    private val target: ProductService = ProductServiceImpl(
        productValidatorMock,
        productRepositoryFake,
        productNutritionClientMock,
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
    fun `when product doesn't exist neither in DB nor in external service - then throw an exception`() {
        val productId = "-1"
        every { productNutritionClientMock.findById(productId) } throws RuntimeException("No such product with id: $productId. Neither in DB nor in external service.")

        val exception = shouldThrow<IllegalStateException> { target.getProductById(productId) }

        exception.message shouldBe "No such product with id: $productId. Neither in DB nor in external service."
    }

    @Test
    fun `when product doesn't exist in DB try to request and cache it and - then return product`() {
        val productId = "1"
        val product = ProductFactory.createProduct(id = productId)
        every { productNutritionClientMock.findById(productId) } returns product

        val actual = target.getProductById(productId)

        verify { productNutritionClientMock.findById(productId) }
        actual shouldBe product
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