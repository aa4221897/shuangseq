package com.example.lotteryprediction.network

import com.example.lotteryprediction.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class RetryPolicyTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var deepSeekService: DeepSeekService
    private val mockTokenManager = mock(TokenManager::class.java)

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        deepSeekService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeepSeekService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testRetryOnNetworkError() = runBlocking {
        // 模拟2次失败后成功
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        mockWebServer.enqueue(MockResponse().setResponseCode(502))
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        `when`(mockTokenManager.getToken()).thenReturn("valid_token")

        val response = deepSeekService.chatCompletion("valid_token", ChatCompletionRequest())
        assertTrue(response.isSuccessful)
    }

    @Test(expected = Exception::class)
    fun testMaxRetryReached() = runBlocking {
        // 模拟3次失�?
        repeat(3) {
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
        }

        `when`(mockTokenManager.getToken()).thenReturn("valid_token")

        deepSeekService.chatCompletion("valid_token", ChatCompletionRequest())
    }
}
