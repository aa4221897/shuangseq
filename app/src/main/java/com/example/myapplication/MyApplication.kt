package com.example.myapplication

import android.app.Application
import com.example.myapplication.deepseek.task.WorkManagerConfig
import com.example.myapplication.deepseek.util.LogUtils

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.d("Application", "Initializing WorkManager")
        WorkManagerConfig.setupAllWorks(this)
    }
}
