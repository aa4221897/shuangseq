package com.example.lotteryprediction

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.deepseek.sdk.DeepSeekClient
import com.example.lotteryprediction.network.ApiKeyService
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LotteryPredictionApp : Application() {
    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var apiKeyService: ApiKeyService
    
    private val appScope = CoroutineScope(Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化安全模�?
        SecurityInitializer.init(this)
        
        // 初始化DeepSeek SDK
        initializeDeepSeek()
        
        // 监听应用生命周期，定期检查API密钥
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                checkAndRefreshApiKey()
            }
        })
        
        // 添加合规声明
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is MainActivity) {
                    activity.showDisclaimer()
                }
            }
            // 其他生命周期方法省略...
        })
    }
    
    private fun initializeDeepSeek() {
        val apiKey = tokenManager.getApiKey() ?: run {
            fetchApiKeyFromBackend()?.also { tokenManager.saveApiKey(it) }
        }
        
        apiKey?.let { DeepSeekClient.initialize(this, it) }
    }
    
    private fun checkAndRefreshApiKey() {
        if (tokenManager.isApiKeyExpired()) {
            appScope.launch {
                fetchApiKeyFromBackend()?.let { 
                    tokenManager.saveApiKey(it)
                    initializeDeepSeek()
                }
            }
        }
    }
    
    private suspend fun fetchApiKeyFromBackend(): String? {
        return try {
            val response = apiKeyService.getApiKey(tokenManager.getToken() ?: "")
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveApiKey(it.apiKey, it.expiresIn)
                    it.apiKey
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
