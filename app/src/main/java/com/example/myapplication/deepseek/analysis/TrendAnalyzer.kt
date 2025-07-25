package com.example.lotteryprediction.deepseek.analysis

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.data.TrendData
import kotlin.math.roundToInt
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.flatMap
import kotlin.collections.map
import kotlin.collections.groupingBy
import kotlin.collections.eachCount
import kotlin.collections.filter
import kotlin.collections.keys
import kotlin.collections.toList
import kotlin.collections.associate
import kotlin.collections.sumOf
import kotlin.collections.count

/**
 * 走势分析引擎
 */
class TrendAnalyzer {
    
    fun analyzeTrends(records: List<LotteryRecord>): TrendAnalysis {
        // 1. 基础走势分析
        val redStats = analyzeNumbers(records.flatMap { it.redNumbers })
        val blueStats = analyzeNumbers(records.map { it.blueNumber })
        
        // 2. 冷热号分�?        val (hotRed, coldRed) = classifyHotCold(redStats)
        val (hotBlue, coldBlue) = classifyHotCold(blueStats)
        
        // 3. 区间分布分析
        val zoneStats = analyzeZones(records)
        
        return TrendAnalysis(
            redStats = redStats,
            blueStats = blueStats,
            hotRedNumbers = hotRed,
            coldRedNumbers = coldRed,
            hotBlueNumbers = hotBlue,
            coldBlueNumbers = coldBlue,
            zoneDistribution = zoneStats
        )
    }
    
    private fun analyzeNumbers(numbers: List<Int>): Map<Int, Int> {
        return numbers.groupingBy { it }.eachCount()
    }
    
    private fun classifyHotCold(stats: Map<Int, Int>): Pair<List<Int>, List<Int>> {
        val avg = stats.values.average()
        return stats.filter { it.value > avg }.keys.toList() to
               stats.filter { it.value <= avg }.keys.toList()
    }
    
    /**
     * 分析区间分布
     * @param records 彩票记录列表
     * @return 各区间分布百分比
     */
    private fun analyzeZones(records: List<LotteryRecord>): Map<String, Float> {
        val zones = listOf(1..6, 7..12, 13..18, 19..24, 25..30, 31..33)
        // 总出现次�?        val total = records.size * 6f
        
        return zones.associate { zone ->
            // 计算每个区间的百分比
            "zone${zone.first}-${zone.last}" to records.sumOf { record ->
                record.redNumbers.count { it in zone }.toDouble()
            }.toFloat() / total * 100
        }
    }
}

data class TrendAnalysis(
    val redStats: Map<Int, Int>,      // 红球出现频次
    val blueStats: Map<Int, Int>,     // 蓝球出现频次
    val hotRedNumbers: List<Int>,     // 热号红球
    val coldRedNumbers: List<Int>,    // 冷号红球
    val hotBlueNumbers: List<Int>,    // 热号蓝球
    val coldBlueNumbers: List<Int>,   // 冷号蓝球
    val zoneDistribution: Map<String, Float> // 区间分布百分�?)
