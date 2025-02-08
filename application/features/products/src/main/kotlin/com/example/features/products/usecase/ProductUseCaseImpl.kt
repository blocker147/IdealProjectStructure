package com.example.features.products.usecase

import com.example.domain.products.ProductService
import com.example.features.products.models.ProductPage
import com.example.features.products.models.QueryParameter
import com.example.features.products.models.Cursor
import com.example.features.products.models.Position
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.util.Base64
import com.example.features.products.models.Product
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class ProductUseCaseImpl(
    private val productService: ProductService,
    private val productMapper: ProductMapper,
    private val objectMapper: ObjectMapper,
) : ProductsUseCase {
    override fun createProduct(product: Product): Product {
        val productDomain = productMapper.mapToDomain(product)
        val createdProductDomain = productService.createProduct(productDomain)
        return productMapper.mapToModel(createdProductDomain)
    }

    override fun getProductById(id: String): Product {
        val foundProductDomain = productService.getProductById(id)
        return productMapper.mapToModel(foundProductDomain)
    }

    override fun getProducts(minCount: Int?, maxCount: Int?, limit: Int, cursor: String?): ProductPage {
        val queryParameters = mutableListOf<QueryParameter>()
        minCount?.let { queryParameters.add(QueryParameter("min_count", it)) }
        maxCount?.let { queryParameters.add(QueryParameter("max_count", it)) }
        queryParameters.add(QueryParameter("limit", limit))
        val queryHash = createQueryHash(queryParameters)

        return if (cursor == null) {
            log.info { "No cursor provided, fetching first page" }
            log.info { "Query parameters: $queryParameters" }

            createProductPage(minCount, maxCount, limit, queryParameters, queryHash, null)
        } else {
            val cursorMetadata = decodeCursor(cursor)
            if (queryHash == cursorMetadata.queryHash) {
                val decodedQueryParameters: List<QueryParameter> = decodeQueryParameters(queryHash)
                log.info { "Cursor provided, fetching page" }
                log.info { "Query parameters: $decodedQueryParameters" }

                createProductPage(
                    decodedQueryParameters.find { it.key == "min_count" }?.let { it.value as Int },
                    decodedQueryParameters.find { it.key == "max_count" }?.let { it.value as Int },
                    decodedQueryParameters.find { it.key == "limit" }?.let { it.value as Int } ?: limit,
                    queryParameters,
                    queryHash,
                    cursorMetadata.value
                )
            } else {
                log.info { "Cursor provided, but query parameters do not match" }
                log.info { "Query parameters: $queryParameters" }

                createProductPage(minCount, maxCount, limit, queryParameters, queryHash, null)
            }
        }
    }

    private fun createProductPage(
        minCount: Int?,
        maxCount: Int?,
        limit: Int,
        queryParameters: List<QueryParameter>,
        queryHash: String,
        currentProductId: Long?
    ): ProductPage {
        val productPage = productService.getProducts(minCount, maxCount, limit, currentProductId)

        val first = productPage.position.first?.let { createCursorMetadataHash(it, queryHash) }?.createURI()
        val prev = productPage.position.prev?.let { createCursorMetadataHash(it, queryHash) }?.createURI()
        val self = productPage.position.self.let { createCursorMetadataHash(it, queryHash) }.createURI()
        val next = productPage.position.next?.let { createCursorMetadataHash(it, queryHash) }?.createURI()
        val last = productPage.position.last?.let { createCursorMetadataHash(it, queryHash) }?.createURI()

        val products = productPage.products.map { productMapper.mapToModel(it) }

        return ProductPage(
            Cursor(
                Position(first, prev, self, next, last),
                queryParameters,
            ),
            ProductPage.Limit.forValue(limit),
            products.size,
            products
        )
    }

    private fun decodeCursor(cursor: String): CursorMetadata {
        val cursorBytes = Base64.getDecoder().decode(cursor)
        return objectMapper.readValue(cursorBytes, CursorMetadata::class.java)
    }

    private fun decodeQueryParameters(queryHash: String): List<QueryParameter> {
        val queryBytes = Base64.getDecoder().decode(queryHash)
        return objectMapper.readValue(
            queryBytes,
            objectMapper.typeFactory.constructCollectionType(List::class.java, QueryParameter::class.java)
        )
    }

    private fun createQueryHash(queryParameters: List<QueryParameter>): String {
        return Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(queryParameters))
    }

    private fun createCursorMetadataHash(position: Long, queryHash: String): String {
        val cursorMetadata = CursorMetadata(position, queryHash)
        return Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(cursorMetadata))
    }

    private data class CursorMetadata(val value: Long?, val queryHash: String)

    private fun String.createURI(): URI = URI.create("/products?cursor=$this")
}