package com.example.lotteryprediction.network

import com.example.lotteryprediction.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface DeepSeekService {
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") token: String,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>

    companion object {
        private const val BASE_URL = "https://api.deepseek.com/"
        private const val TIMEOUT_SECONDS = 30L
        private const val MAX_RETRY = 3

        fun create(): DeepSeekService {
            val client = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor { chain ->
                    var request = chain.request()
                    var response: okhttp3.Response? = null
                    var retryCount = 0
                    var lastException: Exception? = null

                    while (retryCount < MAX_RETRY) {
                        try {
                            response = chain.proceed(request)
                            if (response.isSuccessful) {
                                return@addInterceptor response
                            }
                        } catch (e: Exception) {
                            lastException = e
                        }
                        retryCount++
                    }

                    response?.close()
                    throw lastException ?: RuntimeException("API请求失败")
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DeepSeekService::class.java)
        }
    }
}

data class ChatCompletionRequest(
    val model: String = "deepseek-chat",
    val messages: List<ChatMessage>,
    val temperature: Float? = null,
    val max_tokens: Int? = null,
    val stream: Boolean = false
)

data class ChatMessage(
    val role: String, // "system", "user" or "assistant"
    val content: String
)

data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage,
    val created: Long,
    val model: String
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String?
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
