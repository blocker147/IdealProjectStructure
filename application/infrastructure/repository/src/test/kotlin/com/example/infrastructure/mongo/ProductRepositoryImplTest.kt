package com.example.infrastructure.mongo

import com.example.domain.products.ProductRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import com.example.domain.products.Product as ProductDomain

class ProductRepositoryTest : AbstractMongoTest() {
    private val clockFake = Clock.fixed(
        Instant.parse("1997-01-01T00:00:00Z"),
        Clock.systemUTC().zone
    )
    private lateinit var target: ProductRepository

    @BeforeEach
    fun setup() {
        target = ProductRepositoryImpl(
            repositoryFactory.getRepository(ProductDao::class.java),
            clockFake,
        )
    }

    @Test
    fun `when product is saved - return correct response`() {
        val expected = ProductDomain(title = "title", count = 1)
        val actual = target.save(ProductDomain(title = "title", count = 1))

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual.id shouldNotBe null
    }

    @Test
    fun `when attempting to find product by id - return correct response`() {
        val expected = ProductDomain(title = "title", count = 1)
        val savedProduct = target.save(ProductDomain(title = "title", count = 1))
        val actual = target.findById(savedProduct.id!!)

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual.id shouldBe savedProduct.id
    }
}