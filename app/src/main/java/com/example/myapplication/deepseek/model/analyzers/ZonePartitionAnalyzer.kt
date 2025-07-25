package com.example.lotteryprediction.deepseek.model.analyzers

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import com.example.lotteryprediction.deepseek.model.IndicatorAnalyzer

/**
 * 五分区优化分析器
 * �?3个红球分�?个区间：
 * 1�?1-7) 2�?8-14) 3�?15-21)
 * 4�?22-28) 5�?29-33)
 */
class ZonePartitionAnalyzer : IndicatorAnalyzer {
    override fun analyze(records: List<LotteryRecord>): Map<String, Any> {
        val zoneStats = mutableMapOf(
            "zone1" to 0, "zone2" to 0, 
            "zone3" to 0, "zone4" to 0, "zone5" to 0
        )
        
        // 统计各区间号码出现频�?        records.forEach { record ->
            record.redNumbers.forEach { num ->
                when (num) {
                    in 1..7 -> zoneStats["zone1"] = zoneStats.getOrDefault("zone1", 0) + 1
                    in 8..14 -> zoneStats["zone2"] = zoneStats.getOrDefault("zone2", 0) + 1
                    in 15..21 -> zoneStats["zone3"] = zoneStats.getOrDefault("zone3", 0) + 1
                    in 22..28 -> zoneStats["zone4"] = zoneStats.getOrDefault("zone4", 0) + 1
                    else -> zoneStats["zone5"] = zoneStats.getOrDefault("zone5", 0) + 1
                }
            }
        }

        // 计算区间分布比例
        val total = zoneStats.values.sum().toDouble()
        val zoneRatios = zoneStats.mapValues { (_, count) ->
            count.toDouble() / total
        }

        return mapOf(
            "count" to zoneStats,
            "ratio" to zoneRatios,
            "hotZones" to getHotZones(zoneRatios)
        )
    }

    private fun getHotZones(ratios: Map<String, Double>): List<String> {
        val avg = ratios.values.average()
        return ratios.filter { it.value > avg }.keys.toList()
    }
}
