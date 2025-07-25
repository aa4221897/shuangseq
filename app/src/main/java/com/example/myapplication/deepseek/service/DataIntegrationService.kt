package com.example.lotteryprediction.deepseek.service

import com.example.lotteryprediction.deepseek.knowledge.KnowledgeUpdater

/**
 * 数据集成服务
 */
class DataIntegrationService(
    private val knowledgeUpdater: KnowledgeUpdater
) {
    
    suspend fun integrateDataWithDeepSeek() {
        // 1. 更新知识库数�?        knowledgeUpdater.refreshKnowledgeBase()
        
        // 2. 生成数据获取报告
        val report = """
            ## 数据集成报告 ##
            历史数据: ${KnowledgeBase.get("history_data")}
            走势分析: ${KnowledgeBase.get("trend_analysis")}
            最后更�? ${KnowledgeBase.get("last_update")}
            
            ## 核心指标 ##
            热号红球: ${KnowledgeBase.get("hot_numbers")}
            冷号红球: ${KnowledgeBase.get("cold_numbers")}
            区间分布: ${KnowledgeBase.get("zone_distribution")}
        """.trimIndent()
        
        // 3. 存入知识�?        KnowledgeBase.update("data_report", report)
    }
}
