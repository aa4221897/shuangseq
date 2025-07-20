package com.example.myapplication.deepseek.data

import java.util.Date

/**
 * 双色球开奖记录数据模型
 */
data class LotteryRecord(
    val id: String,
    val date: Date,
    val redNumbers: List<Int>,
    val blueNumber: Int,
    val period: Int
) {
    init {
        require(redNumbers.size == 6) { "红球数量必须为6个" }
        require(redNumbers.all { it in 1..33 }) { "红球号码必须在1-33范围内" }
        require(blueNumber in 1..16) { "蓝球号码必须在1-16范围内" }
    }
}