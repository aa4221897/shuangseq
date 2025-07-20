package com.example.myapplication.deepseek.data

import com.example.myapplication.deepseek.model.LotteryRecord
import kotlinx.coroutines.flow.Flow

interface LotteryRepositoryContract {
    val records: Flow<List<LotteryRecord>>
    suspend fun getRecord(period: String): LotteryRecord?
}