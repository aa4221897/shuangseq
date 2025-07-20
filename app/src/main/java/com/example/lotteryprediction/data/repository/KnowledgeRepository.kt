package com.example.lotteryprediction.data.repository

import com.example.lotteryprediction.data.local.LotteryDao
import com.example.lotteryprediction.data.model.LotteryResult
import com.example.lotteryprediction.data.model.PredictionMethod
import com.example.lotteryprediction.data.remote.KnowledgeUpdateService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KnowledgeRepository @Inject constructor(
    private val localDao: LotteryDao,
    private val remoteService: KnowledgeUpdateService
) {
    // 本地数据访问
    fun getRecentResults(count: Int): Flow<List<LotteryResult>> = localDao.getRecentResults(count)
    
    suspend fun getMethodById(id: Int): PredictionMethod? = localDao.getMethodById(id)

    // 远程同步
    suspend fun syncKnowledgeBase() {
        val localVersion = localDao.getMaxResultVersion() ?: 0
        val update = remoteService.getUpdates(localVersion)
        
        if (update.results.isNotEmpty()) {
            localDao.insertResults(update.results)
        }
        
        if (update.methods.isNotEmpty()) {
            localDao.insertMethods(update.methods)
        }
    }
}