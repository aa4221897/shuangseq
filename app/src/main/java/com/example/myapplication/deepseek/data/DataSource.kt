package com.example.lotteryprediction.deepseek.data

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 双色球数据获取接�? */
interface DataSource {
    @GET("api/lottery/history")
    suspend fun getHistory(
        @Query("lottery_id") id: String = "ssq",
        @Query("count") count: Int = 100
    ): List<LotteryRecord>

    @GET("api/lottery/trend")
    suspend fun getTrendChart(
        @Query("lottery_id") id: String = "ssq",
        @Query("periods") periods: Int = 30
    ): TrendData
}

data class TrendData(
    val periods: List<Int>,
    val redTrend: Map<Int, List<Int>>, // 号码->出现期数列表
    val blueTrend: Map<Int, List<Int>>,
    val chartImageUrl: String // 走势图URL
)
