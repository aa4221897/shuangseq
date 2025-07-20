package com.example.myapplication.deepseek.data

import android.content.Context
import com.example.myapplication.deepseek.model.LotteryRecord
import com.example.myapplication.deepseek.util.LogUtils
import com.example.myapplication.deepseek.util.NetworkUtils
import java.util.Date

/**
 * 数据收集服务
 * 
 * 功能：
 * 1. 收集历史开奖数据
 * 2. 数据清洗和验证
 * 3. 本地/云端存储
 * 
 * 使用示例：
 * ```
 * val service = DataCollectionService(context)
 * service.addRecords(newRecords)
 * ```
 */
class DataCollectionService(private val context: Context) {
    private val TAG = "DataCollection"
    private val localDataSource = LocalDataSource(context)
    private val remoteDataSource = RemoteDataSource()
    private val pendingRecords = mutableListOf<LotteryRecord>()
    private var lastUploadTime = 0L
    private val UPLOAD_INTERVAL = 60 * 1000L // 1分钟上传间隔
    private val MAX_BATCH_SIZE = 50 // 最大批量上传数量

    /**
     * 添加新记录
     */
    fun addRecords(records: List<LotteryRecord>): Boolean {
        return try {
            val validRecords = records.filter { validateRecord(it) }
            val qualityScore = calculateDataQuality(validRecords)
            
            LogUtils.i(TAG, 
                "数据质量评分: ${"%.2f".format(qualityScore)}, " +
                "有效记录: ${validRecords.size}/${records.size}"
            )
            
            // 本地存储压缩数据
            localDataSource.saveRecords(compressRecords(validRecords))
            
            // 网络可用时立即上传或加入待上传队列
            if (NetworkUtils.isConnected(context)) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUploadTime > UPLOAD_INTERVAL || pendingRecords.size >= MAX_BATCH_SIZE) {
                    uploadPendingRecords()
                }
            } else {
                pendingRecords.addAll(validRecords)
                LogUtils.w(TAG, "Network unavailable, ${validRecords.size} records queued")
            }
            
            true
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to add records", e)
            false
        }
    }
    
    private fun uploadPendingRecords() {
        if (pendingRecords.isEmpty()) return
        
        val recordsToUpload = pendingRecords.take(MAX_BATCH_SIZE)
        remoteDataSource.uploadRecords(recordsToUpload)
        pendingRecords.removeAll(recordsToUpload)
        lastUploadTime = System.currentTimeMillis()
        LogUtils.d(TAG, "Uploaded ${recordsToUpload.size} records, ${pendingRecords.size} remaining")
    }
    
    private fun compressRecords(records: List<LotteryRecord>): ByteArray {
        // 简单压缩实现 - 实际项目应使用专业压缩库
        return records.joinToString("|") { record ->
            "${record.redNumbers.joinToString(",")}:${record.blueNumber}:${record.date.time}"
        }.toByteArray()
    }

    private fun validateRecord(record: LotteryRecord): Boolean {
        return record.run {
            // 基础验证
            val validRedCount = redNumbers.size == 6
            val sorted = redNumbers == redNumbers.sorted()
            val redRangeValid = redNumbers.all { it in 1..33 }
            val blueValid = blueNumber in 1..16
            val dateValid = date.time > 0 && date.time <= System.currentTimeMillis()
            
            // 高级验证
            val duplicateNumbers = redNumbers.distinct().size != redNumbers.size
            val consecutiveNumbers = redNumbers.sorted().windowed(2).any { (a, b) -> b - a == 1 }
            val sumValid = redNumbers.sum() in 21..183 // 6-33的最小和最大可能值
            
            if (!validRedCount) LogUtils.w(TAG, "Invalid red numbers count: ${redNumbers.size}")
            if (!sorted) LogUtils.w(TAG, "Red numbers not sorted: $redNumbers")
            if (!redRangeValid) LogUtils.w(TAG, "Red number out of range: $redNumbers")
            if (!blueValid) LogUtils.w(TAG, "Blue number out of range: $blueNumber")
            if (!dateValid) LogUtils.w(TAG, "Invalid date: ${date.time}")
            if (duplicateNumbers) LogUtils.w(TAG, "Duplicate numbers found: $redNumbers")
            
            validRedCount && sorted && redRangeValid && blueValid && dateValid &&
            !duplicateNumbers && consecutiveNumbers && sumValid
        }
    }
    
    fun calculateDataQuality(records: List<LotteryRecord>): Double {
        if (records.isEmpty()) return 0.0
        
        val qualityScores = records.map { record ->
            var score = 0
            if (record.redNumbers.size == 6) score += 20
            if (record.redNumbers == record.redNumbers.sorted()) score += 20
            if (record.redNumbers.all { it in 1..33 }) score += 20
            if (record.blueNumber in 1..16) score += 20
            if (record.date.time > 0) score += 20
            score
        }
        
        return qualityScores.average() / 100.0
    }

    @Suppress("UNUSED_PARAMETER")
    private class LocalDataSource(context: Context) {
        fun saveRecords(records: List<LotteryRecord>) {
            // 实现本地存储逻辑
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private class RemoteDataSource {
        fun uploadRecords(records: List<LotteryRecord>) {
            // 实现云端同步逻辑
        }
    }
}
