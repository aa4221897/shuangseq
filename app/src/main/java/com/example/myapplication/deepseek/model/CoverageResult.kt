package com.example.lotteryprediction.deepseek.model

data class CoverageResult(
    val isFullCoverage: Boolean,
    val totalCovered: Int,
    val totalPossible: Int
) {
    val coverageRate: Double get() = totalCovered.toDouble() / totalPossible.toDouble()
}
