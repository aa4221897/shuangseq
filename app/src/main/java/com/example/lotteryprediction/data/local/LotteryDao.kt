package com.example.lotteryprediction.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lotteryprediction.data.model.LotteryResult
import com.example.lotteryprediction.data.model.PredictionMethod
import kotlinx.coroutines.flow.Flow

@Dao
interface LotteryDao {
    // 开奖结果操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<LotteryResult>)

    @Query("SELECT * FROM lottery_results ORDER BY drawDate DESC LIMIT :count")
    fun getRecentResults(count: Int): Flow<List<LotteryResult>>

    @Query("SELECT MAX(version) FROM lottery_results")
    suspend fun getMaxResultVersion(): Int?

    // 预测方法操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMethods(methods: List<PredictionMethod>)

    @Query("SELECT * FROM prediction_methods WHERE id = :id")
    suspend fun getMethodById(id: Int): PredictionMethod?

    @Query("SELECT MAX(version) FROM prediction_methods")
    suspend fun getMaxMethodVersion(): Int?
}