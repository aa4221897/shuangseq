package com.example.lotteryprediction.deepseek.api

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * DeepSeek API接口定义
 */
interface DeepSeekApi {
    companion object {
        private const val BASE_URL = "https://api.deepseek.com/"

        fun create(context: Context): DeepSeekApi {
            // 创建信任所有证书的TrustManager
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // 配置SSLContext
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // 创建自定义日志拦截器
            val logger = com.example.lotteryprediction.deepseek.util.AndroidLogger()
            
            // 创建重试拦截�?            val retryInterceptor = { chain: okhttp3.Interceptor.Chain ->
                var currentRetry = 0
                val maxRetries = 3
                var response: okhttp3.Response? = null
                var lastException: Exception? = null
                
                while (currentRetry < maxRetries && response == null) {
                    try {
                        response = chain.proceed(chain.request())
                        if (!response.isSuccessful && currentRetry < maxRetries - 1) {
                            response.close()
                            response = null
                            Thread.sleep(1000L * (currentRetry + 1))
                        }
                    } catch (e: Exception) {
                        lastException = e
                        Thread.sleep(1000L * (currentRetry + 1))
                    }
                    currentRetry++
                }
                
                response ?: throw (lastException ?: IllegalStateException("Unknown error after $maxRetries retries"))
            }
            
            // 创建缓存目录和策�?(10MB缓存)
            val cacheDir = File(context.cacheDir, "http_cache")
            val cacheSize = 10L * 1024 * 1024 // 10MB
            val cache = Cache(cacheDir, cacheSize)
            
            // 创建OkHttpClient
            val client = OkHttpClient.Builder()
                .cache(cache)
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { hostname, _ -> 
                    val isValid = try {
                        val url = java.net.URL("https://$hostname")
                        com.example.lotteryprediction.deepseek.util.NetworkUtils.verifySSLCertificate(url.toString())
                    } catch (e: Exception) {
                        logger.logSslHandshake(
                            "https://$hostname", 
                            false, 
                            e.message
                        )
                        false
                    }
                    logger.logSslHandshake(
                        "https://$hostname", 
                        isValid, 
                        if (isValid) null else "SSL verification failed"
                    )
                    isValid
                }
                .addInterceptor { chain ->
                    val request = chain.request()
                    // 只在有网络时使用缓存
                    if (!com.example.lotteryprediction.deepseek.util.NetworkUtils.isNetworkConnected(context)) {
                        request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=${60 * 60 * 24 * 7}") // 1周离线缓�?                            .build()
                    } else {
                        request
                    }
                }
                .addNetworkInterceptor { chain ->
                    val response = chain.proceed(chain.request())
                    response.newBuilder()
                        .header("Cache-Control", "public, max-age=60") // 在线时缓�?分钟
                        .build()
                }
                .addInterceptor(retryInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request()
                    val startTime = System.currentTimeMillis()
                    
                    // 记录请求
                    logger.logNetworkRequest(
                        request.url.toString(),
                        request.method,
                        request.headers.toMap(),
                        request.body?.let { body ->
                            val buffer = okio.Buffer()
                            body.writeTo(buffer)
                            buffer.readUtf8()
                        }
                    )
                    
                    try {
                        val response = chain.proceed(request)
                        val duration = System.currentTimeMillis() - startTime
                        
                        // 记录响应
                        logger.logNetworkResponse(
                            response.request.url.toString(),
                            response.code,
                            response.headers.toMultimap(),
                            response.peekBody(1024).string(),
                            duration
                        )
                        
                        // 记录性能
                        logger.logPerformance(
                            "API_CALL_${request.method}",
                            duration,
                            mapOf(
                                "url" to request.url.toString(),
                                "status" to response.code,
                                "retry_count" to (currentRetry - 1)
                            )
                        )
                        
                        response
                    } catch (e: Exception) {
                        val duration = System.currentTimeMillis() - startTime
                        logger.e(
                            "API_CALL",
                            "Request failed: ${request.url}",
                            e
                        )
                        throw e
                    }
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory.create())
                .build()
                .create(DeepSeekApi::class.java)
        }
    }
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Body request: ChatRequest
    ): ChatResponse

    @POST("v1/optimize")
    suspend fun optimizeAlgorithm(
        @Body request: OptimizeRequest
    ): OptimizeResponse
}

data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val knowledge_base: String = "lottery_prediction"
)

data class Message(
    val role: String, // "user" or "assistant"
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class OptimizeRequest(
    val algorithm: String,
    val constraints: Map<String, Any>
)

data class OptimizeResponse(
    val optimized_algorithm: String,
    val performance_gain: Double
)
