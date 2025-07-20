package com.example.myapplication.deepseek.data

import androidx.room.*
import com.example.myapplication.deepseek.model.LotteryRecord

@Dao
interface LotteryDao {
    @Insert
    suspend fun insert(record: LotteryRecord)

    @Query("SELECT * FROM LotteryRecord ORDER BY period DESC")
    fun getAllRecords(): List<LotteryRecord>

    @Query("SELECT * FROM LotteryRecord WHERE period = :period")
    fun getRecordByPeriod(period: String): LotteryRecord?
}
