package com.example.myapplication.deepseek.data

import com.example.myapplication.deepseek.model.LotteryRecord
import com.example.myapplication.deepseek.util.LogUtils
import java.util.ArrayList
import java.util.Date
import java.util.List
import java.util.concurrent.ConcurrentHashMap

interface LotteryDataSource {
    suspend fun getHistoryData(startPeriod: String, endPeriod: String): List<LotteryRecord>
    suspend fun getLatestData(): LotteryRecord?
}

class LotteryRepository(
    private val localDataSource: LotteryDataSource,
    private val remoteDataSource: LotteryDataSource
) {
    private val tag = "LotteryRepo"
    private val cache = mutableMapOf<String, List<LotteryRecord>>()

    suspend fun getHistoryData(
        startPeriod: String, 
        endPeriod: String,
        forceRefresh: Boolean = false
    ): List<LotteryRecord> {
        return LogUtils.measureTime(tag, "getHistoryData") {
            val cacheKey = "$startPeriod-$endPeriod"
            if (!forceRefresh && cache.containsKey(cacheKey)) {
                return@measureTime cache[cacheKey]!!
            }

            try {
                val remoteData = remoteDataSource.getHistoryData(startPeriod, endPeriod)
                if (remoteData.isNotEmpty()) {
                    cache[cacheKey] = remoteData
                    return@measureTime remoteData
                }
            } catch (e: Exception) {
                LogUtils.e(tag, "Failed to fetch remote data", e)
            }

            val localData = localDataSource.getHistoryData(startPeriod, endPeriod)
            if (localData.isNotEmpty()) {
                cache[cacheKey] = localData
                return@measureTime localData
            }

            throw DataUnavailableException("No data available for $startPeriod-$endPeriod")
        }
    }

    suspend fun getLatestData(forceRefresh: Boolean = false): LotteryRecord {
        return LogUtils.measureTime(tag, "getLatestData") {
            try {
                if (forceRefresh) {
                    return@measureTime remoteDataSource.getLatestData()
                        ?: throw DataUnavailableException("No latest data")
                }

                localDataSource.getLatestData()?.let { return@measureTime it }
                remoteDataSource.getLatestData()?.let {
                    return@measureTime it
                }

                throw DataUnavailableException("No data available")
            } catch (e: Exception) {
                LogUtils.e(tag, "Failed to get latest data", e)
                throw e
            }
        }
    }

    class DataUnavailableException(message: String) : Exception(message)
}
