package com.example.myapplication.deepseek.model.analyzers

import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.IndicatorAnalyzer

/**
 * 邻码分析器
 * 分析预测号码与实际开奖的邻码关系(±1,±2)
 */
class AdjacentNumberAnalyzer : IndicatorAnalyzer {
    
    override fun analyze(records: List<LotteryRecord>): Map<String, Any> {
        val adjacentStats = mutableMapOf(
            "±1" to 0,
            "±2" to 0,
            "exact" to 0
        )
        
        // 模拟预测过程并统计邻码出现情况
        for (i in 1 until records.size) {
            val predicted = simulatePrediction(records.subList(0, i))
            val actual = records[i].redNumbers
            
            predicted.forEach { predNum ->
                when {
                    actual.contains(predNum) -> adjacentStats["exact"] = adjacentStats["exact"]!! + 1
                    actual.any { it in predNum-1..predNum+1 } -> adjacentStats["±1"] = adjacentStats["±1"]!! + 1
                    actual.any { it in predNum-2..predNum+2 } -> adjacentStats["±2"] = adjacentStats["±2"]!! + 1
                }
            }
        }
        
        // 计算概率
        val total = adjacentStats.values.sum().toDouble()
        val probabilities = adjacentStats.mapValues { (_, count) ->
            count.toDouble() / total
        }
        
        return mapOf(
            "counts" to adjacentStats,
            "probabilities" to probabilities
        )
    }
    
    private fun simulatePrediction(history: List<LotteryRecord>): List<Int> {
        // 简化的预测模拟（实际应使用完整预测逻辑）
        return history.last().redNumbers.map { it + 1 }
    }
}