package com.example.lotteryprediction.deepseek.knowledge

import com.example.lotteryprediction.deepseek.data.ResourceMapper

/**
 * 资源知识管理
 */
object ResourceKnowledge {
    
    fun updateResourceKnowledge(mapper: ResourceMapper) {
        val trendResources = mapper.getTrendChartResources()
        val layoutMappings = mapper.getLayoutToDrawableMap()
        
        KnowledgeBase.update("resource_mapping", """
            ## 走势图相关资�?##
            关键资源数量: ${trendResources.size}
            
            ## 布局-资源映射 ##
            ${layoutMappings.entries.joinToString("\n") { (layout, drawables) ->
                "- 布局ID $layout 关联资源: ${drawables.joinToString()}"
            }}
            
            ## 核心资源ID ##
            ${trendResources.entries.joinToString("\n") { 
                "${it.key}: ${it.value}" 
            }}
        """.trimIndent())
    }
    
    fun getTrendChartDrawables(): Map<String, Int> {
        return when (val res = KnowledgeBase.get("resource_mapping")) {
            is Map<*, *> -> {
                res.filterKeys { it is String }
                  .filterValues { it is Int }
                  .mapKeys { it.key as String }
                  .mapValues { it.value as Int }
            }
            else -> emptyMap()
        }
    }
}
