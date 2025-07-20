package com.example.myapplication.deepseek.model

import com.example.myapplication.deepseek.util.LogUtils
import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.LinkedHashMap
import java.util.List

class KillerPredictor(private val historyData: List<LotteryRecord>, private val lookbackPeriod: Int = 50) {
    fun getHistoryData(): List<LotteryRecord> = historyData
    private val tag = "KillerPredictor"
    private var coldNumbers: List<Int> = calculateColdHotNumbers()

    fun predictKillerNumbers(currentIndex: Int): List<Int> {
        // 边界检查
        if (currentIndex < lookbackPeriod) {
            val adjustedLookback = maxOf(5, currentIndex)
            LogUtils.w(tag, "Adjusted lookback period from $lookbackPeriod to $adjustedLookback")
        }

        // 获取冷号杀号
        val coldKillers = coldNumbers.take(10)

        // 排除近期出现过的冷号
        val recentAppeared = getRecentAppearedNumbers(currentIndex)
        val filteredKillers = coldKillers.filterNot { recentAppeared.contains(it) }

        // 不足10个时补充全局冷号
        return if (filteredKillers.size >= 10) {
            filteredKillers.take(10)
        } else {
            val globalCold = getGlobalColdNumbers()
            (filteredKillers + globalCold).take(10)
        }
    }

    private fun calculateColdHotNumbers(): List<Int> {
        if (historyData.isEmpty()) return (1..33).take(10) // 默认值
        
        val frequencyMap = mutableMapOf<Int, Int>().apply {
            (1..33).forEach { put(it, 0) }
        }

        historyData.takeLast(lookbackPeriod).forEach { record ->
            record.redNumbers.forEach { num ->
                frequencyMap[num] = frequencyMap.getOrDefault(num, 0) + 1
            }
        }

        return frequencyMap.toList()
            .sortedBy { (_, count) -> count }
            .map { (num, _) -> num }
            .take(10)
    }

    private fun getRecentAppearedNumbers(currentIndex: Int): Set<Int> {
        val startIndex = maxOf(0, currentIndex - 3)
        return historyData.subList(startIndex, currentIndex)
            .flatMap { it.redNumbers }
            .toSet()
    }

    private fun getGlobalColdNumbers(): List<Int> {
        // 全局冷号计算逻辑
        return (1..33).shuffled().take(10) // 临时实现
    }

    fun backtest(startIndex: Int, endIndex: Int): Double {
        var correctCount = 0
        var totalTests = 0
        
        for (i in startIndex until endIndex) {
            val predicted = predictKillerNumbers(i)
            val actual = historyData[i+1].redNumbers
            
            val correct = 10 - (predicted intersect actual.toSet()).size
            correctCount += correct
            totalTests++
        }
        
        return if (totalTests > 0) correctCount.toDouble() / (totalTests * 10) else 0.0
    }
}
