package com.example.myapplication.deepseek.model

import com.example.myapplication.deepseek.data.LotteryRecord
import com.example.myapplication.deepseek.model.v2.ModularAnalysisEngine

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