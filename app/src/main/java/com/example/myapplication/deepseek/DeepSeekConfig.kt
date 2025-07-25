package com.example.lotteryprediction.deepseek

import android.content.Context
import android.content.SharedPreferences

class DeepSeekConfig(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DeepSeekPrefs", Context.MODE_PRIVATE)

    var apiKey: String
        get() = prefs.getString("api_key", "") ?: ""
        set(value) = prefs.edit().putString("api_key", value).apply()

    var endpoint: String
        get() = prefs.getString("endpoint", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1"
        set(value) = prefs.edit().putString("endpoint", value).apply()

    var enablePrediction: Boolean
        get() = prefs.getBoolean("enable_prediction", true)
        set(value) = prefs.edit().putBoolean("enable_prediction", value).apply()

    companion object {
        const val DEFAULT_TIMEOUT = 30L // seconds
    }
}
