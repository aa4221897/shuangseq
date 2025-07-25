package com.example.lotteryprediction

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LotteryPredictionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化安全模�?
        SecurityInitializer.init(this)
    }
}
