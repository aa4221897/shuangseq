package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.relations.NumberRelation

/**
 * 生克动力学引�? * 分析主生/被生、主�?被克关系
 */
class DynamicsEngine {
    
    fun analyzeRelations(records: List<LotteryRecord>): DynamicsAnalysis {
        val dominantPromote = mutableMapOf<Int, Int>() // 主生统计
        val dominatedPromote = mutableMapOf<Int, Int>() // 被生统计
        val dominantInhibit = mutableMapOf<Int, Int>() // 主克统计
        val dominatedInhibit = mutableMapOf<Int, Int>() // 被克统计
        
        // 分析历史数据中的生克关系
        for (i in 1 until records.size) {
            val prev = records[i-1].redNumbers
            val current = records[i].redNumbers
            
            prev.forEach { prevNum ->
                current.forEach { currNum ->
                    when (NumberRelation.getRelationStrength(prevNum, currNum)) {
                        1.0 -> { // 相生关系
                            dominantPromote[prevNum] = dominantPromote.getOrDefault(prevNum, 0) + 1
                            dominatedPromote[currNum] = dominatedPromote.getOrDefault(currNum, 0) + 1
                        }
                        -1.0 -> { // 相克关系
                            dominantInhibit[prevNum] = dominantInhibit.getOrDefault(prevNum, 0) + 1
                            dominatedInhibit[currNum] = dominatedInhibit.getOrDefault(currNum, 0) + 1
                        }
                    }
                }
            }
        }
        
        return DynamicsAnalysis(
            dominantPromote = dominantPromote,
            dominatedPromote = dominatedPromote,
            dominantInhibit = dominantInhibit,
            dominatedInhibit = dominatedInhibit
        )
    }
    
    fun generatePredictions(analysis: DynamicsAnalysis): Map<String, List<Int>> {
        // 基于生克关系生成预测
        val promotingNumbers = analysis.dominantPromote
            .filter { it.value > 2 } // 至少出现3次主�?            .keys.sorted()
            
        val inhibitingNumbers = analysis.dominantInhibit
            .filter { it.value > 2 } // 至少出现3次主�?            .keys.sorted()
            
        return mapOf(
            "promote" to promotingNumbers,
            "inhibit" to inhibitingNumbers
        )
    }
}

data class DynamicsAnalysis(
    val dominantPromote: Map<Int, Int>, // 主生号码 -> 出现次数
    val dominatedPromote: Map<Int, Int>, // 被生号码 -> 出现次数
    val dominantInhibit: Map<Int, Int>, // 主克号码 -> 出现次数
    val dominatedInhibit: Map<Int, Int>  // 被克号码 -> 出现次数
)
