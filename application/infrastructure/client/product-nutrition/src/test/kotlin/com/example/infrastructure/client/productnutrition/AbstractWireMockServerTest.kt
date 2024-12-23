package com.example.infrastructure.client.productnutrition

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractWireMockServerTest {

    companion object {
        private val wireMockServer: WireMockServer = WireMockServer()

        @BeforeAll
        @JvmStatic
        fun setup() {
            wireMockServer.start()
        }
    }
}