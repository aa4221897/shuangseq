package com.example.lotteryprediction.deepseek.api

data class OptimizeRequest(
    val algorithm: String,
    val constraints: Map<String, String>
)

data class OptimizeResponse(
    val optimized_algorithm: String,
    val status: Int
)
