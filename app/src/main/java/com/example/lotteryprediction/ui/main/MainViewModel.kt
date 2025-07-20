package com.example.lotteryprediction.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lotteryprediction.domain.PredictionEngine
import com.example.lotteryprediction.domain.PredictionResult
import com.example.lotteryprediction.data.repository.KnowledgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val knowledgeRepo: KnowledgeRepository,
    private val predictionEngine: PredictionEngine
) : ViewModel() {
    private val _recentResults = MutableStateFlow<List<LotteryResult>>(emptyList())
    val recentResults: StateFlow<List<LotteryResult>> = _recentResults

    private val _predictionResult = MutableStateFlow<PredictionResult?>(null)
    val predictionResult: StateFlow<PredictionResult?> = _predictionResult

    init {
        loadRecentResults()
    }

    private fun loadRecentResults() {
        viewModelScope.launch {
            knowledgeRepo.getRecentResults(10).collect { results ->
                _recentResults.value = results
            }
        }
    }

    suspend fun predict() {
        // 使用默认预测方法ID
        val result = predictionEngine.predictNextLottery(1)
        _predictionResult.value = result
    }
}
