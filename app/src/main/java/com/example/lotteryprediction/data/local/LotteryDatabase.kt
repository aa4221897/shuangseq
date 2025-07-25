package com.example.lotteryprediction.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lotteryprediction.data.model.LotteryResult
import com.example.lotteryprediction.data.model.PredictionMethod

@Database(
    entities = [LotteryResult::class, PredictionMethod::class],
    version = 1,
    exportSchema = false
)
abstract class LotteryDatabase : RoomDatabase() {
    abstract fun lotteryDao(): LotteryDao

    companion object {
        @Volatile
        private var INSTANCE: LotteryDatabase? = null

        fun getInstance(context: Context): LotteryDatabase {
            return INSTANCE ?: synchronized(this) {
                // 使用applicationContext是必要的，因为Room需要应用级别的Context
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LotteryDatabase::class.java,
                    "lottery_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
