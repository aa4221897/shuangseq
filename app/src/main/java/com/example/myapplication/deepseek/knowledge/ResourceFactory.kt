package com.example.myapplication.deepseek.knowledge

interface ResourceFactory {
    fun loadDrawables(): Map<String, Int>
    fun loadLayoutMappings(): Map<Int, List<Int>>
}