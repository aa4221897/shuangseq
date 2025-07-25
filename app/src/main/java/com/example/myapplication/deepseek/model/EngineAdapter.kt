package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.v2.ModularAnalysisEngine

class EngineAdapter {
    private val modularEngine = ModularAnalysisEngine(
        mapOf(
            "zone" to ZonePartitionAnalyzer(),
            "adjacent" to AdjacentNumberAnalyzer(),
            "position" to PositionalAnalyzer()
        )
    )

    fun analyze(records: List<LotteryRecord>) = modularEngine.analyze(records)
}
