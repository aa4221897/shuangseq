package com.example.lotteryprediction.deepseek.service

import android.content.res.Resources
import com.example.lotteryprediction.deepseek.data.LayoutDataExtractor
import com.example.lotteryprediction.deepseek.knowledge.LayoutKnowledge

/**
 * 资源访问服务
 */
class ResourceService(private val resources: Resources) {
    
    fun integrateLayoutResources() {
        // 1. 提取布局数据
        val extractor = LayoutDataExtractor(resources)
        val layouts = extractor.extractLotteryData()
        
        // 2. 更新知识�?        LayoutKnowledge.updateLayoutKnowledge(extractor)
        
        // 3. 生成资源报告
        val report = """
            ## 双色球资源集成报�?##
            有效布局文件: ${layouts.size}�?            含走势图的布局: ${layouts.count { it.containsResultView }}�?            
            ## 关键布局 ##
            ${layouts.filter { it.containsResultView }
                .joinToString("\n") { "- 布局ID: ${it.layoutId}" }}
        """.trimIndent()
        
        KnowledgeBase.update("resource_report", report)
    }
    
    fun getTrendChartLayouts(): List<Int> {
        return LayoutKnowledge.getLayoutsWithTrend()
            .map { it.layoutId }
    }
}
