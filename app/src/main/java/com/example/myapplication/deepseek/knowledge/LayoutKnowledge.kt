package com.example.lotteryprediction.deepseek.knowledge

import com.example.lotteryprediction.deepseek.data.LayoutDataExtractor
import com.example.lotteryprediction.deepseek.data.LotteryLayoutInfo

/**
 * 布局知识管理
 */
object LayoutKnowledge {
    
    private val layouts = mutableListOf<LotteryLayoutInfo>()
    
    fun updateLayoutKnowledge(extractor: LayoutDataExtractor) {
        layouts.clear()
        layouts.addAll(extractor.extractLotteryData())
        
        // 更新到主知识�?        KnowledgeBase.update("layout_info", """
            ## 双色球布局信息 ##
            有效布局数量: ${layouts.size}
            包含走势图的布局: ${layouts.filter { it.containsResultView }.size}
            
            ## 详细布局 ##
            ${layouts.joinToString("\n") { 
                "- ${it.layoutId}: ${it.displayType}" + 
                if(it.containsResultView) " (含走势图)" else ""
            }}
        """.trimIndent())
    }
    
    fun getLayoutsWithTrend(): List<LotteryLayoutInfo> {
        return layouts.filter { it.containsResultView }
    }
}
