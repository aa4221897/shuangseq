package com.example.lotteryprediction.deepseek.model

import android.util.Log
import com.example.lotteryprediction.deepseek.util.Logger
import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.LinkedHashMap
import java.util.List
import kotlin.math.max
import kotlin.math.min

class LotteryPredictionManager(private val historyData: List<LotteryRecord>) {
    init {
        require(historyData.isNotEmpty()) { "History data cannot be empty" }
    }
    
    private val tag = "PredictionManager"
    private val killerPredictor = KillerPredictor(historyData)
    private val killerOptimizer = KillerOptimizer(killerPredictor)
    private val coverageValidator = CoverageValidator(historyData)
    private val coverageOptimizer = CoverageOptimizer(historyData)
    private var logger: Logger = object : Logger {
        override fun e(tag: String, message: String) {
            Log.e(tag, message)
        }
        override fun d(tag: String, message: String) {
            Log.d(tag, message)
        }
    }

    fun setLogger(logger: Logger) {
        this.logger = logger
    }

    fun predictNextPeriod(): PredictionResult {
        require(historyData.size >= 5) { "At least 5 history records are required" }
        val currentIndex = historyData.lastIndex
        return try {
            // 执行杀号预�?            val killers = killerPredictor.predictKillerNumbers(currentIndex)
            
            // 获取优化建议
            val improvements = killerOptimizer.getActiveImprovements(currentIndex)
            
            // 生成预测结果
            val predictions = Predictions(
                killers = killers,
                danma = predictDanmaNumbers(currentIndex),
                groups = predictGroupCombinations()
            )
            
            // 验证历史覆盖情况
            val lastRecord = historyData.last()
            val validationResult = coverageValidator.validate(lastRecord, predictions)
            
            PredictionResult(
                killers = killers,
                danma = predictions.danma,
                groups = predictions.groups,
                validation = validationResult,
                improvements = improvements
            )
        } catch (e: Exception) {
            logger.e(tag, "Prediction failed: ${e.message}")
            Log.e(tag, "Prediction failed", e)
            throw PredictionException("Prediction failed: ${e.message}")
        }
    }

    fun analyzeHistoricalCoverage() {
        coverageOptimizer.analyzeSuccessPatterns()
        val strategy = coverageOptimizer.generateStrategy()
        logger.d(tag, "Current strategy: $strategy")
    }

    private fun predictDanmaNumbers(currentIndex: Int): List<Int> {
        // 简化实现，实际应使用胆码预测算�?        val windowSize = max(5, min(20, currentIndex).coerceAtLeast(1))
        val startIndex = max(0, currentIndex - windowSize)
        return historyData
            .subList(startIndex, currentIndex)
            .flatMap { it.redNumbers }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
    }

    private fun predictGroupCombinations(): List<List<Int>> {
        // 简化实现，实际应使用组合预测算�?        return listOf(
            (1..6).map { it * 5 - 2 }.take(6),
            (7..12).map { it * 2 + 1 }.take(6),
            (13..18).map { it * 2 }.take(6)
        )
    }

    data class Predictions(
        val killers: List<Int>,
        val danma: List<Int>,
        val groups: List<List<Int>>
    )

    data class PredictionResult(
        val killers: List<Int>,
        val danma: List<Int>,
        val groups: List<List<Int>>,
        val validation: CoverageValidator.CoverageResult,
        val improvements: List<KillerOptimizer.ImprovementPlan>
    )

    class PredictionException(message: String) : Exception(message)
}
