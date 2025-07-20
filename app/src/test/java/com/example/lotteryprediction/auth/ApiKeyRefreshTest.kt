package com.example.lotteryprediction.auth

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lotteryprediction.LotteryPredictionApp
import com.example.lotteryprediction.network.ApiKeyService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ApiKeyRefreshTest {
    private lateinit var tokenManager: TokenManager
    private lateinit var mockApiKeyService: ApiKeyService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<LotteryPredictionApp>()
        tokenManager = TokenManager(context)
        mockApiKeyService = mock(ApiKeyService::class.java)
    }

    @Test
    fun testApiKeyAutoRefresh() = runBlocking {
        // 模拟API密钥过期
        tokenManager.saveApiKey("expired_key", -1)
        
        // 模拟服务返回新密钥
        `when`(mockApiKeyService.getApiKey(any())).thenReturn(
            Response.success(ApiKeyResponse("new_key", 3600))
        )

        // 触发刷新
        val app = LotteryPredictionApp()
        app.apiKeyService = mockApiKeyService
        app.checkAndRefreshApiKey()

        // 验证密钥已更新
        assertEquals("new_key", tokenManager.getApiKey())
    }

    @Test
    fun testApiKeyNotRefreshedWhenValid() = runBlocking {
        // 设置有效密钥
        tokenManager.saveApiKey("valid_key", 3600)
        
        // 验证未调用刷新服务
        verify(mockApiKeyService, never()).getApiKey(any())
    }
}