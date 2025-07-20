package com.example.myapplication.deepseek.model.v2

import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.AnalysisStrategy
import com.example.myapplication.deepseek.model.AnalysisResult

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