package com.example.lotteryprediction.deepseek.task

import android.content.Context
import com.example.lotteryprediction.deepseek.util.LogUtils

object ReportComparator {
    private const val TAG = "ReportComparator"

    fun compareReports(report1: String, report2: String): String {
        return try {
            val lines1 = report1.split("\n")
            val lines2 = report2.split("\n")
            
            val maxLength = maxOf(lines1.size, lines2.size)
            val diffLines = mutableListOf<String>()
            
            for (i in 0 until maxLength) {
                val line1 = lines1.getOrNull(i) ?: ""
                val line2 = lines2.getOrNull(i) ?: ""
                
                if (line1 != line2) {
                    diffLines.add("Line ${i + 1}:")
                    diffLines.add("  - $line1")
                    diffLines.add("  + $line2")
                }
            }
            
            if (diffLines.isEmpty()) {
                "No differences found"
            } else {
                "Found ${diffLines.size / 3} differences:\n" + diffLines.joinToString("\n")
            }
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to compare reports", e)
            "Comparison failed: ${e.message}"
        }
    }

    fun getFormattedDifferences(context: Context, report1Id: String, report2Id: String): String {
        val reports = ReportHistoryManager.getHistoryReports(context)
        val report1 = reports.find { it.first == report1Id }?.second ?: return "Report 1 not found"
        val report2 = reports.find { it.first == report2Id }?.second ?: return "Report 2 not found"
        
        return compareReports(report1, report2)
    }
}
