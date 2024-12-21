package com.example.infrastructure.client.productnutrition

import com.example.domain.products.Product as ProductDomain
import com.example.domain.products.ProductNutritionClient
import com.example.infrastructure.client.productnutrition.api.ProductNutritionApi
import com.example.infrastructure.client.productnutrition.invoker.ApiClient
import com.example.infrastructure.client.productnutrition.invoker.ApiResponseDecoder
import feign.form.FormEncoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger

class ProductNutritionClientImpl : ProductNutritionClient {

    override fun findById(id: String): ProductDomain {
        val apiClient = ApiClient()
        apiClient.basePath = "http://localhost:8081/product-nutrition"  // must be provided by configuration
        val feignClient = apiClient.feignBuilder
            .client(OkHttpClient()) // perhaps something else can be used
            .logger(Slf4jLogger())  // perhaps something else can be used
            .encoder(               // perhaps something else can be used
                FormEncoder(
                    JacksonEncoder(
                        apiClient.objectMapper  // Potentially objectMapper needs to be configured
                    )
                )
            )
            .decoder(               // perhaps something else can be used
                ApiResponseDecoder(
                    apiClient.objectMapper
                )
            )
//            .retryer()            // perhaps something else can be used
//            .errorDecoder()       // perhaps something else can be used
            .target(ProductNutritionApi::class.java, apiClient.basePath)

        val productNutrition = feignClient.getProductById("1")
        println(productNutrition)

        return ProductDomain(title = "hola", count = 1)
    }
}