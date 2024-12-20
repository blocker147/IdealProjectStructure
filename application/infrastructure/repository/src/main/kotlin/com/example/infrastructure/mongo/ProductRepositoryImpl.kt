package com.example.infrastructure.mongo

import com.example.domain.products.Product as ProductDomain
import com.example.infrastructure.mongo.Product as ProductEntity
import com.example.domain.products.ProductRepository
import java.time.Clock

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val clock: Clock,
) : ProductRepository {
    override fun save(product: ProductDomain): ProductDomain {
        val productEntity = mapToEntity(product)
        val savedProductEntity = productDao.save(productEntity)
        return mapToDomain(savedProductEntity)
    }

    override fun findById(id: String): ProductDomain {
        val existingEntity = findProductOrThrowException(id)
        return mapToDomain(existingEntity)
    }

    override fun update(productId: String, product: ProductDomain) {
        val existingEntity = findProductOrThrowException(productId)
        val updatedEntity = updatedCopyOfEntity(existingEntity, product)
        productDao.save(updatedEntity)
    }

    private fun updatedCopyOfEntity(entity: ProductEntity, product: ProductDomain): ProductEntity {
        return entity.copy(
            title = product.title,
            count = product.count,
            lastModifiedDate = clock.instant(),
        )
    }

    private fun findProductOrThrowException(id: String): ProductEntity {
        return productDao.findById(id)
            .orElseThrow { RuntimeException("Product not found for update") }
    }

    private fun mapToEntity(domain: ProductDomain): ProductEntity {
        val instant = clock.instant()
        return ProductEntity(
            id = domain.id,
            createdDate = instant,
            lastModifiedDate = instant,
            title = domain.title,
            count = domain.count,
        )
    }

    private fun mapToDomain(entity: ProductEntity): ProductDomain {
        return ProductDomain(
            id = entity.id,
            title = entity.title,
            count = entity.count,
        )
    }
}
