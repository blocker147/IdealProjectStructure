package com.example.infrastructure.client.productnutrition

import com.example.domain.products.Product
import com.example.domain.products.Product as ProductDomain
import com.example.domain.products.ProductNutritionClient
import com.example.infrastructure.client.productnutrition.api.ProductNutritionApi
import com.example.infrastructure.client.productnutrition.invoker.ApiClient

class ProductNutritionClientImpl(
) : ProductNutritionClient {
    override fun findById(id: String): Product {
        val apiClient = ApiClient();
        apiClient.basePath = "http://localhost:8081/product-nutrition"
        val client = apiClient.buildClient(ProductNutritionApi::class.java)

        val productNutrition = client.getProductById("1")
        println(productNutrition)

        return ProductDomain(title = "", count = 1)
    }
}