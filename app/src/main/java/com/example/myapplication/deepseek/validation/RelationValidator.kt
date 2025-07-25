package com.example.lotteryprediction.deepseek.validation

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.DynamicsAnalysis
import com.example.lotteryprediction.deepseek.model.DynamicsEngine
import kotlin.math.roundToInt

/**
 * 关系验证系统
 * 验证生克关系的预测效�? */
class RelationValidator(private val engine: DynamicsEngine) {
    
    fun validate(records: List<LotteryRecord>): ValidationReport {
        require(records.size > 100) { "至少需�?00期数据验�? }
        
        val testSize = (records.size * 0.2).roundToInt()
        val trainingData = records.dropLast(testSize)
        val testData = records.takeLast(testSize)
        
        // 1. 训练模型
        val analysis = engine.analyzeRelations(trainingData)
        
        // 2. 执行预测验证
        var hitCount = 0
        var adjacentHit = 0
        val predictions = mutableListOf<Pair<List<Int>, List<Int>>>()
        
        for (i in trainingData.size until records.size - 1) {
            val current = records[i]
            val next = records[i+1]
            
            val predicted = engine.generatePredictions(
                engine.analyzeRelations(records.subList(0, i))
            )
            
            // 验证主生号码是否在下期出�?            val promoteHits = predicted["promote"]?.count { next.redNumbers.contains(it) } ?: 0
            // 验证主克号码是否抑制下期号码
            val inhibitHits = predicted["inhibit"]?.count { !next.redNumbers.contains(it) } ?: 0
            
            hitCount += promoteHits
            adjacentHit += countAdjacentHits(predicted["promote"] ?: emptyList(), next.redNumbers)
            
            predictions.add(predicted["promote"]?.sorted() ?: emptyList() to next.redNumbers.sorted())
        }
        
        return ValidationReport(
            totalTests = testSize,
            promoteHitRate = hitCount.toDouble() / (predicted["promote"]?.size ?: 1 * testSize),
            inhibitEffectiveness = inhibitHits.toDouble() / (predicted["inhibit"]?.size ?: 1 * testSize),
            adjacentHitRate = adjacentHit.toDouble() / (predicted["promote"]?.size ?: 1 * testSize),
            caseStudies = predictions.takeLast(10)
        )
    }
    
    private fun countAdjacentHits(predicted: List<Int>, actual: List<Int>): Int {
        return predicted.count { pred ->
            actual.any { act -> act in pred-2..pred+2 }
        }
    }
}

data class ValidationReport(
    val totalTests: Int,
    val promoteHitRate: Double, // 主生号码命中�?    val inhibitEffectiveness: Double, // 主克抑制效果
    val adjacentHitRate: Double, // 邻码命中�?    val caseStudies: List<Pair<List<Int>, List<Int>>> // 预测与实际对比案�?)
