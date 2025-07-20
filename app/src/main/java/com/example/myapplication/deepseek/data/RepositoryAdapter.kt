package com.example.myapplication.deepseek.data

import com.example.myapplication.deepseek.model.LotteryRecord
import kotlinx.coroutines.flow.Flow

class RepositoryAdapter(
    private val v2Repo: com.example.myapplication.deepseek.data.v2.LotteryRepository
) {
    val records: Flow<List<LotteryRecord>> get() = v2Repo.records
    
    suspend fun getRecord(period: String) = v2Repo.getRecord(period)
}