package com.example.lotteryprediction.deepseek.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lotteryprediction.deepseek.model.CoverageResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LotteryViewModel(
    private val logger: Logger = AndroidLogger()
) : ViewModel() {
    private val _uiState = MutableStateFlow<LotteryUiState>(LotteryUiState.Loading)
    val uiState: StateFlow<LotteryUiState> = _uiState
    private var lastRefreshTime = 0L
    private var cachedPrediction: PredictionResult? = null

    private var retryCount = 0
    private val maxRetries = 3
    
    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = LotteryUiState.Loading
            val startTime = System.currentTimeMillis()
            logger.d("LotteryViewModel", "开始加载数�?forceRefresh=$forceRefresh)")
            
            try {
                // 检查缓存有效�?(5分钟缓存)
                val shouldUseCache = !forceRefresh && 
                    cachedPrediction != null && 
                    System.currentTimeMillis() - lastRefreshTime < 5 * 60 * 1000
                
                if (shouldUseCache) {
                    logger.d("LotteryViewModel", "使用缓存数据")
                    _uiState.value = LotteryUiState.Success(cachedPrediction!!)
                    return@launch
                }
                
                // 实现实际数据加载逻辑
                val prediction = try {
                    // TODO: 替换为实际数据加载逻辑
                    PredictionResult(
                        killers = listOf(1, 2, 3),
                        danma = listOf(4, 5),
                        groups = listOf(
                            listOf(6, 7, 8),
                            listOf(9, 10, 11),
                            listOf(12, 13)
                        ),
                        validation = CoverageResult(
                            isFullCoverage = false,
                            totalCovered = 4,
                            totalPossible = 6
                        )
                    )
                } catch (e: Exception) {
                    logger.e("LotteryViewModel", "数据解析失败", e)
                    throw DataParseException("数据解析失败", e)
                }
                
                // 更新缓存
                cachedPrediction = prediction
                lastRefreshTime = System.currentTimeMillis()
                
                val duration = System.currentTimeMillis() - startTime
                logger.logPerformance(
                    "DATA_LOAD",
                    duration,
                    mapOf(
                        "forceRefresh" to forceRefresh,
                        "itemsCount" to (prediction.killers.size + prediction.danma.size + prediction.groups.sumOf { it.size }),
                        "status" to "success"
                    )
                )
                _uiState.value = LotteryUiState.Success(prediction)
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is DataParseException -> "数据解析错误: ${e.message}"
                    is NetworkException -> "网络错误: ${e.message}"
                    is IllegalStateException -> "状态错�? ${e.message}"
                    else -> "未知错误: ${e.message}"
                }
                
                logger.logPerformance(
                    "DATA_LOAD",
                    System.currentTimeMillis() - startTime,
                    mapOf(
                        "forceRefresh" to forceRefresh,
                        "status" to "failed",
                        "error" to errorMessage
                    )
                )
                
                if (e is NetworkException && retryCount++ < maxRetries) {
                    val delay = 1000L * retryCount
                    logger.w(
                        "LotteryViewModel", 
                        "网络错误�?{delay}ms后重�?$retryCount/$maxRetries)"
                    )
                    _uiState.value = LotteryUiState.Retrying(delay, retryCount, maxRetries)
                    kotlinx.coroutines.delay(delay)
                    loadData(forceRefresh)
                } else {
                    logger.e(
                        "LotteryViewModel", 
                        "数据加载失败: $errorMessage", 
                        e
                    )
                    _uiState.value = LotteryUiState.Error(errorMessage)
                }
            }
        }
    }

    sealed class LotteryUiState {
        object Loading : LotteryUiState()
        data class Success(val prediction: PredictionResult) : LotteryUiState()
        data class Error(val message: String) : LotteryUiState()
        data class Retrying(
            val delayMs: Long,
            val currentRetry: Int,
            val maxRetries: Int
        ) : LotteryUiState()
    }

    sealed class LotteryUiState {
        object Loading : LotteryUiState()
        data class Success(val prediction: PredictionResult) : LotteryUiState()
        data class Error(val message: String) : LotteryUiState()
    }

    data class PredictionResult(
        val killers: List<Int>,
        val danma: List<Int>,
        val groups: List<List<Int>>,
        val validation: CoverageResult
    )
}
