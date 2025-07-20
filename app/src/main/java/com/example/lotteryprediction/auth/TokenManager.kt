package com.example.lotteryprediction.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(
            KeyGenParameterSpec.Builder(
                "_lottery_prediction_key",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private const val TOKEN_EXPIRY_KEY = "token_expiry"
    private const val TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L // 7天有效期
    private const val API_KEY = "api_key"
    
    // Token管理
    fun saveToken(token: String) {
        val expiryTime = System.currentTimeMillis() + TOKEN_VALIDITY_MS
        sharedPreferences.edit()
            .putString("user_token", token)
            .putLong(TOKEN_EXPIRY_KEY, expiryTime)
            .apply()
    }

    fun getToken(): String? {
        val expiryTime = sharedPreferences.getLong(TOKEN_EXPIRY_KEY, 0L)
        if (expiryTime < System.currentTimeMillis()) {
            clearToken()
            return null
        }
        return sharedPreferences.getString("user_token", null)
    }

    fun clearToken() {
        sharedPreferences.edit()
            .remove("user_token")
            .remove(TOKEN_EXPIRY_KEY)
            .apply()
    }
    
    // API密钥管理
    private const val API_KEY_EXPIRY = "api_key_expiry"
    
    fun saveApiKey(apiKey: String, expiresIn: Long = 7 * 24 * 60 * 60) {
        val expiryTime = System.currentTimeMillis() + expiresIn * 1000
        sharedPreferences.edit()
            .putString(API_KEY, apiKey)
            .putLong(API_KEY_EXPIRY, expiryTime)
            .apply()
    }
    
    fun getApiKey(): String? {
        val expiryTime = sharedPreferences.getLong(API_KEY_EXPIRY, 0L)
        return if (expiryTime > System.currentTimeMillis()) {
            sharedPreferences.getString(API_KEY, null)
        } else {
            null
        }
    }
    
    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(API_KEY)
            .remove(API_KEY_EXPIRY)
            .apply()
    }
    
    fun isApiKeyExpired(): Boolean {
        val expiryTime = sharedPreferences.getLong(API_KEY_EXPIRY, 0L)
        return expiryTime < System.currentTimeMillis()
    }
}