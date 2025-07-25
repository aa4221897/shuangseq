package com.example.lotteryprediction.deepseek.util

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

    fun logNetworkRequest(
        url: String,
        method: String,
        headers: Map<String, List<String>>,
        body: String?
    ) {
        Log.d("Network", """
            Request:
            URL: $url
            Method: $method
            Headers: ${headers.entries.joinToString { "${it.key}=${it.value}" }}
            Body: ${body?.take(200) ?: "null"}
        """.trimIndent())
    }

    fun logNetworkResponse(
        url: String,
        statusCode: Int,
        headers: Map<String, List<String>>,
        body: String,
        durationMs: Long
    ) {
        Log.d("Network", """
            Response:
            URL: $url
            Status: $statusCode
            Duration: ${durationMs}ms
            Headers: ${headers.entries.joinToString { "${it.key}=${it.value}" }}
            Body: ${body.take(200)}
        """.trimIndent())
    }

    fun logSslHandshake(
        url: String,
        success: Boolean,
        errorMessage: String?
    ) {
        Log.w("SSL", """
            SSL Handshake:
            URL: $url
            Result: ${if (success) "SUCCESS" else "FAILED"}
            ${errorMessage?.let { "Error: $it" } ?: ""}
        """.trimIndent())
    }

    private fun formatMetadata(metadata: Map<String, Any>): String {
        return if (metadata.isEmpty()) "" else "| ${metadata.entries.joinToString { "${it.key}=${it.value}" }}"
    }
}
