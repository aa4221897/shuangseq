package com.example.lotteryprediction.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiKeyService {
    @GET("api/v1/key")
    suspend fun getApiKey(
        @Header("Authorization") token: String
    ): Response<ApiKeyResponse>
}

data class ApiKeyResponse(
    val apiKey: String,
    val expiresIn: Long // 过期时间(秒)
)