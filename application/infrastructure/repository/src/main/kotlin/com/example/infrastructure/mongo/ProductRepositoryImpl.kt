package com.example.infrastructure.mongo

import com.example.domain.products.Position
import com.example.domain.products.ProductPage
import com.example.domain.products.Product as ProductDomain
import com.example.infrastructure.mongo.Product as ProductEntity
import com.example.domain.products.ProductRepository
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import java.time.Clock

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val clock: Clock,
    private val mongoTemplate: MongoTemplate
) : ProductRepository {
    private fun getNextSequence(): Long {
        val query = Query(Criteria.where("_id").`is`("products"))
        val update = Update().inc("seq", 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(true)

        val counter = mongoTemplate.findAndModify(query, update, options, Counter::class.java)
        return counter?.seq ?: 1L
    }

    override fun save(product: ProductDomain): ProductDomain {
        val productEntity = mapToEntity(product)
        val savedProductEntity = productDao.save(productEntity)
        return mapToDomain(savedProductEntity)
    }

    override fun findById(id: String): ProductDomain {
        val existingEntity = findProductOrThrowException(id)
        return mapToDomain(existingEntity)
    }

    override fun findAllBy(minCount: Int?, maxCount: Int?, limit: Int, selfId: Long?): ProductPage {
        val sortingField = "generatedId"

        var filterCriteria = Criteria()
        minCount?.let { filterCriteria = filterCriteria.and("count").gte(it) }
        maxCount?.let { filterCriteria = filterCriteria.andOperator(Criteria("count").lte(it)) }

        val firstQuery = Query()
            .addCriteria(filterCriteria)
            .with(Sort.by(Sort.Direction.ASC, sortingField))
            .limit(1)
        val first = mongoTemplate.findOne(firstQuery, ProductEntity::class.java) ?: throw RuntimeException("Collection is empty")

        val selfQuery = Query()
            .addCriteria(filterCriteria)
            .addCriteria(Criteria.where(sortingField).`is`(selfId))
        val self = selfId?.let { mongoTemplate.findOne(selfQuery, ProductEntity::class.java) } ?: first

        val prevQuery = Query()
            .addCriteria(filterCriteria)
            .addCriteria(Criteria.where(sortingField).lt(self.generatedId))
            .with(Sort.by(Sort.Direction.DESC, sortingField))
            .limit(limit)
            .skip(limit.toLong() - 1)
        val prev = mongoTemplate.findOne(prevQuery, ProductEntity::class.java)

        val nextQuery = Query()
            .addCriteria(filterCriteria)
            .addCriteria(Criteria.where(sortingField).gt(self.generatedId))
            .with(Sort.by(Sort.Direction.ASC, sortingField))
            .limit(limit)
            .skip(limit.toLong() - 1)
        val next = mongoTemplate.findOne(nextQuery, ProductEntity::class.java)

        val recordsLeftQuery = Query()
            .addCriteria(filterCriteria)
            .addCriteria(Criteria.where(sortingField).gt(self.generatedId))
            .with(Sort.by(Sort.Direction.ASC, sortingField))
        val recordsLeft = mongoTemplate.count(recordsLeftQuery, ProductEntity::class.java)
        val last = if (recordsLeft < limit) {
            null
        } else {
            val lastOffset = (recordsLeft - limit) % limit
            val lastQuery = Query()
                .addCriteria(filterCriteria)
                .with(Sort.by(Sort.Direction.DESC, sortingField))
                .skip(lastOffset)
            mongoTemplate.findOne(lastQuery, ProductEntity::class.java)
        }

        val dataQuery = Query()
            .addCriteria(filterCriteria)
            .addCriteria(Criteria.where(sortingField).gte(self.generatedId))
            .with(Sort.by(Sort.Direction.ASC, sortingField))
            .limit(limit)
        val data: List<ProductEntity> = mongoTemplate.find(dataQuery, ProductEntity::class.java)

        return ProductPage(
            position = Position(
                first = if (first == self) null else first.generatedId,
                prev = if (prev == self) null else prev?.generatedId,
                self = self.generatedId,
                next = if (next == self) null else next?.generatedId,
                last = if (last == self) null else last?.generatedId,
            ),
            products = data.map { mapToDomain(it) },
        )
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
        val generatedId = if (domain._id == -1L) getNextSequence() else domain._id

        return ProductEntity(
            id = domain.id,
            createdDate = instant,
            lastModifiedDate = instant,
            generatedId = generatedId,
            title = domain.title,
            count = domain.count,
        )
    }

    private fun mapToDomain(entity: ProductEntity): ProductDomain {
        return ProductDomain(
            id = entity.id,
            _id = entity.generatedId,
            title = entity.title,
            count = entity.count,
        )
    }
}
