package com.example.lotteryprediction.deepseek.util

interface Logger {
    fun d(tag: String, message: String, metadata: Map<String, Any> = emptyMap())
    fun e(tag: String, message: String, throwable: Throwable? = null, metadata: Map<String, Any> = emptyMap())
    fun logPerformance(eventName: String, durationMs: Long, metadata: Map<String, Any> = emptyMap())
    
    fun logNetworkRequest(
        url: String,
        method: String,
        headers: Map<String, List<String>>,
        body: String?
    )
    
    fun logNetworkResponse(
        url: String,
        statusCode: Int,
        headers: Map<String, List<String>>,
        body: String,
        durationMs: Long
    )
    
    fun logSslHandshake(
        url: String,
        success: Boolean,
        errorMessage: String?
    )
}
