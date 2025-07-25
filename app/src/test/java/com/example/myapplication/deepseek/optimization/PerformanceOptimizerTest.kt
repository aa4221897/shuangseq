package com.example.lotteryprediction.deepseek.optimization

import com.example.lotteryprediction.deepseek.api.DeepSeekApi
import com.example.lotteryprediction.deepseek.model.AdvancedPredictionEngine
import com.example.lotteryprediction.deepseek.util.AndroidLogger
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class PerformanceOptimizerTest {
    private lateinit var engine: AdvancedPredictionEngine
    private lateinit var api: DeepSeekApi
    private lateinit var optimizer: PerformanceOptimizer

    @Before
    fun setup() {
        engine = mock(AdvancedPredictionEngine::class.java)
        api = mock(DeepSeekApi::class.java)
        optimizer = PerformanceOptimizer(engine, AndroidLogger())
    }

    @Test
    fun testOptimizationLevelAdjustment() {
        // 初始应为BALANCED
        assertEquals(
            PerformanceOptimizer.OptimizationLevel.BALANCED,
            optimizer.getCurrentOptimizationLevel()
        )
        
        // 模拟无改进历�?
        optimizer.applyOptimizations("").apply {
            assertEquals(0, improvements.size)
        }
        assertEquals(
            PerformanceOptimizer.OptimizationLevel.AGGRESSIVE,
            optimizer.getCurrentOptimizationLevel()
        )
        
        // 模拟过多改进
        optimizer.applyOptimizations("").apply {
            assertEquals(4, improvements.size) // AGGRESSIVE模式默认4个改�?
        }
        assertEquals(
            PerformanceOptimizer.OptimizationLevel.CONSERVATIVE,
            optimizer.getCurrentOptimizationLevel()
        )
    }

    @Test
    fun testBottleneckAnalysis() = runBlocking {
        val bottlenecks = optimizer.analyzeBottlenecks()
        assertTrue(bottlenecks.isNotEmpty())
        assertTrue(bottlenecks.any { it.contains("内存压力") || it.contains("CPU负载") })
    }

    @Test
    fun testOptimizationResult() = runBlocking {
        `when`(api.optimizeAlgorithm(any())).thenReturn(
            OptimizeResponse("optimized_code", 200)
        )
        
        val result = optimizer.optimizeRuntime()
        assertTrue(result.improvements.isNotEmpty())
        assertTrue(result.after < result.before)
    }
    
    // 辅助方法
    private fun PerformanceOptimizer.getCurrentOptimizationLevel() = 
        this::class.java.getDeclaredField("optimizationLevel")
            .apply { isAccessible = true }
            .get(this) as PerformanceOptimizer.OptimizationLevel
}
