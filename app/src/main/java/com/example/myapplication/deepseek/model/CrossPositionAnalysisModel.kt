package com.example.myapplication.deepseek.model


import kotlin.math.abs

/**
 * 异位分析模型
 * 分析不同位置红球间的历史关系
 */
class CrossPositionAnalysisModel : LotteryAnalysisModel() {
    override fun analyze(history: List<LotteryRecord>, currentIndex: Int): AnalysisResult {
        if (history.isEmpty() || currentIndex < 1) {
            return AnalysisResult(
                positionResults = emptyMap(),
                crossPositionResults = (1..6).associateWith { 
                    AnalysisResult.CrossPositionAnalysis(
                        sizeMatrix = emptyList(),
                        parityMatrix = emptyList()
                    )
                },
                zoneEnergies = emptyMap(),
                confidence = 0.0
            )
        }
        validateHistoryData(history)
        val crossResults = mutableMapOf<Int, AnalysisResult.CrossPositionAnalysis>()
        
        // 对每个当前位置(1-6)进行分析
        for (currentPos in 0..5) {
            val sizeMatrix = mutableListOf<List<Double>>()
            val parityMatrix = mutableListOf<List<Double>>()
            
            // 回溯1-5期数据
            for (lookback in 1..minOf(5, currentIndex)) {
                try {
                    val currentNum = history[currentIndex].redNumbers[currentPos]
                    val prevRecord = history[currentIndex - lookback]
                    
                    val sizeRelations = mutableListOf<Double>()
                    val parityRelations = mutableListOf<Double>()
                    
                    // 对比所有历史位置(1-6)
                    for (prevPos in 0..5) {
                        val prevNum = prevRecord.redNumbers[prevPos]
                        
                        // 计算大小关系
                        sizeRelations.add(
                            when {
                                currentNum > prevNum -> 1.0
                                currentNum < prevNum -> -1.0
                                else -> 0.0
                            }
                        )
                        
                        // 计算奇偶关系
                        parityRelations.add(
                            if (currentNum % 2 == prevNum % 2) 1.0 else -1.0
                        )
                    }
                    
                    sizeMatrix.add(sizeRelations)
                    parityMatrix.add(parityRelations)
                } catch (e: IndexOutOfBoundsException) {
                    break // 终止回溯
                }
            }
            
            crossResults[currentPos + 1] = AnalysisResult.CrossPositionAnalysis(
                sizeMatrix = sizeMatrix,
                parityMatrix = parityMatrix
            )
        }
        
        val result = AnalysisResult(
            positionResults = emptyMap(),
            crossPositionResults = crossResults,
            zoneEnergies = emptyMap(),
            confidence = calculateConfidence(crossResults.values)
        )
        return result
    }
    
    private fun calculateConfidence(results: Collection<AnalysisResult.CrossPositionAnalysis>): Double {
        if (results.isEmpty()) return 0.0
        // 优化置信度计算
        return results.map { analysis ->
            val strongSize = analysis.sizeMatrix.sumOf { row -> row.count { abs(it) > 0.5 } }
            val strongParity = analysis.parityMatrix.sumOf { row -> row.count { abs(it) > 0.5 } }
            val total = analysis.sizeMatrix.sumOf { it.size } + analysis.parityMatrix.sumOf { it.size }
            if (total > 0) (strongSize + strongParity).toDouble() / total else 0.0
        }.average().coerceIn(0.0, 1.0)
    }
}
