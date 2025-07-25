package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.util.LogUtils
import java.util.ArrayList
import java.util.Date
import java.util.LinkedHashMap
import java.util.List

class KillerOptimizer(private val predictor: KillerPredictor) {
    private val tag = "KillerOptimizer"
    private val errorLog = mutableListOf<ErrorContext>()
    private val improvementPlans = mutableListOf<ImprovementPlan>()

    data class ErrorContext(
        val period: Int,
        val errorNumbers: List<Int>,
        val frequencyStats: Map<Int, Int>,
        val positionStats: Map<Int, PositionDistribution>
    )

    data class ImprovementPlan(
        val applyFrom: Int,
        val filterRules: List<String>,
        val positionRules: List<String>
    )

    data class PositionDistribution(
        val positions: List<Int>,
        val deviation: Double
    )

    fun analyzeErrors(currentIndex: Int, predicted: List<Int>, actual: List<Int>) {
        val errors = predicted intersect actual.toSet()
        if (errors.isEmpty()) return

        val ctx = ErrorContext(
            period = currentIndex,
            errorNumbers = errors.toList(),
            frequencyStats = getFrequencyStats(errors, currentIndex),
            positionStats = getPositionStats(errors, currentIndex)
        )
        
        errorLog.add(ctx)
        generateImprovementPlan(ctx)
    }

    private fun getFrequencyStats(errorNumbers: Set<Int>, currentIndex: Int): Map<Int, Int> {
        return errorNumbers.associateWith { num ->
            predictor.getHistoryData()
                .subList(maxOf(0, currentIndex - 10), currentIndex)
                .count { it.redNumbers.contains(num) }
        }
    }

    private fun getPositionStats(errorNumbers: Set<Int>, currentIndex: Int): Map<Int, PositionDistribution> {
        return errorNumbers.associateWith { num ->
            val positions = predictor.getHistoryData()
                .subList(maxOf(0, currentIndex - 20), currentIndex)
                .mapNotNull { record -> 
                    record.redNumbers.indexOf(num).takeIf { it >= 0 }
                }
            
            val avg = positions.average()
            val deviation = positions.map { pos -> (pos - avg) * (pos - avg) }.average()
            
            PositionDistribution(positions, deviation)
        }
    }

    private fun generateImprovementPlan(ctx: ErrorContext) {
        val plan = ImprovementPlan(
            applyFrom = ctx.period + 1,
            filterRules = buildFilterRules(ctx),
            positionRules = buildPositionRules(ctx)
        )
        
        if (plan.filterRules.isNotEmpty() || plan.positionRules.isNotEmpty()) {
            improvementPlans.add(plan)
            LogUtils.i(tag, "Generated improvement plan for period ${ctx.period}")
        }
    }

    private fun buildFilterRules(ctx: ErrorContext): List<String> {
        return ctx.errorNumbers.filter { num ->
            ctx.frequencyStats[num]?.let { it > 2 } == true
        }.map { num ->
            "exclude_if_recent_freq_gt_${ctx.frequencyStats[num]?.minus(1)}"
        }
    }

    private fun buildPositionRules(ctx: ErrorContext): List<String> {
        return ctx.errorNumbers.mapNotNull { num ->
            ctx.positionStats[num]?.let { stats ->
                "avoid_position_${stats.positions.mode()}" 
            }
        }
    }

    fun getActiveImprovements(currentIndex: Int): List<ImprovementPlan> {
        return improvementPlans.filter { it.applyFrom <= currentIndex }
    }
}

private fun <T> List<T>.mode(): T {
    return groupBy { it }.maxByOrNull { it.value.size }?.key ?: first()
}
