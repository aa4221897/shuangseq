package com.example.myapplication.deepseek.task

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.deepseek.ml.FeatureEngineer
import com.example.myapplication.deepseek.model.LotteryRecord
import com.example.myapplication.deepseek.util.LogUtils
import com.example.myapplication.deepseek.data.AppDatabase
import com.example.myapplication.deepseek.task.ReportHistoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * 特征工程优化检查点
 * 
 * 每月1日凌晨4点自动执行：
 * 1. 评估当前特征有效性
 * 2. 生成优化建议
 * 3. 记录特征性能指标
 */
class FeatureOptimizationCheckpoint(
    context: Context,
    workerParams: WorkerParameters,
    private val database: AppDatabase? = null,
    private val featureEngineer: FeatureEngineer = FeatureEngineer()
) : CoroutineWorker(context, workerParams) {
    private val TAG = "FeatureOptimization"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val dataset = if (database != null) {
            loadDatasetFrom(database)
        } else {
            loadDataset()
        }
        if (dataset.isEmpty()) {
            LogUtils.w(TAG, "No data available for evaluation")
            return@withContext Result.success()
        }

        try {
            // 提取并评估特征
            val features = featureEngineer.extractFeatures(dataset)
            val normalizedFeatures = featureEngineer.normalizeFeatures(features)
            
            // 生成报告
            val report = evaluateFeatures(normalizedFeatures)
            saveReport(report)

            LogUtils.i(TAG, "Feature evaluation completed")
            Result.success()
        } catch (e: Exception) {
            LogUtils.e(TAG, "Feature evaluation failed", e)
            Result.failure()
        }
    }

    private suspend fun loadDataset(): List<LotteryRecord> {
        return loadDatasetFrom(AppDatabase.getInstance(applicationContext))
    }
    
    private suspend fun loadDatasetFrom(database: AppDatabase): List<LotteryRecord> {
        return try {
            val dao = database.lotteryDao()
            val records = dao.getAllRecords()
            if (records.isEmpty()) {
                LogUtils.w(TAG, "No records found in database")
            }
            records
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to load dataset", e)
            emptyList()
        }
    }

    private fun evaluateFeatures(features: List<DoubleArray>): String {
        return buildString {
            appendLine("Feature Evaluation Report")
            appendLine("=======================")
            appendLine("Total features: ${features.size}")
            appendLine("Feature dimensions: ${features.firstOrNull()?.size ?: 0}")
            
            // 计算特征重要性
            val importanceScores = calculateFeatureImportance(features)
            appendLine("\nFeature Importance Scores:")
            importanceScores.forEachIndexed { index, score ->
                appendLine("Feature $index: ${"%.2f".format(score)}")
            }
        }
    }
    
    private fun calculateFeatureImportance(features: List<DoubleArray>): DoubleArray {
        // 简单实现 - 实际项目应使用专业ML库
        val dims = features.firstOrNull()?.size ?: return doubleArrayOf()
        val scores = DoubleArray(dims) { 0.0 }
        
        features.forEach { feature ->
            feature.forEachIndexed { i, value ->
                scores[i] += abs(value)
            }
        }
        
        val maxScore = scores.maxOrNull() ?: 1.0
        return scores.map { it / maxScore }.toDoubleArray()
    }

    private fun saveReport(report: String) {
        try {
            ReportHistoryManager.saveReport(applicationContext, report)
            LogUtils.i(TAG, "Report saved with history version")
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to save report", e)
        }
    }
}
