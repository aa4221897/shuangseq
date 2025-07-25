package com.example.lotteryprediction.deepseek.knowledge

interface ResourceFactory {
    fun loadDrawables(): Map<String, Int>
    fun loadLayoutMappings(): Map<Int, List<Int>>
}
