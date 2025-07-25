package com.example.lotteryprediction.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "lottery_results")
data class LotteryResult(
    @PrimaryKey val drawDate: Date,
    val redBalls: List<Int>,
    val blueBall: Int,
    val version: Int = 0
) {
    fun toRedBallString(): String = redBalls.joinToString(",") { "%02d".format(it) }
}
