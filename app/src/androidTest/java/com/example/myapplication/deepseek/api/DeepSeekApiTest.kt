package com.example.myapplication.deepseek.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.deepseek.util.AndroidLogger
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeepSeekApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: DeepSeekApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        // 使用MockWebServer的URL作为测试端点
        api = object : DeepSeekApi {
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
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testChatCompletion() = runBlocking {
        val testResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "role": "assistant",
                            "content": "Hello, how can I help you?"
                        }
                    }
                ]
            }
        """.trimIndent()
        
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(testResponse))
        
        val request = ChatRequest(
            messages = listOf(Message("user", "Hello"))
        )
        
        val response = api.chatCompletion(request)
        assert(response.choices[0].message.content == "Hello, how can I help you?")
    }

    @Test(expected = Exception::class)
    fun testFailedRequest() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500))
        
        val request = ChatRequest(
            messages = listOf(Message("user", "Hello"))
        )
        
        api.chatCompletion(request)
    }
}