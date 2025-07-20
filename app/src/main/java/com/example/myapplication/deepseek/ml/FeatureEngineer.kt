package com.example.myapplication.deepseek.ml

import com.example.myapplication.deepseek.model.LotteryRecord
import com.example.myapplication.deepseek.util.LogUtils
import kotlin.math.pow

/**
 * 机器学习特征工程工具
 * 
 * 功能：
 * 1. 特征提取
 * 2. 特征转换
 * 3. 特征选择
 * 
 * 使用示例：
 * ```
 * val features = FeatureEngineer.extractFeatures(history)
 * ```
 */
class FeatureEngineer {
    private val TAG = "FeatureEngineer"
    private val featureImportance = mutableMapOf<String, Double>()
    private var featureSelectionThreshold = 0.5 // 默认特征选择阈值

    /**
     * 从历史数据提取特征
     */
    fun extractFeatures(history: List<LotteryRecord>): List<DoubleArray> {
        return history.mapIndexed { index, record ->
            val features = mutableListOf<Double>().apply {
                // 基础特征
                addAll(record.redNumbers.map { it.toDouble() })
                
                // 统计特征
                add(record.redNumbers.average()) // 平均值
                add(record.redNumbers.sum().toDouble()) // 总和
                add(record.redNumbers.max().toDouble()) // 最大值
                add(record.redNumbers.min().toDouble()) // 最小值
                add(record.redNumbers.standardDeviation()) // 标准差
                
                // 组合特征
                add(record.redNumbers.sumOf { it % 2 }.toDouble()) // 奇偶分布
                add(record.redNumbers.sumOf { if (it <= 16) 1 else 0 }.toDouble()) // 区间分布
                
                // 时序特征(需要历史数据)
                if (index > 0) {
                    val prev = history[index - 1]
                    add(differenceRatio(record.redNumbers, prev.redNumbers))
                    add(continuityRatio(record.redNumbers, prev.redNumbers))
                }
                
                // 高级数学特征
                add(record.redNumbers.geometricMean())
                add(record.redNumbers.harmonicMean())
            }
            features.toDoubleArray()
        }.also {
            LogUtils.d(TAG, "Extracted ${it.size} feature vectors with ${it.firstOrNull()?.size ?: 0} features each")
        }
    }
    
    private fun List<Int>.standardDeviation(): Double {
        val mean = average()
        return kotlin.math.sqrt(sumOf { (it - mean).pow(2) } / size)
    }
    
    private fun List<Int>.geometricMean(): Double {
        return size.toDouble().pow(1.0/size) * 
            fold(1.0) { acc, i -> acc * i }.pow(1.0/size)
    }
    
    private fun List<Int>.harmonicMean(): Double {
        return size / sumOf { 1.0 / it }
    }

    /**
     * 特征标准化
     */
    fun normalizeFeatures(features: List<DoubleArray>): List<DoubleArray> {
        if (features.isEmpty()) return emptyList()
        
        val means = DoubleArray(features[0].size) { 0.0 }
        val stds = DoubleArray(features[0].size)
        stds.fill(0.0)
        
        // 计算均值和标准差
        features.forEach { vec ->
            vec.forEachIndexed { i, v -> means[i] = means[i] + v }
        }
        means.indices.forEach { i -> means[i] = means[i] / features.size }
        
        features.forEach { vec ->
            vec.forEachIndexed { i, v -> stds[i] = stds[i] + (v - means[i]).pow(2) }
        }
        stds.indices.forEach { i -> stds[i] = kotlin.math.sqrt(stds[i]/features.size) }
        
        // 标准化
        return features.map { vec ->
            vec.mapIndexed { i, v -> 
                if (stds[i] != 0.0) (v - means[i])/stds[i] else 0.0
            }.toDoubleArray()
        }.also {
            LogUtils.d(TAG, "Normalized ${it.size} feature vectors")
        }
    }

    private fun differenceRatio(current: List<Int>, previous: List<Int>): Double {
        return current.zip(previous).sumOf { (c, p) -> 
            (c - p).toDouble().pow(2) 
        } / current.size
    }
    
    private fun continuityRatio(current: List<Int>, previous: List<Int>): Double {
        return current.zip(previous).count { (c, p) -> 
            kotlin.math.abs(c - p) <= 1 
        }.toDouble() / current.size
    }
    
    /**
     * 基于重要性分数选择特征
     */
    fun selectFeatures(features: List<DoubleArray>, threshold: Double = featureSelectionThreshold): List<DoubleArray> {
        if (featureImportance.isEmpty()) return features
        
        return features.map { vec ->
            vec.filterIndexed { index, _ -> 
                featureImportance.values.elementAtOrNull(index) ?: 0.0 >= threshold
            }.toDoubleArray()
        }.also {
            LogUtils.d(TAG, "Selected ${it.firstOrNull()?.size ?: 0} features after thresholding")
        }
    }
    
    /**
     * 计算特征重要性
     */
    fun calculateFeatureImportance(features: List<DoubleArray>, labels: List<Int>) {
        // 简单实现 - 实际应使用专业特征选择算法
        features.firstOrNull()?.indices?.forEach { index ->
            val importance = features.map { it[index] }.zip(labels).sumOf { (f, l) ->
                (f - l).pow(2)
            }.let { 1 / (1 + it) }
            featureImportance["feature_$index"] = importance
        }
        
        LogUtils.d(TAG, "Calculated importance for ${featureImportance.size} features")
    }
}
