package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.relations.NumberRelation

/**
 * 深度关联预测引擎
 * 基于相生相克关系生成预测
 */
class DeepRelationEngine {
    
    fun generatePredictions(
        lastRecord: LotteryRecord,
        relationStats: Map<String, Map<Int, Double>>
    ): Map<Int, List<Pair<Int, Double>>> {
        val predictions = mutableMapOf<Int, MutableList<Pair<Int, Double>>>()
        
        // 为每个位置生成候选号�?(1-33)
        (1..6).forEach { position ->
            predictions[position] = mutableListOf()
            val posKey = "pos$position"
            
            // 计算每个号码的关联得�?            (1..33).forEach { candidateNum ->
                var score = 0.0
                
                // 考虑上期各位置号码的影响
                lastRecord.redNumbers.forEachIndexed { lastPosIndex, lastNum ->
                    val relationWeight = relationStats[posKey]?.get(lastPosIndex + 1) ?: 0.0
                    val relationEffect = NumberRelation.getRelationStrength(lastNum, candidateNum)
                    score += relationWeight * relationEffect
                }
                
                if (score > 0) {
                    predictions[position]?.add(candidateNum to score)
                }
            }
            
            // 按得分排�?            predictions[position]?.sortByDescending { it.second }
        }
        
        return predictions
    }
    
    fun analyzeHistoricalRelations(
        records: List<LotteryRecord>,
        lookBack: Int = 10
    ): Map<String, Map<Int, Double>> {
        val relationStats = mutableMapOf<String, MutableMap<Int, Double>>()
        
        // 初始化统计结�?        (1..6).forEach { pos ->
            relationStats["pos$pos"] = mutableMapOf<Int, Double>().withDefault { 0.0 }
        }
        
        // 分析近期历史数据
        for (i in maxOf(0, records.size - lookBack) until records.size - 1) {
            val current = records[i]
            val next = records[i + 1]
            
            current.redNumbers.forEachIndexed { currentPos, currentNum ->
                next.redNumbers.forEachIndexed { nextPos, nextNum ->
                    val relation = NumberRelation.getRelationStrength(currentNum, nextNum)
                    val posKey = "pos${nextPos + 1}"
                    relationStats[posKey]?.merge(currentPos + 1, relation) { old, new -> old + new }
                }
            }
        }
        
        // 标准化权�?        relationStats.forEach { (pos, relations) ->
            val total = relations.values.sum().absoluteValue
            if (total > 0) {
                relations.replaceAll { _, value -> value / total }
            }
        }
        
        return relationStats
    }
}
