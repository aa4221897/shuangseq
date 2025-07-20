package com.example.myapplication.deepseek.model

/**
 * 分析结果数据类
 * @param positionResults 同位分析结果
 * @param crossPositionResults 异位分析结果 
 * @param zoneEnergies 分区能量分析结果
 * @param confidence 预测置信度(0-1)
 */
data class AnalysisResult(
    val positionResults: Map<Int, PositionAnalysis>,
    val crossPositionResults: Map<Int, CrossPositionAnalysis>,
    val zoneEnergies: Map<LotteryAnalysisModel.Zone, Double>,
    val confidence: Double
) {
    /**
     * 同位分析结果
     */
    data class PositionAnalysis(
        val sizeRelation: Double, // 大小关系强度(-1到1)
        val parityRelation: Double // 奇偶关系强度(-1到1)
    )

    /**
     * 异位分析结果
     */
    data class CrossPositionAnalysis(
        val sizeMatrix: List<List<Double>>, // 跨位大小关系矩阵
        val parityMatrix: List<List<Double>> // 跨位奇偶关系矩阵
    )
}
