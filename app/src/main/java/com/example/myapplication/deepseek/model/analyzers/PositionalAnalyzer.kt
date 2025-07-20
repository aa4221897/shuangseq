package com.example.myapplication.deepseek.model.analyzers

import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.IndicatorAnalyzer

/**
 * 定位分析器：分析每个位置号码的出现规律
 */
class PositionalAnalyzer : IndicatorAnalyzer {
    override fun analyze(records: List<LotteryRecord>): Map<Int, Map<Int, Int>> {
        val positionStats = mutableMapOf<Int, MutableMap<Int, Int>>()
        
        // 初始化位置字典 (双色球6个红球位置)
        (1..6).forEach { position ->
            positionStats[position] = mutableMapOf<Int, Int>().withDefault { 0 }
        }

        // 统计每个位置上各个号码出现的次数
        records.forEach { record ->
            record.redNumbers.forEachIndexed { index, number ->
                val position = index + 1
                positionStats[position]?.merge(number, 1, Int::plus)
            }
        }

        return positionStats
    }
}