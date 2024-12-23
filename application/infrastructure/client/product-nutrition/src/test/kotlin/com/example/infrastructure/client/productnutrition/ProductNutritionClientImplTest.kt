package com.example.infrastructure.client.productnutrition

import com.example.domain.products.ProductNutrition as ProductNutritionDomain
import com.example.infrastructure.client.productnutrition.models.ProductNutrition as ProductNutritionClient
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class ProductNutritionClientImplTest : AbstractWireMockServerTest() {
    private val target = ProductNutritionClientImpl(
        ProductNutritionMapper(),
        ProductNutritionProperties("http://localhost:8080")
    )

    @Test
    fun `when request to product-nutrition is made - then return correct response`() {
        val productNutrition = ProductNutritionClient()
            .productId("1")
            .title("Banana")
            .proteins(10)
            .fats(5)
            .carbohydrates(20)
            .calories(100)

        stubFor(
            get(urlEqualTo("/product-nutrition/1"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(target.apiClient.objectMapper.writeValueAsString(productNutrition))
                )
        )

        val expected = ProductNutritionDomain(
            productId = "1",
            proteins = 10,
            fats = 5,
            carbohydrates = 20,
            calories = 100
        )
        val actual: ProductNutritionDomain = target.findById("1")

        actual shouldBe expected
    }
}