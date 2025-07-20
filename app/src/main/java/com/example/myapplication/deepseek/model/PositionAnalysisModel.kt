package com.example.myapplication.deepseek.model


import kotlin.math.abs

/**
 * 同位分析模型
 * 分析同一位置红球的历史关系
 */
class PositionAnalysisModel : LotteryAnalysisModel() {
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
        
        val positionResults = mutableMapOf<Int, AnalysisResult.PositionAnalysis>()
        
        // 对每个位置(1-6)进行分析
        for (position in 0..5) {
            val sizeRelations = mutableListOf<Double>()
            val parityRelations = mutableListOf<Double>()
            
            // 优化回溯计算(最多5期)
            val lookbackRange = 1..minOf(5, currentIndex.coerceAtLeast(0))
            for (lookback in lookbackRange) {
                val currentNum = history[currentIndex].redNumbers[position]
                val prevNum = history[currentIndex - lookback].redNumbers[position]
                
                // 处理相同数字的特殊情况
                if (currentNum == prevNum) {
                    // 相同数字时给予更强的正相关评分
                    sizeRelations.add(0.8)
                    parityRelations.add(1.0)
                } else {
                    sizeRelations.add(
                        when {
                            currentNum > prevNum -> 1.0
                            else -> -1.0
                        }
                    )
                    parityRelations.add(
                        if (currentNum % 2 == prevNum % 2) 1.0 else -1.0
                    )
                }
            }
            
            // 计算平均关系强度
            val avgSize = if (sizeRelations.isNotEmpty()) sizeRelations.average() else 0.0
            val avgParity = if (parityRelations.isNotEmpty()) parityRelations.average() else 0.0
            
            positionResults[position + 1] = AnalysisResult.PositionAnalysis(avgSize, avgParity)
        }
        
        return AnalysisResult(
            positionResults = positionResults,
            crossPositionResults = emptyMap(),
            zoneEnergies = emptyMap(),
            confidence = calculateConfidence(positionResults.values)
        )
    }
    
    private fun calculateConfidence(results: Collection<AnalysisResult.PositionAnalysis>): Double {
        if (results.isEmpty()) return 0.0
        // 置信度基于关系强度绝对值平均
        val avgStrength = results.flatMap { listOf(it.sizeRelation, it.parityRelation) }
            .map { kotlin.math.abs(it) }
            .average()
        return avgStrength.coerceIn(0.0, 1.0)
    }
}
