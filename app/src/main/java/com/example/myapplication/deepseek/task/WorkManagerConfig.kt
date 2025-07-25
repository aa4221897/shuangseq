package com.example.lotteryprediction.deepseek.task

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.lotteryprediction.deepseek.util.LogUtils
import java.util.concurrent.TimeUnit

/**
 * WorkManager配置�? * 
 * 功能�? * 1. 配置所有后台任�? * 2. 设置执行频率和约�? * 3. 管理任务生命周期
 */
object WorkManagerConfig {
    private const val FEATURE_OPTIMIZATION_WORK_NAME = "feature_optimization_work"
    private val statusListeners = mutableListOf<(String, Boolean) -> Unit>()
    
    /**
     * 初始化所有后台任�?     */
    fun setupAllWorks(context: Context) {
        setupFeatureOptimizationWork(context)
        monitorWorkStatus(context)
    }
    
    /**
     * 添加任务状态监听器
     */
    fun addStatusListener(listener: (String, Boolean) -> Unit) {
        statusListeners.add(listener)
    }

    /**
     * 配置特征优化任务
     */
    private fun setupFeatureOptimizationWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<FeatureOptimizationCheckpoint>(
            30, // 30天间�?            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            FEATURE_OPTIMIZATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    /**
     * 取消所有后台任�?     */
    fun cancelAllWorks(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }
    
    /**
     * 监控任务执行状�?     */
    private fun monitorWorkStatus(context: Context) {
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(FEATURE_OPTIMIZATION_WORK_NAME)
            .observeForever { workInfos ->
                workInfos.forEach { info ->
                    val isRunning = info.state == WorkInfo.State.RUNNING
                    statusListeners.forEach { it(FEATURE_OPTIMIZATION_WORK_NAME, isRunning) }
                    LogUtils.d("WorkManager", 
                        "Feature optimization work state: ${info.state}, isRunning: $isRunning")
                }
            }
    }
}
