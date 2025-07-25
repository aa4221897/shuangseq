package com.example.lotteryprediction.deepseek.data

import androidx.room.*
import com.example.lotteryprediction.deepseek.model.LotteryRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface LotteryDao {
    @Insert
    suspend fun insert(record: LotteryRecord)

    @Insert
    suspend fun insertAll(records: List<LotteryRecord>)

    @Query("SELECT * FROM LotteryRecord ORDER BY period DESC LIMIT :limit OFFSET :offset")
    fun getRecordsPaged(limit: Int, offset: Int): Flow<List<LotteryRecord>>

    @Query("SELECT * FROM LotteryRecord WHERE period = :period")
    fun getRecordByPeriod(period: String): Flow<LotteryRecord?>

    @Query("SELECT * FROM LotteryRecord WHERE redNumbers LIKE '%' || :number || '%'")
    fun getRecordsByNumber(number: Int): Flow<List<LotteryRecord>>

    @Query("SELECT COUNT(*) FROM LotteryRecord")
    suspend fun getRecordCount(): Int

    @Query("CREATE INDEX IF NOT EXISTS idx_period ON LotteryRecord(period)")
    suspend fun createPeriodIndex()

    @Query("CREATE INDEX IF NOT EXISTS idx_numbers ON LotteryRecord(redNumbers)")
    suspend fun createNumbersIndex()
}
