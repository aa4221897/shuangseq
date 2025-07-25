package com.example.lotteryprediction.deepseek.data.v2

import com.example.lotteryprediction.deepseek.data.LotteryDao
import com.example.lotteryprediction.deepseek.model.LotteryRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LotteryRepository(private val dao: LotteryDao) : LotteryRepositoryContract {
    val records: Flow<List<LotteryRecord>> = dao.observeAll()
        .flowOn(Dispatchers.IO)
        .catch { emit(emptyList()) }

    suspend fun getRecord(period: String) = 
        withContext(Dispatchers.IO) {
            dao.getRecordByPeriod(period)
        }
}
