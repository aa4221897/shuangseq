package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.util.LogUtils

class CoverageValidator(private val historyData: List<LotteryRecord>) {
    private val tag = "CoverageValidator"

    fun isNumberCoveredInHistory(number: Int, period: String): Boolean {
        return historyData.any { record ->
            record.redNumbers.contains(number) && record.period != period
        }
    }

    fun getContextRecords(currentPeriod: String, lookback: Int): List<LotteryRecord> {
        val currentIndex = historyData.indexOfFirst { it.period == currentPeriod }
        return if (currentIndex >= 0) {
            historyData.subList(maxOf(0, currentIndex - lookback), currentIndex)
        } else {
            emptyList()
        }
    }

    fun classifyCoverage(record: LotteryRecord, unusedContext: List<LotteryRecord>): CoverageOptimizer.CoverageType {
        return when {
            record.redNumbers.count { it % 2 == 0 } >= 4 -> CoverageOptimizer.CoverageType.DANMA_DRIVEN
            record.redNumbers.groupBy { it / 7 }.size <= 3 -> CoverageOptimizer.CoverageType.GROUP_DOMINANT
            else -> CoverageOptimizer.CoverageType.BALANCED_COVERAGE
        }
    }

    fun analyzeNumberDistribution(records: List<LotteryRecord>): CoverageOptimizer.NumberDistribution {
        val total = records.size.toDouble()
        val evenCount = records.sumOf { it.redNumbers.count { num -> num % 2 == 0 } }
        val primeCount = records.sumOf { it.redNumbers.count { num -> isPrime(num) } }
        
        val zoneDist = (1..5).associateWith { zone ->
            records.count { record -> record.redNumbers.any { num -> num / 7 == zone } } / total
        }
        
        return CoverageOptimizer.NumberDistribution(
            evenRatio = evenCount / (total * 6),
            primeRatio = primeCount / (total * 6),
            zoneDistribution = zoneDist
        )
    }

    fun analyzePositionStability(records: List<LotteryRecord>): CoverageOptimizer.PositionStability {
        val posCounts = Array(6) { mutableMapOf<Int, Int>() }
        records.forEach { record ->
            record.redNumbers.forEachIndexed { index, num ->
                posCounts[index][num] = posCounts[index].getOrDefault(num, 0) + 1
            }
        }

        val deviations = posCounts.map { posMap ->
            val avg = posMap.values.average()
            posMap.values.map { count -> (count - avg) * (count - avg) }.average()
        }

        return CoverageOptimizer.PositionStability(
            deviation = deviations.average(),
            hotPositions = posCounts.mapIndexedNotNull { index, map -> 
                index.takeIf { map.values.any { count -> count >= 3 } }
            }
        )
    }

    private fun isPrime(n: Int): Boolean {
        if (n <= 1) return false
        if (n == 2) return true
        if (n % 2 == 0) return false
        for (i in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
            if (n % i == 0) return false
        }
        return true
    }

    fun validate(record: LotteryRecord, predictions: LotteryPredictionManager.Predictions): CoverageResult {
        val coveredNumbers = predictions.groups.flatten() + predictions.danma + predictions.killers
        val totalCovered = record.redNumbers.count { it in coveredNumbers }
        
        return CoverageResult(
            isFullCoverage = totalCovered == 6,
            totalCovered = totalCovered,
            totalPossible = 6
        )
    }

    data class CoverageResult(
        val isFullCoverage: Boolean,
        val totalCovered: Int,
        val totalPossible: Int
    )
}
