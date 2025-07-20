package com.example.myapplication.deepseek

import android.content.Context
import com.example.myapplication.deepseek.api.DeepSeekApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

class DeepSeekClient(private val config: DeepSeekConfig) {
    private var retryCount = 0
    private val maxRetries = 3
    private val api: DeepSeekApi by lazy {
        DeepSeekApi.create().also { api ->
            // 确保API端点与配置一致
            (api as? retrofit2.Retrofit)?.baseUrl()?.let { baseUrl ->
                if (baseUrl.toString() != config.endpoint) {
                    throw IllegalStateException("API endpoint mismatch: ${baseUrl} vs ${config.endpoint}")
                }
            }
        }
    }

    suspend fun chat(message: String): String {
        return withContext(Dispatchers.IO) {
            try {
                retryCount = 0
                val response = api.chatCompletion(
                    auth = "Bearer ${config.apiKey}",
                    request = DeepSeekApi.ChatRequest(
                        messages = listOf(
                            DeepSeekApi.ChatMessage(
                                role = "user",
                                content = message
                            )
                        )
                    )
                )
                response.choices.first().message.content
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    suspend fun optimizePrediction(
        algorithm: String,
        parameters: Map<String, Any>,
        hardwareConstraints: Map<String, Int>
    ): Pair<String, Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                retryCount = 0
                val response = api.optimizePrediction(
                    auth = "Bearer ${config.apiKey}",
                    request = DeepSeekApi.OptimizationRequest(
                        algorithm = algorithm,
                        parameters = parameters,
                        hardware_constraints = DeepSeekApi.HardwareConstraints(
                            max_memory = hardwareConstraints["memory"] ?: 1024,
                            max_cpu = hardwareConstraints["cpu"] ?: 1
                        )
                    )
                )
                response.optimized_algorithm to response.parameters
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    suspend fun analyzeData(
        dataType: String,
        timeRange: Pair<String, String>,
        metrics: List<String>
    ): DeepSeekApi.AnalysisResponse {
        return withContext(Dispatchers.IO) {
            try {
                retryCount = 0
                api.analyzeData(
                    auth = "Bearer ${config.apiKey}",
                    request = DeepSeekApi.AnalysisRequest(
                        data_type = dataType,
                        time_range = DeepSeekApi.TimeRange(
                            start = timeRange.first,
                            end = timeRange.second
                        ),
                        metrics = metrics
                    )
                )
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    private fun <T> handleApiError(e: Exception): T {
        when (e) {
            is HttpException -> {
                when (e.code()) {
                    401 -> throw DeepSeekException("认证失败，请检查API密钥")
                    403 -> throw DeepSeekException("无权访问该资源")
                    404 -> throw DeepSeekException("请求的资源不存在")
                    429 -> {
                        if (retryCount++ < maxRetries) {
                            Thread.sleep(1000L * retryCount)
                            throw RetryableException("请求过于频繁，正在重试($retryCount/$maxRetries)")
                        }
                        throw DeepSeekException("请求过于频繁，请稍后再试")
                    }
                    500 -> throw DeepSeekException("服务器内部错误")
                    else -> throw DeepSeekException("HTTP错误: ${e.code()}")
                }
            }
            is SocketTimeoutException -> throw DeepSeekException("请求超时，请检查网络连接")
            is SSLHandshakeException -> {
                if (retryCount++ < maxRetries) {
                    Thread.sleep(1000L * retryCount)
                    throw RetryableException("SSL握手失败，正在重试($retryCount/$maxRetries)")
                }
                throw DeepSeekException("SSL握手失败，请检查网络设置或联系管理员")
            }
            is IOException -> throw DeepSeekException("网络错误: ${e.message}")
            else -> throw DeepSeekException("未知错误: ${e.message}")
        }
    }
}

class DeepSeekException(message: String) : Exception(message)
class RetryableException(message: String) : Exception(message)
