package com.example.myapplication.deepseek.service

import android.content.Context
import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.data.ResourceMapper
import com.example.myapplication.deepseek.knowledge.KnowledgeBase
import com.example.myapplication.deepseek.knowledge.ResourceKnowledge

/**
 * 数据可视化服务
 */
class DataVisualizationService(private val context: Context) {
    
    private val mapper = ResourceMapper(context)
    
    fun integrateDataWithResources(records: List<LotteryRecord>) {
        // 1. 更新资源知识
        ResourceKnowledge.updateResourceKnowledge(mapper)
        
        // 2. 构建关联数据集
        val dataset = buildDataset(records)
        
        // 3. 更新知识库
        KnowledgeBase.update("visual_dataset", dataset)
    }
    
    private fun buildDataset(records: List<LotteryRecord>): String {
        val trendDrawables = mapper.getTrendChartResources()
        
        return """
            ## 数据-资源关联报告 ##
            可用走势图资源: ${trendDrawables.size}个
            历史数据期数: ${records.size}
            
            ## 核心资源 ##
            ${trendDrawables.entries.joinToString("\n") { 
                "- ${it.key} (ID: ${it.value})" 
            }}
            
            ## 示例数据 ##
            最新期号: ${records.last().period}
            红球: ${records.last().redNumbers}
            关联资源ID: ${trendDrawables["history_icon"]}
        """.trimIndent()
    }
    
    fun getTrendChartResources(): Map<String, Int> {
        return mapper.getTrendChartResources()
    }
}