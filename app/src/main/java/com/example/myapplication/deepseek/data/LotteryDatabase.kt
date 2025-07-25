package com.example.lotteryprediction.deepseek.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lotteryprediction.deepseek.model.LotteryRecord

@Database(
    entities = [LotteryRecord::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lotteryDao(): LotteryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // 使用applicationContext是必要的，因为Room需要应用级别的Context
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lottery_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 在新线程执行索引创建
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).lotteryDao().createPeriodIndex()
                            getInstance(context).lotteryDao().createNumbersIndex()
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
