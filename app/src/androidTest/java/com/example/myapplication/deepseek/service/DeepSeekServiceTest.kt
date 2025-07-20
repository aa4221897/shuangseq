package com.example.myapplication.deepseek.service

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.AdvancedPredictionEngine
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

@RunWith(AndroidJUnit4::class)
class DeepSeekServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: DeepSeekService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val predictionEngine = AdvancedPredictionEngine()
        service = DeepSeekService(predictionEngine)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should handle user query successfully`() = runBlocking {
        val testRecords = listOf(
            LotteryRecord(redNumbers = listOf(1, 2, 3, 4, 5, 6))
        )
        
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("""{"choices":[{"message":{"content":"response"}}]}"""))
        
        val response = service.handleUserQuery("test query", testRecords)
        assert(response == "操作执行完成: response")
    }

    @Test(expected = SSLHandshakeException::class)
    fun `should throw on SSL handshake failure`() = runBlocking {
        val testRecords = listOf(
            LotteryRecord(redNumbers = listOf(1, 2, 3, 4, 5, 6))
        )
        
        mockWebServer.enqueue(MockResponse()
            .setSocketPolicy(SocketPolicy.FAIL_HANDSHAKE))
        
        service.handleUserQuery("test query", testRecords)
    }
}