package com.example.lotteryprediction.data.remote

import com.example.lotteryprediction.BuildConfig
import com.example.lotteryprediction.data.model.KnowledgeUpdate
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject

class KnowledgeUpdateService @Inject constructor(
    private val api: KnowledgeUpdateApi
) {
    suspend fun getUpdates(localVersion: Int): KnowledgeUpdate {
        val response = api.getUpdates(
            "Bearer ${BuildConfig.KNOWLEDGE_API_KEY}",
            localVersion
        )
        return response.body() ?: throw Exception("Failed to get updates")
    }
}

interface KnowledgeUpdateApi {
    @GET("knowledge/update")
    suspend fun getUpdates(
        @Header("Authorization") auth: String,
        @Query("version") version: Int
    ): Response<KnowledgeUpdate>
}

data class KnowledgeUpdate(
    val results: List<LotteryResult>,
    val methods: List<PredictionMethod>,
    val newVersion: Int
)
