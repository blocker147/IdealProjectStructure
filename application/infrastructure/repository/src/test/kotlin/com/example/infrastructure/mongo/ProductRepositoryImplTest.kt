package com.example.infrastructure.mongo

import com.example.domain.products.ProductFactory
import com.example.domain.products.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant

class ProductRepositoryImplTest : AbstractMongoTest() {
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
            mongoTemplate
        )
    }

    @Test
    fun `when product is saved without id - return correct response`() {
        val expected = ProductFactory.createProduct(title = "title", count = 1)
        val actual = target.save(expected)

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual._id shouldBe 1L
        actual.id shouldNotBe null
    }

    @Test
    fun `when product is saved with id - return correct response`() {
        val expected = ProductFactory.createProduct(_id = 5L, title = "title", count = 1)
        val actual = target.save(expected)

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual._id shouldBe expected._id
        actual.id shouldNotBe null
    }

    @Test
    fun `when many products are saved - return correct response`() {
        val expected = ProductFactory.createProduct(_id = -1L, title = "title", count = 1)
        var actual = target.save(expected)

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual._id shouldBe 1L
        actual.id shouldNotBe null

        actual = target.save(expected)

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual._id shouldBe 2L
        actual.id shouldNotBe null
    }

    @Test
    fun `when attempting to receive product page in empty collection - throw exception`() {
        val exception = shouldThrow<RuntimeException> {
            target.findAllBy(null, null, 3, null)
        }

        exception.message shouldBe "Collection is empty"
    }

    @Test
    fun `when attempting to receive product page without self id - return correct response`() {
        val count = 74
        val limit = 25
        (1..count).forEach { _ -> target.save(ProductFactory.createProduct()) }

        val actual = target.findAllBy(null, null, limit, null)

        actual.position.first shouldBe null
        actual.position.prev shouldBe null
        actual.position.self shouldBe 1
        actual.position.next shouldBe (1 + limit)
        actual.position.last shouldBe (count - count % limit + 1)

        actual.products.size shouldBe limit
    }

    @Test
    fun `when attempting to receive product page with self id - return correct response`() {
        val count = 74
        val limit = 25
        (1..count).forEach { _ -> target.save(ProductFactory.createProduct()) }

        val actual = target.findAllBy(null, null, limit, 1)

        actual.position.first shouldBe null
        actual.position.prev shouldBe null
        actual.position.self shouldBe 1
        actual.position.next shouldBe (1 + limit)
        actual.position.last shouldBe (count - count % limit + 1)

        actual.products.size shouldBe limit
    }

    @Test
    fun `when attempting to receive product page with filters - return correct response`() {
        val count = 74
        val limit = 5
        (1..count).forEach { target.save(ProductFactory.createProduct(count = it)) }

        var actual = target.findAllBy(5, 25, limit, null)

        actual.position.first shouldBe null
        actual.position.prev shouldBe null
        actual.position.self shouldBe 5
        actual.position.next shouldBe 10
        actual.position.last shouldBe 25

        actual.products.size shouldBe 5

        actual = target.findAllBy(5, 25, limit, actual.position.last)

        actual.position.first shouldBe 5
        actual.position.prev shouldBe 20
        actual.position.self shouldBe 25
        actual.position.next shouldBe null
        actual.position.last shouldBe null

        actual.products.size shouldBe 1
    }

    @Test
    fun `when attempting to receive product page multiple times - return correct response`() {
        val count = 74
        val limit = 25
        (1..count).forEach { _ -> target.save(ProductFactory.createProduct()) }

        var actual = target.findAllBy(null, null, limit, 1)

        actual.position.first shouldBe null
        actual.position.prev shouldBe null
        actual.position.self shouldBe 1
        actual.position.next shouldBe 26
        actual.position.last shouldBe 51

        actual.products.size shouldBe 25

        actual = target.findAllBy(null, null, limit, 26)

        actual.position.first shouldBe 1
        actual.position.prev shouldBe 1
        actual.position.self shouldBe 26
        actual.position.next shouldBe 51
        actual.position.last shouldBe 51

        actual.products.size shouldBe 25

        actual = target.findAllBy(null, null, limit, 51)

        actual.position.first shouldBe 1
        actual.position.prev shouldBe 26
        actual.position.self shouldBe 51
        actual.position.next shouldBe null
        actual.position.last shouldBe null

        actual.products.size shouldBe 24
    }

    @Test
    fun `when attempting to find product by id - return correct response`() {
        val expected = ProductFactory.createProduct(_id = 5L, title = "title", count = 1)
        val savedProduct = target.save(expected)
        val actual = target.findById(savedProduct.id!!)

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
        actual._id shouldBe expected._id
        actual.id shouldBe savedProduct.id
    }
}