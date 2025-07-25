package com.example.lotteryprediction.deepseek.task

import android.content.Context
import com.example.lotteryprediction.deepseek.util.LogUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.LinkedHashMap
import java.util.List

/**
 * 报告历史版本管理
 */
object ReportHistoryManager {
    private const val MAX_HISTORY = 10
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun saveReport(context: Context, content: String) {
        val historyDir = File(context.filesDir, "feature_reports/history")
        historyDir.mkdirs()

        // 保存最新报�?        val latestFile = File(context.filesDir, "feature_reports/latest_report.txt")
        latestFile.writeText(content)

        // 保存历史版本
        val timestamp = dateFormat.format(Date())
        val historyFile = File(historyDir, "report_$timestamp.txt")
        historyFile.writeText(content)

        // 清理旧报�?        cleanupOldReports(historyDir)
    }

    fun getHistoryReports(context: Context): List<Pair<String, String>> {
        val historyDir = File(context.filesDir, "feature_reports/history")
        return historyDir.listFiles()
            ?.sortedByDescending { it.name }
            ?.map { it.name to it.readText() }
            ?: emptyList()
    }

    private fun cleanupOldReports(dir: File) {
        dir.listFiles()
            ?.sortedBy { it.lastModified() }
            ?.takeIf { it.size > MAX_HISTORY }
            ?.let { files ->
                files.take(files.size - MAX_HISTORY).forEach {
                    it.delete()
                    LogUtils.d("ReportHistory", "Deleted old report: ${it.name}")
                }
            }
    }
}
