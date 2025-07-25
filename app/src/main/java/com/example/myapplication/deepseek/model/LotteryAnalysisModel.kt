package com.example.lotteryprediction.deepseek.model

import kotlin.math.pow

/**
 * 双色球预测分析模型基�? * 
 * 提供以下核心功能�? * 1. 数据有效性验�? * 2. 号码分区计算
 * 3. 加权平均值计�? * 
 * 子类需实现具体分析逻辑�? * @see PositionAnalysisModel 同位分析
 * @see CrossPositionAnalysisModel 跨位分析
 * @see ZoneEnergyAnalysisModel 分区能量分析
 */
abstract class LotteryAnalysisModel {
    abstract fun analyze(history: List<LotteryRecord>): AnalysisResult
    
    protected fun commonAnalysisSteps(history: List<LotteryRecord>): Map<String, Any> {
        // 合并的公共分析逻辑
    }
    
    enum class Zone {
     * 红球分区枚举
     * 
     * 将红球号�?1-33)划分�?个区域：
     * - LOW: 1-6
     * - MID_LOW: 7-12
     * - MID: 13-18
     * - MID_HIGH: 19-24
     * - HIGH: 25-33
     */
    enum class Zone {
        LOW, MID_LOW, MID, MID_HIGH, HIGH;

        companion object {
            fun fromNumber(number: Int): Zone {
                return when {
                    number <= 6 -> LOW
                    number <= 12 -> MID_LOW
                    number <= 18 -> MID
                    number <= 24 -> MID_HIGH
                    else -> HIGH
                }
            }
        }
    }

    companion object {
        // 红球范围
        const val MIN_RED = 1
        const val MAX_RED = 33
        
        // 分区边界定义
        val ZONE_BOUNDARIES = mapOf(
            Zone.LOW to 1..6,
            Zone.MID_LOW to 7..12,
            Zone.MID to 13..18,
            Zone.MID_HIGH to 19..24,
            Zone.HIGH to 25..33
        )
        
        /**
         * 验证历史数据有效�?         * @param history 历史开奖记录列�?         * @throws IllegalArgumentException 当数据不符合以下要求时抛出异常：
         * 1. 历史记录少于6�?         * 2. 某期红球数量不等�?
         * 3. 红球未按升序排列
         * 4. 红球数值超�?-33范围
         */
        fun validateHistoryData(history: List<LotteryRecord>) {
            require(history.size >= 6) { "需要至�?期历史数�? }
            history.forEach { record ->
                require(record.redNumbers.size == 6) { "每期应含6个红�? }
                require(record.redNumbers == record.redNumbers.sorted()) { "红球需按顺序排�? }
                require(record.redNumbers.all { it in MIN_RED..MAX_RED }) { "红球值超范围" }
            }
        }
        
        /**
         * 获取红球号码所属分�?         * @param number 红球号码(1-33)
         * @return 对应的分区枚�?         * @throws IllegalArgumentException 当号码不�?-33范围内时抛出异常
         * 
         * 示例�?         * ```
         * val zone = getNumberZone(5) // 返回Zone.LOW
         * ```
         */
        fun getNumberZone(number: Int): Zone {
            return when (number) {
                in 1..6 -> Zone.LOW
                in 7..12 -> Zone.MID_LOW
                in 13..18 -> Zone.MID
                in 19..24 -> Zone.MID_HIGH
                in 25..33 -> Zone.HIGH
                else -> throw IllegalArgumentException("无效的红球号�? $number")
            }
        }

        /**
         * 计算时间衰减加权平均�?         * @param values 数值列�?按时间顺序排列，最新数据在最�?
         * @param decayRate 衰减�?0-1)，默�?.5，越小表示近期数据权重越�?         * @return 加权平均�?         * @throws IllegalArgumentException 当衰减率不在0-1范围内时抛出异常
         * 
         * 示例�?         * ```
         * val avg = calculateWeightedAverage(listOf(1.0, 2.0, 3.0)) 
         * // 权重分布为[0.25, 0.5, 1.0]
         * ```
         */
        fun calculateWeightedAverage(values: List<Double>, decayRate: Double = 0.5): Double {
            require(decayRate in 0.0..1.0) { "衰减率必须在0-1之间" }
            val weights = List(values.size) { i -> decayRate.pow(values.size - i - 1) }
            val totalWeight = weights.sum()
            return if (totalWeight > 0) {
                values.zip(weights).sumOf { (v, w) -> v * w } / totalWeight
            } else {
                0.0
            }
        }
    }
    
    /**
     * 执行分析
     */
    abstract fun analyze(history: List<LotteryRecord>, currentIndex: Int): AnalysisResult
}
