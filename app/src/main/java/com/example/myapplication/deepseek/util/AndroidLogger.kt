package com.example.myapplication.deepseek.util

import android.util.Log

class AndroidLogger : Logger {
    override fun d(tag: String, message: String, metadata: Map<String, Any>) {
        Log.d(tag, "$message ${formatMetadata(metadata)}")
    }

    override fun e(tag: String, message: String, throwable: Throwable?, metadata: Map<String, Any>) {
        Log.e(tag, "$message ${formatMetadata(metadata)}", throwable)
    }

    override fun logPerformance(eventName: String, durationMs: Long, metadata: Map<String, Any>) {
        Log.i("Performance", "$eventName took ${durationMs}ms ${formatMetadata(metadata)}")
    }

    private fun formatMetadata(metadata: Map<String, Any>): String {
        return if (metadata.isEmpty()) "" else "| ${metadata.entries.joinToString { "${it.key}=${it.value}" }}"
    }
}