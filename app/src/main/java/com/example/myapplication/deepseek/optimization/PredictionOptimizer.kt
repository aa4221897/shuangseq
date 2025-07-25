package com.example.lotteryprediction.deepseek.optimization

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.FactorAnalysisEngine
import com.example.lotteryprediction.deepseek.model.LotteryAnalysisEngine
import com.example.lotteryprediction.deepseek.validation.EnhancedBacktestEngine

/**
 * 预测优化策略引擎
 * 功能�? * 1. 自动调整分析参数
 * 2. 优化因素权重分配
 * 3. 生成最优预测方�? */
class PredictionOptimizer(
    private val analysisEngine: LotteryAnalysisEngine,
    private val factorEngine: FactorAnalysisEngine,
    private val backtestEngine: EnhancedBacktestEngine
) {
    
    fun optimizeStrategies(
        allRecords: List<LotteryRecord>,
        iterations: Int = 100
    ): OptimizationReport {
        val optimizationLog = mutableListOf<OptimizationStep>()
        var bestStrategy = emptyMap<String, Any>()
        var bestHitRate = 0.0
        
        // 迭代优化
        repeat(iterations) { iteration ->
            val currentParams = generateParameters(iteration)
            val testResult = backtestEngine.runComprehensiveBacktest(
                allRecords, 
                testSize = 50
            )
            
            // 记录优化步骤
            optimizationLog.add(
                OptimizationStep(
                    iteration = iteration,
                    parameters = currentParams,
                    hitRate = testResult.hitStats.totalHitRate,
                    suggestions = testResult.optimizationSuggestions
                )
            )
            
            // 更新最佳策�?            if (testResult.hitStats.totalHitRate > bestHitRate) {
                bestHitRate = testResult.hitStats.totalHitRate
                bestStrategy = currentParams
            }
        }
        
        return OptimizationReport(
            bestStrategy = bestStrategy,
            bestHitRate = bestHitRate,
            optimizationHistory = optimizationLog
        )
    }
    
    private fun generateParameters(iteration: Int): Map<String, Any> {
        // 基于迭代次数生成不同参数组合
        return mapOf(
            "trendWeight" to (0.1 + (iteration % 10) * 0.1),
            "zoneWeight" to (0.5 - (iteration % 5) * 0.1),
            "hotColdRatio" to (0.3 + (iteration % 7) * 0.05)
        )
    }
}

data class OptimizationStep(
    val iteration: Int,
    val parameters: Map<String, Any>,
    val hitRate: Double,
    val suggestions: List<String>
)

data class OptimizationReport(
    val bestStrategy: Map<String, Any>,
    val bestHitRate: Double,
    val optimizationHistory: List<OptimizationStep>
)
