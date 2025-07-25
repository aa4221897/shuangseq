package com.example.lotteryprediction.deepseek.data

import com.example.lotteryprediction.deepseek.model.LotteryRecord
import kotlinx.coroutines.flow.Flow

class RepositoryAdapter(
    private val v2Repo: com.example.lotteryprediction.deepseek.data.v2.LotteryRepository
) {
    val records: Flow<List<LotteryRecord>> get() = v2Repo.records
    
    suspend fun getRecord(period: String) = v2Repo.getRecord(period)
}
