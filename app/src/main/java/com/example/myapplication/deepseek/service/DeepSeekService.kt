package com.example.myapplication.deepseek.service

import com.example.myapplication.deepseek.api.DeepSeekApi
import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.AdvancedPredictionEngine

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
        // 构建知识上下文
        val knowledge = buildKnowledgeContext(context)
        
        // 调用DeepSeek API
        try {
            retryCount = 0
            val startTime = System.currentTimeMillis()
            
            logger.d("DeepSeekService", "开始处理用户查询: $query")
            logger.logPerformance(
                "USER_QUERY_START",
                System.currentTimeMillis(),
                mapOf("query" to query)
            )
            
            val response = api.chatCompletion(
                ChatRequest(
                    messages = listOf(
                        Message("system", knowledge),
                        Message("user", query)
                    )
                )
            )
            
            val duration = System.currentTimeMillis() - startTime
            logger.logPerformance(
                "USER_QUERY_END",
                duration,
                mapOf(
                    "query" to query,
                    "result" to response.choices.first().message.content
                )
            )
            
            // 解析并执行操作
            val result = executeOperations(response.choices.first().message.content)
            logger.d("DeepSeekService", "查询处理完成: $result")
            return result
        } catch (e: Exception) {
            if (e is SSLHandshakeException && retryCount++ < maxRetries) {
                logger.e(
                    "DeepSeekService", 
                    "SSL握手失败(重试 $retryCount/$maxRetries)", 
                    e
                )
                Thread.sleep(1000L * retryCount)
                return handleUserQuery(query, context)
            }
            
            val error = when (e) {
                is HttpException -> DeepSeekException("API请求失败: ${e.code()}")
                is IOException -> DeepSeekException("网络错误: ${e.message}")
                else -> DeepSeekException("处理失败: ${e.message}")
            }
            
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
            数据规模: ${records.size}期
            最新开奖: ${records.last().redNumbers}
            预测模型: ${predictionEngine::class.simpleName}
            
            ## 可用操作 ##
            - 修改算法参数
            - 优化性能配置
            - 更新知识库
            - 执行预测分析
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