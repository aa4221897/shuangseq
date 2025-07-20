package com.example.myapplication.deepseek.util

interface Logger {
    fun d(tag: String, message: String, metadata: Map<String, Any> = emptyMap())
    fun e(tag: String, message: String, throwable: Throwable? = null, metadata: Map<String, Any> = emptyMap())
    fun logPerformance(eventName: String, durationMs: Long, metadata: Map<String, Any> = emptyMap())
}