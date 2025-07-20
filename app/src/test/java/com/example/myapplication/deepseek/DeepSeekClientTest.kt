package com.example.myapplication.deepseek

import com.example.myapplication.deepseek.api.DeepSeekApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

class DeepSeekClientTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: DeepSeekClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val config = DeepSeekConfig(
            endpoint = mockWebServer.url("/").toString(),
            apiKey = "test-api-key"
        )
        client = DeepSeekClient(config)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test(expected = SSLHandshakeException::class)
    fun `should handle SSL handshake failure`() = runBlocking {
        // 模拟SSL握手失败
        mockWebServer.enqueue(MockResponse()
            .setSocketPolicy(SocketPolicy.FAIL_HANDSHAKE)
            .setBodyDelay(100, TimeUnit.MILLISECONDS))
        
        client.chat("test message")
    }

    @Test
    fun `should retry on SSL handshake failure`() = runBlocking {
        // 第一次模拟失败，第二次成功
        mockWebServer.enqueue(MockResponse()
            .setSocketPolicy(SocketPolicy.FAIL_HANDSHAKE))
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"choices":[{"message":{"content":"response"}}]}"""))
        
        val response = client.chat("test message")
        assert(response == "response")
    }
}