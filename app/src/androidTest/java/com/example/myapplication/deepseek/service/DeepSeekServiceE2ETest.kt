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

@RunWith(AndroidJUnit4::class)
class DeepSeekServiceE2ETest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: DeepSeekService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        // 使用MockWebServer的URL作为测试端点
        val api = object : DeepSeekApi {
            companion object {
                fun create(baseUrl: String): DeepSeekApi {
                    return Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(OkHttpClient.Builder().build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(DeepSeekApi::class.java)
                }
            }
        }.create(mockWebServer.url("/").toString())
        
        service = DeepSeekService(
            predictionEngine = AdvancedPredictionEngine(),
            logger = AndroidLogger()
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testCompleteWorkflow() = runBlocking {
        // 模拟API响应
        val testResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "role": "assistant",
                            "content": "PREDICT 01,02,03,04,05,06"
                        }
                    }
                ]
            }
        """.trimIndent()
        
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(testResponse))
        
        // 准备测试数据
        val records = listOf(
            LotteryRecord(redNumbers = listOf(1, 2, 3, 4, 5, 6)),
            LotteryRecord(redNumbers = listOf(7, 8, 9, 10, 11, 12))
        )
        
        // 执行测试
        val result = service.handleUserQuery(
            query = "预测下一期号码",
            context = records
        )
        
        // 验证结果
        assert(result == "预测分析完成")
    }

    @Test(expected = DeepSeekException::class)
    fun testErrorHandling() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500))
        
        val records = listOf(
            LotteryRecord(redNumbers = listOf(1, 2, 3, 4, 5, 6))
        )
        
        service.handleUserQuery(
            query = "预测下一期号码",
            context = records
        )
    }
}
