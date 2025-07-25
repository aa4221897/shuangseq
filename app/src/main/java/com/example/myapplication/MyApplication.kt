package com.example.lotteryprediction

import android.app.Application
import com.example.lotteryprediction.deepseek.task.WorkManagerConfig
import com.example.lotteryprediction.deepseek.util.LogUtils

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.d("Application", "Initializing WorkManager")
        WorkManagerConfig.setupAllWorks(this)
    }
}
