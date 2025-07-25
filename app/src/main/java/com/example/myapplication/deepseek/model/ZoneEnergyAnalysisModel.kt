package com.example.lotteryprediction.deepseek.model

import kotlin.math.pow
import kotlin.math.max

/**
 * 分区能量分析模型
 * 分析各号码分区的历史能量分布
 */
class ZoneEnergyAnalysisModel : LotteryAnalysisModel() {
    private val repeatNumberStats = mutableMapOf<Int, Int>() // 记录号码重复次数
    
    override fun analyze(history: List<LotteryRecord>, currentIndex: Int): AnalysisResult {
        if (history.isEmpty() || currentIndex < 0 || currentIndex >= history.size) {
            return AnalysisResult(
                positionResults = emptyMap(),
                crossPositionResults = emptyMap(),
                zoneEnergies = emptyMap(),
                confidence = 0.0
            )
        }
        
        try {
            validateHistoryData(history)
        } catch (e: Exception) {
            return AnalysisResult(
                positionResults = emptyMap(),
                crossPositionResults = emptyMap(),
                zoneEnergies = emptyMap(),
                confidence = 0.0
            )
        }
        
        // 计算各分区历史出现次数和重号频率
        val zoneCounts = LotteryAnalysisModel.Zone.values().associateWith { mutableListOf<Int>() }
        repeatNumberStats.clear() // 清空之前的统计数�?        
        val startIdx = max(0, currentIndex - 10) // 最多回�?0�?        // 统计重号频率
        for (i in startIdx until currentIndex) {
            history[i].redNumbers.forEach { num ->
                if (history[i+1].redNumbers.contains(num)) {
                    repeatNumberStats[num] = repeatNumberStats.getOrDefault(num, 0) + 1
                }
            }
        }
        for (i in startIdx..currentIndex) {
            val counts = LotteryAnalysisModel.Zone.values().associateWith { 0 }.toMutableMap()
            
            history[i].redNumbers.forEach { num ->
                val zone = LotteryAnalysisModel.getNumberZone(num)
                // 增加重号权重
                val weight = if (repeatNumberStats.containsKey(num)) 1 + repeatNumberStats[num]!! * 0.2 else 1.0
                counts[zone] = counts.getOrDefault(zone, 0) + (1 * weight).toInt()
            }
            
            // 将当前计数添加到zoneCounts�?            LotteryAnalysisModel.Zone.values().forEach { zone ->
                zoneCounts[zone]?.add(counts.getOrDefault(zone, 0))
            }
        }
        
        // 计算加权能量�?        val zoneEnergies = mutableMapOf<LotteryAnalysisModel.Zone, Double>()
        
        // 计算各分区能量�?        LotteryAnalysisModel.Zone.values().forEach { zone ->
            val counts = zoneCounts[zone] ?: emptyList()
            zoneEnergies[zone] = if (counts.isNotEmpty()) calculateWeightedAverage(counts.map { it.toDouble() }) else 0.0
        }
        println("DEBUG: Zone counts: ${zoneCounts.mapValues { it.value.size }}")
        
        println("DEBUG: Final zone energies: $zoneEnergies")
        
        val finalConfidence = calculateConfidence(zoneEnergies)
        return AnalysisResult(
            positionResults = emptyMap(),
            crossPositionResults = emptyMap(),
            zoneEnergies = zoneEnergies,
            confidence = finalConfidence
        )
    }
    
    private fun calculateConfidence(energies: Map<LotteryAnalysisModel.Zone, Double>): Double {
        if (energies.isEmpty()) return 0.0
        val avgEnergy = energies.values.average()
        val variance = energies.values.map { (it - avgEnergy).pow(2) }.average()
        // 考虑重号因素提高置信�?        val repeatFactor = if (repeatNumberStats.isNotEmpty()) {
            1.0 + (repeatNumberStats.values.average() * 0.05).coerceAtMost(0.2)
        } else {
            1.0
        }
        val baseConfidence = (1.0 - (variance / 4.0)).coerceIn(0.0, 1.0)
        return (baseConfidence * repeatFactor).coerceAtMost(1.0)
    }
    
    /**
     * 获取重号统计信息
     */
    fun getRepeatNumberStats(): Map<Int, Int> {
        return repeatNumberStats.toMap()
    }
}
