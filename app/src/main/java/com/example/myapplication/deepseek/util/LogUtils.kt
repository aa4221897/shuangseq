package com.example.lotteryprediction.deepseek.util

import android.util.Log

object LogUtils : Logger {
    private const val GLOBAL_TAG = "LotteryPredict"
    var debugEnabled = true // 临时设置为true，后续再修复BuildConfig引用

    override fun d(tag: String, message: String) {
        if (debugEnabled) Log.d("$GLOBAL_TAG:$tag", message)
    }

    fun i(tag: String, message: String) {
        Log.i("$GLOBAL_TAG:$tag", message)
    }

    fun w(tag: String, message: String) {
        Log.w("$GLOBAL_TAG:$tag", message)
    }

    override fun e(tag: String, message: String) {
        Log.e("$GLOBAL_TAG:$tag", message)
    }
    
    fun e(tag: String, message: String, e: Throwable) {
        Log.e("$GLOBAL_TAG:$tag", message, e)
    }

    fun logPerformance(tag: String, operation: String, timeMs: Long) {
        if (debugEnabled) {
            Log.d("$GLOBAL_TAG:PERF:$tag", "$operation took ${timeMs}ms")
        }
    }

    inline fun <T> measureTime(tag: String, operation: String, block: () -> T): T {
        val start = System.currentTimeMillis()
        return try {
            block()
        } finally {
            logPerformance(tag, operation, System.currentTimeMillis() - start)
        }
    }
}
