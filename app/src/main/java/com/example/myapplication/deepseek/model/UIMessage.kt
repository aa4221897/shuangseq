package com.example.lotteryprediction.deepseek.model

data class UIMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
