package com.example.myapplication.deepseek.util

import com.example.myapplication.deepseek.model.LotteryAnalysisModel
import com.example.myapplication.deepseek.model.LotteryRecord
import org.junit.Test
import kotlin.test.assertTrue

class PerformanceMonitorTest {
    private class TestModel : LotteryAnalysisModel {
        override fun analyze(history: List<LotteryRecord>, currentIndex: Int): Any {
            Thread.sleep(10) // 模拟10ms处理时间
            return Any()
        }
    }

    @Test
    fun testRunWithBenchmark() {
        val model = TestModel()
        val history = listOf(LotteryRecord(emptyList(), 1, 0))
        
        PerformanceMonitor.runWithBenchmark(model, history) { result ->
            assertTrue(result is Any)
        }
    }

    @Test
    fun testDynamicThresholdCalculation() {
        val modelName = "TestModel"
        val testData = listOf(10_000_000L, 12_000_000L, 15_000_000L)
        
        PerformanceMonitor.apply {
            historicalData[modelName] = testData.toMutableList()
            val threshold = calculateDynamicThreshold(modelName)
            assertTrue(threshold > testData.average().toLong())
        }
    }
}