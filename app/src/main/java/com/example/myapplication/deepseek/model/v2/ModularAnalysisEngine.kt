package com.example.lotteryprediction.deepseek.model.v2

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.AnalysisStrategy
import com.example.lotteryprediction.deepseek.model.AnalysisResult

class ModularAnalysisEngine(
    private val strategies: Map<String, AnalysisStrategy>
) {
    fun analyze(records: List<LotteryRecord>): AnalysisResult {
        return AnalysisResult(
            strategies.mapValues { (_, strategy) ->
                strategy.analyze(records) 
            }
        )
    }
}
