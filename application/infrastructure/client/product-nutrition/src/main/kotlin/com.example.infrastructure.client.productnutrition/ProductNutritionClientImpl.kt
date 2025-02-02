package com.example.infrastructure.client.productnutrition

import com.example.domain.products.ProductNutritionClient
import com.example.infrastructure.client.productnutrition.v1.api.ProductNutritionApi
import com.example.infrastructure.client.productnutrition.v1.models.ProductNutritionV2 as ProductNutritionClientModel
import org.mapstruct.Mapper
import org.springframework.web.client.RestClient
import com.example.domain.products.ProductNutrition as ProductNutritionDomain

class ProductNutritionClientImpl(
    private val productNutritionProperties: ProductNutritionProperties,
    private val productNutritionMapper: ProductNutritionMapper
) : ProductNutritionClient {
    override fun findById(id: String): ProductNutritionDomain {
        val restClient = RestClient
            .builder()
            .baseUrl(productNutritionProperties.basePath)
            .build()
        val productNutritionApi = ProductNutritionApi(restClient)
        val productNutritionByIdV2 = productNutritionApi.getProductNutritionByIdV2("b")
        val mapFromClientModelToDomain = productNutritionMapper.mapFromClientModelToDomain(productNutritionByIdV2)
        return mapFromClientModelToDomain
    }
}

@Mapper(componentModel = "spring")
interface ProductNutritionMapper {
    fun mapFromClientModelToDomain(clientModel: ProductNutritionClientModel): ProductNutritionDomain
}