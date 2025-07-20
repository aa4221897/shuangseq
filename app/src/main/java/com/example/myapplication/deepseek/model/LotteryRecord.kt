package com.example.myapplication.deepseek.model

import com.example.myapplication.deepseek.util.LogUtils
import java.util.Date

data class LotteryRecord(
    val period: String,
    val date: Date,
    val redNumbers: List<Int>,
    val blueNumber: Int
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
            num to index + 1 // 位置从1开始计数
        }.toMap()
    }

    companion object {
        fun fromString(period: String, date: Date, numbers: String): LotteryRecord {
            val parts = numbers.split(" ")
            require(parts.size == 7) { "Invalid numbers format" }

            val reds = parts.take(6).map { it.toInt() }
            val blue = parts.last().toInt()
            
            return LotteryRecord(period, date, reds, blue)
        }
    }
}
