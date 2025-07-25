package com.example.lotteryprediction.deepseek

import android.os.SystemClock
import android.util.Log

class PerformanceMonitor {
    companion object {
        private const val TAG = "PerformanceMonitor"
        private val timings = mutableMapOf<String, Long>()

        fun startTrace(tag: String) {
            timings[tag] = SystemClock.elapsedRealtime()
            Log.d(TAG, "Started trace: $tag")
        }

        fun endTrace(tag: String) {
            val startTime = timings[tag] ?: return
            val duration = SystemClock.elapsedRealtime() - startTime
            Log.d(TAG, "Trace '$tag' took $duration ms")
            timings.remove(tag)
        }

        fun logMemoryUsage() {
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            Log.d(TAG, "Memory usage: $usedMemory MB")
        }
    }
}
