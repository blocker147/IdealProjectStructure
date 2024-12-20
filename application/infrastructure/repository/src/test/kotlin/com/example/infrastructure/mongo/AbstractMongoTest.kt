package com.example.infrastructure.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.Document
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractMongoTest {
    private lateinit var mongoTemplate: MongoTemplate
    protected lateinit var repositoryFactory: MongoRepositoryFactory

    companion object {
        private const val MONGO_URL = "mongodb://localhost:27017"

        private val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(MONGO_URL))
            .applyToClusterSettings { it.serverSelectionTimeout(1, TimeUnit.SECONDS) }
            .build()

        private var mongoClient: MongoClient? = null

        @BeforeAll
        @JvmStatic
        fun checkMongoConnection() {
            try {
                mongoClient = MongoClients.create(settings)
                mongoClient?.getDatabase("admin")?.runCommand(Document("ping", 1))
            } catch (e: MongoException) {
                throw IllegalStateException("Unable to connect to MongoDB at $MONGO_URL", e)
            }
        }
    }

    @BeforeEach
    fun createNewDatabaseBeforeEachTestMethod() {
        mongoClient?.let {
            val uniqueDatabaseNameForEveryTest = UUID.randomUUID().toString()
            mongoTemplate = MongoTemplate(it, uniqueDatabaseNameForEveryTest)
            repositoryFactory = MongoRepositoryFactory(mongoTemplate)
        }
    }
}