package com.example.lotteryprediction.deepseek.util

import android.util.Log
import com.example.lotteryprediction.deepseek.model.LotteryAnalysisModel
import com.example.lotteryprediction.deepseek.model.LotteryRecord
import kotlin.system.measureNanoTime

/**
 * 性能监控工具
 * 
 * 功能�? * 1. 记录各模型执行时�? * 2. 生成性能报告
 * 3. 检测性能异常
 * 
 * 使用示例�? * ```
 * PerformanceMonitor.runWithBenchmark(model, history) { result ->
 *    // 使用预测结果
 * }
 * ```
 */
object PerformanceMonitor {
    private const val TAG = "Performance"
    private val staticThresholds = mapOf(
        "PositionAnalysis" to 10_000_000L, // 10ms
        "CrossPosition" to 20_000_000L,    // 20ms
        "ZoneEnergy" to 15_000_000L,       // 15ms
        "MLPrediction" to 50_000_000L      // 50ms
    )
    private val historicalData = mutableMapOf<String, MutableList<Long>>()
    private const val HISTORY_SIZE = 20

    fun <T> runWithBenchmark(
        model: LotteryAnalysisModel,
        history: List<LotteryRecord>,
        block: (Any) -> T
    ): T {
        val modelName = model::class.simpleName ?: "Unknown"
        val time = measureNanoTime {
            val result = model.analyze(history, history.lastIndex)
            block(result)
        }

        // 记录历史数据
        historicalData.getOrPut(modelName) { mutableListOf() }.apply {
            add(time)
            if (size > HISTORY_SIZE) removeAt(0)
        }

        val staticThreshold = staticThresholds[modelName]
        val dynamicThreshold = calculateDynamicThreshold(modelName)
        
        LogUtils.i(TAG, """
            $modelName performance:
            - Current: ${time/1_000_000}ms
            - Static threshold: ${staticThreshold?.let { it/1_000_000 } ?: "N/A"}ms
            - Dynamic threshold: ${dynamicThreshold/1_000_000}ms
            - History avg: ${(historicalData[modelName]?.average() ?: 0.0).toLong()/1_000_000}ms
        """.trimIndent())
        
        if (staticThreshold != null && time > staticThreshold) {
            LogUtils.w(TAG, "Static threshold alert: $modelName")
        }
        
        if (time > dynamicThreshold) {
            LogUtils.w(TAG, "Dynamic threshold alert: $modelName")
        }

        return block(model.analyze(history, history.lastIndex))
    }
    
    private fun calculateDynamicThreshold(modelName: String): Long {
        val history = historicalData[modelName] ?: return Long.MAX_VALUE
        if (history.size < 3) return staticThresholds[modelName] ?: Long.MAX_VALUE
        
        val avg = history.average()
        val stdDev = kotlin.math.sqrt(
            history.map { (it - avg) * (it - avg) }.average()
        )
        
        return (avg + 2 * stdDev).toLong()
    }

    fun generateReport(): String {
        // 生成汇总报告逻辑
        return "Performance report generated"
    }
}
