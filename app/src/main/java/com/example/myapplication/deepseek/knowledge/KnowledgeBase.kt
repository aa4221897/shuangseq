package com.example.myapplication.deepseek.knowledge

import com.example.myapplication.deepseek.data.LotteryRecord

/**
 * 双色球知识库系统
 */
object KnowledgeBase {
    private val data = mutableMapOf<String, Any>()
    private var version = 1
    
    init {
        // 基础数据结构
        data["data_model"] = """
            LotteryRecord {
                redNumbers: List<Int>(6)
                blueNumber: Int
                period: Int
                date: Date
            }
        """
        
        // 预测算法文档
        data["algorithms"] = """
            1. 传统统计分析
            2. 相生相克关系分析
            3. 生克动力学模型
            4. 邻码偏差预测
        """
        
        // 系统元数据
        data["metadata"] = mapOf(
            "created" to System.currentTimeMillis(),
            "last_updated" to System.currentTimeMillis()
        )
    }
    
    fun update(key: String, value: Any) {
        data[key] = value
        data["metadata"] = (data["metadata"] as Map<*, *>).toMutableMap().apply {
            put("last_updated", System.currentTimeMillis())
        }
        version++
    }
    
    fun get(key: String): Any? {
        return data[key]
    }
    
    fun loadHistoricalData(records: List<LotteryRecord>) {
        data["history_stats"] = analyzeHistory(records)
    }
    
    private fun analyzeHistory(records: List<LotteryRecord>): Map<String, Any> {
        return mapOf(
            "total_periods" to records.size,
            "hot_numbers" to calculateHotNumbers(records),
            "cold_numbers" to calculateColdNumbers(records)
        )
    }
    
    private fun calculateHotNumbers(records: List<LotteryRecord>): List<Int> {
        val frequencyMap = mutableMapOf<Int, Int>()
        
        records.forEach { record ->
            record.redNumbers.forEach { num ->
                frequencyMap[num] = frequencyMap.getOrDefault(num, 0) + 1
            }
            frequencyMap[record.blueNumber] = frequencyMap.getOrDefault(record.blueNumber, 0) + 1
        }
        
        return frequencyMap.entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key }
    }
    
    private fun calculateColdNumbers(records: List<LotteryRecord>): List<Int> {
        val allNumbers = (1..33).toList() + (1..16).toList()
        val drawnNumbers = records.flatMap { it.redNumbers + it.blueNumber }
        
        return allNumbers.filter { num -> 
            drawnNumbers.count { it == num } < (records.size * 0.2) 
        }.take(10)
    }
}