package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.data.LotteryRecord

/**
 * 复合分析引擎
 * 整合传统统计分析与相生相克关系分�? */
class CompositeAnalysisEngine(
    private val classicEngine: LotteryAnalysisEngine,
    private val relationEngine: DeepRelationEngine
) {
    
    fun analyzeAndPredict(records: List<LotteryRecord>): CompositePrediction {
        require(records.size >= 10) { "至少需�?0期历史数�? }
        
        // 1. 传统统计分析
        val classicResult = classicEngine.analyze(records)
        
        // 2. 相生相克关系分析
        val lastRecord = records.last()
        val relationStats = relationEngine.analyzeHistoricalRelations(records)
        val relationPredictions = relationEngine.generatePredictions(lastRecord, relationStats)
        
        // 3. 结果融合
        val finalPredictions = mergePredictions(
            classicResult, 
            relationPredictions
        )
        
        return CompositePrediction(
            classicAnalysis = classicResult,
            relationAnalysis = relationStats,
            finalNumbers = finalPredictions
        )
    }
    
    private fun mergePredictions(
        classicResult: Any,
        relationPredictions: Map<Int, List<Pair<Int, Double>>>
    ): Map<Int, List<Int>> {
        // 实现两种预测结果的融合算�?        // 这里简化处理：取关系分析的�?个推荐号�?        return relationPredictions.mapValues { (_, nums) ->
            nums.take(3).map { it.first }
        }
    }
}

data class CompositePrediction(
    val classicAnalysis: Any,
    val relationAnalysis: Map<String, Map<Int, Double>>,
    val finalNumbers: Map<Int, List<Int>>
)
