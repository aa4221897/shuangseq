package com.example.lotteryprediction.deepseek.api

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DeepSeekApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: DeepSeekApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        api = object : DeepSeekApi {
            override suspend fun chatCompletion(request: ChatRequest): ChatResponse {
                return ChatResponse(listOf(Choice(Message("assistant", "test response"))))
            }

            override suspend fun optimizeAlgorithm(request: OptimizeRequest): OptimizeResponse {
                return OptimizeResponse("optimized_code", 0.5)
            }
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testChatCompletion() = runBlocking {
        val response = api.chatCompletion(
            ChatRequest(
                messages = listOf(Message("user", "test message"))
            )
        )
        assertNotNull(response)
        assertEquals(1, response.choices.size)
    }

    @Test
    fun testOptimizeAlgorithm() = runBlocking {
        val response = api.optimizeAlgorithm(
            OptimizeRequest(
                algorithm = "test_algorithm",
                constraints = mapOf("memory" to "512MB")
            )
        )
        assertEquals(0.5, response.performance_gain)
    }
}
