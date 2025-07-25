package com.example.lotteryprediction.deepseek.service

import com.example.lotteryprediction.deepseek.api.DeepSeekApi
import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.AdvancedPredictionEngine

/**
 * DeepSeek交互服务
 */
class DeepSeekService(
    private val predictionEngine: AdvancedPredictionEngine,
    private val logger: Logger = AndroidLogger()
) {
    private val api: DeepSeekApi by lazy { DeepSeekApi.create() }
    private var retryCount = 0
    private val maxRetries = 3
    
    suspend fun handleUserQuery(
        query: String,
        context: List<LotteryRecord>
    ): String {
        // 构建知识上下�?        val knowledge = buildKnowledgeContext(context)
        
        // 调用DeepSeek API
        try {
            retryCount = 0
            val startTime = System.currentTimeMillis()
            
            logger.d("DeepSeekService", "开始处理用户查�? $query")
            logger.logPerformance(
                "USER_QUERY_START",
                System.currentTimeMillis(),
                mapOf(
                    "query" to query,
                    "contextSize" to context.size
                )
            )
            
            val response = try {
                api.chatCompletion(
                    ChatRequest(
                        messages = listOf(
                            Message("system", knowledge),
                            Message("user", query)
                        )
                    )
                )
            } catch (e: Exception) {
                logger.e("DeepSeekService", "API调用失败", e)
                throw e
            }
            
            val duration = System.currentTimeMillis() - startTime
            logger.logPerformance(
                "USER_QUERY_END",
                duration,
                mapOf(
                    "query" to query,
                    "resultLength" to response.choices.first().message.content.length,
                    "status" to "success"
                )
            )
            
            // 解析并执行操�?            return try {
                val result = executeOperations(response.choices.first().message.content)
                logger.d("DeepSeekService", "查询处理完成: $result")
                result
            } catch (e: Exception) {
                logger.e("DeepSeekService", "操作执行失败", e)
                throw DeepSeekException("操作执行失败: ${e.message}")
            }
        } catch (e: Exception) {
            val error = when {
                e is SocketTimeoutException && retryCount++ < maxRetries -> {
                    val delay = 1000L * retryCount
                    logger.w(
                        "DeepSeekService",
                        "请求超时�?{delay}ms后重�?$retryCount/$maxRetries)"
                    )
                    Thread.sleep(delay)
                    return handleUserQuery(query, context)
                }
                e is HttpException && e.code() in 500..599 && retryCount++ < maxRetries -> {
                    val delay = 1000L * retryCount
                    logger.w(
                        "DeepSeekService",
                        "服务器错�?${e.code()})�?{delay}ms后重�?$retryCount/$maxRetries)"
                    )
                    Thread.sleep(delay)
                    return handleUserQuery(query, context)
                }
                e is HttpException -> {
                    val errorBody = try {
                        e.response()?.errorBody()?.string() ?: "无错误详�?
                    } catch (ex: Exception) {
                        "无法解析错误详情"
                    }
                    DeepSeekException("API请求失败(${e.code()}): $errorBody")
                }
                e is IOException -> DeepSeekException("网络错误: ${e.message}")
                e is IllegalStateException -> DeepSeekException("状态错�? ${e.message}")
                else -> DeepSeekException("处理失败: ${e.message}")
            }
            
            logger.logPerformance(
                "USER_QUERY_END",
                System.currentTimeMillis() - startTime,
                mapOf(
                    "query" to query,
                    "status" to "failed",
                    "error" to error.message
                )
            )
            
            logger.e(
                "DeepSeekService", 
                "处理用户查询失败: ${error.message}", 
                error
            )
            throw error
        }
    }
    
    private fun buildKnowledgeContext(records: List<LotteryRecord>): String {
        return """
            ## 双色球知识库 ##
            数据规模: ${records.size}�?            最新开�? ${records.last().redNumbers}
            预测模型: ${predictionEngine::class.simpleName}
            
            ## 可用操作 ##
            - 修改算法参数
            - 优化性能配置
            - 更新知识�?            - 执行预测分析
        """.trimIndent()
    }
    
    private fun executeOperations(command: String): String {
        return when {
            command.startsWith("OPTIMIZE") -> optimizeAlgorithm(command)
            command.startsWith("PREDICT") -> runPrediction(command)
            else -> "操作执行完成: $command"
        }
    }
    
    private fun optimizeAlgorithm(command: String): String {
        // 实现算法优化逻辑
        return "算法优化完成"
    }
    
    private fun runPrediction(command: String): String {
        // 实现预测执行逻辑
        return "预测分析完成"
    }
}
