package com.example.lotteryprediction.deepseek.data

import com.example.lotteryprediction.deepseek.model.LotteryRecord
import kotlinx.coroutines.flow.Flow

interface LotteryRepositoryContract {
    val records: Flow<List<LotteryRecord>>
    suspend fun getRecord(period: String): LotteryRecord?
}
