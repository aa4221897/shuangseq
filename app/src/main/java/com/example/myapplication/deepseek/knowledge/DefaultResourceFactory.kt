package com.example.lotteryprediction.deepseek.knowledge

import com.example.lotteryprediction.deepseek.data.ResourceMapper

class DefaultResourceFactory(
    private val mapper: ResourceMapper
) : ResourceFactory {
    override fun loadDrawables(): Map<String, Int> {
        return mapper.getTrendChartResources()
            .mapValues { it.value }
    }

    override fun loadLayoutMappings(): Map<Int, List<Int>> {
        return mapper.getLayoutToDrawableMap()
            .mapKeys { it.key.toInt() }
    }
}
