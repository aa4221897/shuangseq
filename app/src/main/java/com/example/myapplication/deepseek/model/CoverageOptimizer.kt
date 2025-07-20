package com.example.myapplication.deepseek.model

import com.example.myapplication.deepseek.util.LogUtils
import kotlin.math.max
import java.util.ArrayList
import java.util.Date
import java.util.LinkedHashSet
import java.util.List

class CoverageOptimizer(private val historyData: List<LotteryRecord>) {
    private val validator = CoverageValidator(historyData)
    private val tag = "CoverageOptimizer"
    private val successPatterns = mutableListOf<SuccessPattern>()
    private val strategyHistory = mutableListOf<CoverageStrategy>()

    data class SuccessPattern(
        val period: String,
        val coverageType: CoverageType,
        val numberDistribution: NumberDistribution,
        val positionStability: PositionStability
    )

    data class CoverageStrategy(
        val danmaWeight: Double,
        val groupWeight: Double,
        val constraints: List<String>,
        val appliedFrom: String
    )

    data class NumberDistribution(
        val evenRatio: Double,
        val primeRatio: Double,
        val zoneDistribution: Map<Int, Double>
    )

    data class PositionStability(
        val deviation: Double,
        val hotPositions: List<Int>
    )

    enum class CoverageType {
        DANMA_DRIVEN,
        GROUP_DOMINANT,
        BALANCED_COVERAGE
    }

    fun analyzeSuccessPatterns() {
        val successfulRecords = historyData.filter { record ->
            record.redNumbers.all { num ->
                // 模拟验证逻辑，实际应从CoverageValidator获取结果
                validator.isNumberCoveredInHistory(num, record.period)
            }
        }

        successPatterns.clear()
        successfulRecords.forEach { record ->
            try {
                val pattern = extractPattern(record)
                successPatterns.add(pattern)
            } catch (e: Exception) {
                LogUtils.e(tag, "Failed to extract pattern for ${record.period}", e)
            }
        }
    }

    private fun extractPattern(record: LotteryRecord): SuccessPattern {
        val prevRecords = validator.getContextRecords(record.period, 30)
        return SuccessPattern(
            period = record.period,
            coverageType = validator.classifyCoverage(record, prevRecords),
            numberDistribution = validator.analyzeNumberDistribution(prevRecords),
            positionStability = validator.analyzePositionStability(prevRecords)
        )
    }

    fun generateStrategy(): CoverageStrategy {
        return if (successPatterns.size >= 20) {
            generateDataDrivenStrategy()
        } else {
            generateConservativeStrategy()
        }.also {
            strategyHistory.add(it)
            LogUtils.i(tag, "Generated new strategy: $it")
        }
    }

    private fun generateDataDrivenStrategy(): CoverageStrategy {
        val typeCounts = successPatterns.groupingBy { it.coverageType }.eachCount()
        val total = typeCounts.values.sum().toDouble()

        return CoverageStrategy(
            danmaWeight = typeCounts[CoverageType.DANMA_DRIVEN]?.div(total) ?: 0.4,
            groupWeight = typeCounts[CoverageType.GROUP_DOMINANT]?.div(total) ?: 0.3,
            constraints = deriveConstraints(),
            appliedFrom = historyData.last().period
        )
    }

    private fun deriveConstraints(): List<String> {
        return listOf(
            "even_ratio > 0.65",
            "prime_ratio < 0.5",
            "pos_deviation < 0.85"
        ) // 简化版约束
    }

    private fun generateConservativeStrategy(): CoverageStrategy {
        return CoverageStrategy(
            danmaWeight = 0.4,
            groupWeight = 0.3,
            constraints = emptyList(),
            appliedFrom = historyData.last().period
        ).also {
            LogUtils.w(tag, "Using conservative strategy due to insufficient data")
        }
    }

    // 辅助方法省略...
}
