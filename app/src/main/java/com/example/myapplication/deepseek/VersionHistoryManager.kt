package com.example.lotteryprediction.deepseek

import android.content.Context
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VersionHistoryManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("version_history", Context.MODE_PRIVATE)

    suspend fun addVersion(code: Int, name: String, date: String, changes: String) {
        withContext(Dispatchers.IO) {
            prefs.edit().apply {
                putInt("version_${code}_code", code)
                putString("version_${code}_name", name)
                putString("version_${code}_date", date)
                putString("version_${code}_changes", changes)
                apply()
            }
        }
    }

    suspend fun getHistory(): List<VersionHistory> = withContext(Dispatchers.IO) {
        val versions = mutableListOf<VersionHistory>()
        prefs.all.keys.filter { it.startsWith("version_") && it.endsWith("_code") }.forEach { key ->
            val code = key.removePrefix("version_").removeSuffix("_code").toInt()
            versions.add(
                VersionHistory(
                    code = code,
                    versionName = prefs.getString("version_${code}_name", "") ?: "",
                    releaseDate = prefs.getString("version_${code}_date", "") ?: "",
                    changes = prefs.getString("version_${code}_changes", "") ?: ""
                )
            )
        }
        versions.sortedByDescending { it.code }
    }

    suspend fun getHistoryData(): List<LotteryHistory> = withContext(Dispatchers.IO) {
        // 模拟返回彩票历史数据
        listOf(
            LotteryHistory("2023001", listOf(1, 2, 3, 4, 5, 6), "2023-01-01"),
            LotteryHistory("2023002", listOf(7, 8, 9, 10, 11, 12), "2023-01-08")
        )
    }

    data class VersionHistory(
        val code: Int,
        val versionName: String,
        val releaseDate: String,
        val changes: String
    )

    data class LotteryHistory(
        val period: String,
        val numbers: List<Int>,
        val date: String
    )
}
