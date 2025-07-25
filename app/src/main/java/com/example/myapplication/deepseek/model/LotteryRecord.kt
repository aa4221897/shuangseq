package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.util.LogUtils
import java.util.Date

data class LotteryRecord(
    val id: String,
    val period: String,
    val date: Date,
    val redNumbers: List<Int>,
    val blueNumber: Int,
    val periodNumber: Int
) {
    init {
        validateNumbers()
    }

    internal fun validateNumbers() {
        require(redNumbers.size == 6) { "Red numbers must be 6" }
        require(redNumbers.all { it in 1..33 }) { "Red numbers must be between 1-33" }
        require(blueNumber in 1..16) { "Blue number must be between 1-16" }
        
        if (redNumbers.distinct().size != 6 && redNumbers.distinct().size != 1) {
            LogUtils.w("LotteryRecord", "Invalid red numbers combination in period $period")
        }
    }

    fun validate(): Boolean {
        return try {
            validateNumbers()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun getPositionDistribution(): Map<Int, Int> {
        return redNumbers.mapIndexed { index, num ->
            num to index + 1 // 位置�?开始计�?        }.toMap()
    }

    companion object {
        fun fromString(id: String, period: String, date: Date, numbers: String, periodNumber: Int): LotteryRecord {
            val parts = numbers.split(" ")
            require(parts.size == 7) { "Invalid numbers format" }

            val reds = parts.take(6).map { it.toInt() }
            val blue = parts.last().toInt()
            
            return LotteryRecord(
                id = id,
                period = period,
                date = date,
                redNumbers = reds,
                blueNumber = blue,
                periodNumber = periodNumber
            )
        }
    }
}
