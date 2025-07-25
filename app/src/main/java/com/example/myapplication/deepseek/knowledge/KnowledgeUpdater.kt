package com.example.lotteryprediction.deepseek.knowledge

import com.example.lotteryprediction.deepseek.analysis.TrendAnalysis
import com.example.lotteryprediction.deepseek.data.DataSource

/**
 * 知识库数据更新器
 */
class KnowledgeUpdater(
    private val dataSource: DataSource,
    private val trendAnalyzer: TrendAnalyzer
) {
    
    suspend fun refreshKnowledgeBase() {
        // 1. 获取最新数�?        val history = dataSource.getHistory()
        val trendData = dataSource.getTrendChart()
        
        // 2. 分析走势
        val analysis = trendAnalyzer.analyzeTrends(history)
        
        // 3. 更新知识�?        KnowledgeBase.apply {
            update("history_data", history)
            update("trend_data", trendData)
            update("trend_analysis", analysis)
            update("last_update", System.currentTimeMillis())
            
            // 核心知识更新
            update("hot_numbers", analysis.hotRedNumbers)
            update("cold_numbers", analysis.coldRedNumbers) 
            update("zone_distribution", analysis.zoneDistribution)
        }
    }
}
