package com.example.lotteryprediction.deepseek.model.analyzers

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.IndicatorAnalyzer
import com.example.lotteryprediction.deepseek.model.relations.NumberRelation

/**
 * 位置关联分析�? * 分析各位置号码间的相生相克关�? */
class PositionRelationAnalyzer : IndicatorAnalyzer {
    
    override fun analyze(records: List<LotteryRecord>): Map<String, Map<Int, Double>> {
        val positionRelations = mutableMapOf<String, MutableMap<Int, Double>>()
        
        // 初始化位置关系矩�?        listOf("pos1", "pos2", "pos3", "pos4", "pos5", "pos6").forEach { pos ->
            positionRelations[pos] = mutableMapOf<Int, Double>().withDefault { 0.0 }
        }

        // 分析历史数据中的位置关系
        for (i in 1 until records.size) {
            val current = records[i]
            val previous = records[i-1]
            
            // 分析每个位置与其他位置的关联
            current.redNumbers.forEachIndexed { currentPosIndex, currentNum ->
                val posKey = "pos${currentPosIndex + 1}"
                
                // 检查与上期各位置号码的关系
                previous.redNumbers.forEachIndexed { prevPosIndex, prevNum ->
                    val relationStrength = NumberRelation.getRelationStrength(prevNum, currentNum)
                    positionRelations[posKey]?.merge(
                        prevPosIndex + 1, 
                        relationStrength
                    ) { old, new -> old + new }
                }
            }
        }

        // 标准化关系强�?(转换为概�?
        positionRelations.forEach { (pos, relations) ->
            val total = relations.values.sum().absoluteValue.toDouble()
            if (total > 0) {
                relations.replaceAll { _, value -> value / total }
            }
        }

        return positionRelations
    }
}
