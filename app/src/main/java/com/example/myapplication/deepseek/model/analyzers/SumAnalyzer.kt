package com.example.myapplication.deepseek.model.analyzers

import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.IndicatorAnalyzer

/**
 * 和值分析器：分析和值的分布规律
 */
class SumAnalyzer : IndicatorAnalyzer {
    override fun analyze(records: List<LotteryRecord>): Map<String, Any> {
        val sumStats = mutableMapOf<Int, Int>().withDefault { 0 }
        var minSum = Int.MAX_VALUE
        var maxSum = Int.MIN_VALUE
        
        // 计算和值分布
        records.forEach { record ->
            val sum = record.redNumbers.sum()
            sumStats[sum] = sumStats.getValue(sum) + 1
            minSum = minOf(minSum, sum)
            maxSum = maxOf(maxSum, sum)
        }

        return mapOf(
            "distribution" to sumStats,
            "min" to minSum,
            "max" to maxSum,
            "average" to sumStats.keys.average()
        )
    }
}