package com.example.infrastructure.client.productnutrition

import com.example.domain.products.ProductNutrition as ProductNutritionDomain
import com.example.domain.products.ProductNutritionClient
import com.example.infrastructure.client.productnutrition.api.ProductNutritionApi
import com.example.infrastructure.client.productnutrition.exceptions.DownstreamException
import com.example.infrastructure.client.productnutrition.invoker.ApiClient
import com.example.infrastructure.client.productnutrition.invoker.ApiResponseDecoder
import feign.form.FormEncoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger

class ProductNutritionClientImpl(
    private val productNutritionMapper: ProductNutritionMapper,
    private val productNutritionProperties: ProductNutritionProperties,
) : ProductNutritionClient {
    val apiClient = ApiClient()

    override fun findById(id: String): ProductNutritionDomain {
        apiClient.basePath = productNutritionProperties.basePath
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

        try {
            val productNutrition = feignClient.getProductById("1")
            return productNutritionMapper.map(productNutrition)
        } catch (e: Exception) {
            throw DownstreamException("Exception from product-nutrition service", e)
        }
    }
}