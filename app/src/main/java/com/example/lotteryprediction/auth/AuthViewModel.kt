package com.example.lotteryprediction.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lotteryprediction.network.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private var lastRequestTime = 0L
    private const val MIN_REQUEST_INTERVAL = 60_000L // 1分钟间隔
    
    fun requestSMSCode(phone: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // 验证手机号格式
                if (!phone.matches(Regex("^1[3-9]\\d{9}$"))) {
                    _uiState.value = AuthUiState.Error("手机号格式不正确")
                    return@launch
                }
                
                // 频率限制检查
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastRequestTime < MIN_REQUEST_INTERVAL) {
                    _uiState.value = AuthUiState.Error("请求过于频繁，请稍后再试")
                    return@launch
                }
                
                authService.requestSMSCode(phone)
                lastRequestTime = currentTime
                _uiState.value = AuthUiState.CodeSent
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "请求验证码失败")
            }
        }
    }

    fun verifyCode(phone: String, code: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val token = authService.verifyCode(phone, code)
                tokenManager.saveToken(token)
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "验证码错误")
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object CodeSent : AuthUiState()
    object Success : AuthUiState()
    class Error(val message: String) : AuthUiState()
}