package com.example.features.products.rest

import com.example.features.products.models.Product as ProductModel
import com.example.features.products.usecase.ProductsUseCase
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ProductsControllerTest {
    private val productsUseCase: ProductsUseCase = mockk()
    private val productsController: ProductsController = ProductsController(productsUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(productsController)
        .build()

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `when product is created - then return correct response`() {
        val mockProduct = ProductModel("Banana", 10, "1")
        every { productsUseCase.createProduct(any()) } returns mockProduct

        val result = mockMvc.perform(
            post("/products")
                .contentType("application/json")
                .content(
                    """
                    {
                        "title": "Banana",
                        "count": 10
                    }
                    """
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseBody = result.response.contentAsString

        responseBody shouldBe """
            {"title":"Banana","count":10,"id":"1"}
        """.trimIndent()
    }

    @Test
    fun `when product is requested by id - then return correct response`() {
        val mockProduct = ProductModel("Banana", 10, "1")
        every { productsUseCase.getProductById("1") } returns mockProduct

        val result = mockMvc.perform(get("/products/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseBody = result.response.contentAsString

        responseBody shouldBe """
            {"title":"Banana","count":10,"id":"1"}
        """.trimIndent()
    }
}