package com.example.lotteryprediction.deepseek.ui

import com.example.lotteryprediction.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.lotteryprediction.databinding.FragmentPredictionResultBinding

class PredictionResultFragment : Fragment() {
    private var _binding: FragmentPredictionResultBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment view not initialized" }
    private lateinit var viewModel: LotteryViewModel
    private val logger by lazy { AndroidLogger() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictionResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化视图逻辑

        viewModel = ViewModelProvider(requireActivity()).get(LotteryViewModel::class.java)
        setupObservers()
        setupListeners()
        viewModel.loadData()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is LotteryViewModel.LotteryUiState.Loading -> {
                        logger.d("PredictionResult", "显示加载状�?)
                        binding.progressBar.visibility = View.VISIBLE
                        binding.contentGroup.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                    }
                    is LotteryViewModel.LotteryUiState.Success -> {
                        logger.d("PredictionResult", "开始更新UI")
                        val startTime = System.currentTimeMillis()
                        binding.progressBar.visibility = View.GONE
                        binding.contentGroup.visibility = View.VISIBLE
                        binding.errorText.visibility = View.GONE
                        updateUI(state)
                        val duration = System.currentTimeMillis() - startTime
                        logger.logPerformance(
                            "UI_UPDATE",
                            duration,
                            mapOf(
                                "items" to (state.prediction.killers.size + 
                                    state.prediction.danma.size + 
                                    state.prediction.groups.sumOf { it.size })
                            )
                        )
                    }
                    is LotteryViewModel.LotteryUiState.Error -> {
                        logger.e(
                            "PredictionResult", 
                            "显示错误状�? ${state.message}"
                        )
                        binding.progressBar.visibility = View.GONE
                        binding.contentGroup.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                        binding.errorText.text = state.message
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.refreshButton.setOnClickListener {
            viewModel.loadData(forceRefresh = true)
        }
    }

    private fun updateUI(state: LotteryViewModel.LotteryUiState.Success) {
        try {
            val prediction = state.prediction
            logger.d("PredictionResult", "更新UI数据: $prediction")
            
            with(prediction) {
                binding.apply {
                    killerNumbers.text = killers.joinToString(", ")
                    danmaNumbers.text = danma.joinToString(", ")
                    
                    when {
                        groups.size >= 3 -> {
                            group1Numbers.text = groups[0].joinToString(", ")
                            group2Numbers.text = groups[1].joinToString(", ")
                            group3Numbers.text = groups[2].joinToString(", ")
                            logger.d("PredictionResult", "显示3组数�?)
                        }
                        else -> {
                            group1Numbers.text = getString(R.string.prediction_invalid_group_data)
                            getString(R.string.prediction_full_coverage)
                            getString(R.string.prediction_good_coverage, validation.totalCovered)
                            getString(R.string.prediction_partial_coverage, validation.totalCovered)
            showErrorState(getString(R.string.prediction_update_error, e.localizedMessage))
                    }
                    
                    coverageInfo.text = when {
                        validation.isFullCoverage -> {
                            logger.d("PredictionResult", "显示完整覆盖状�?)
                            getString(R.string.prediction_full_coverage)
                        }
                        validation.totalCovered >= 5 -> {
                            logger.d("PredictionResult", "显示良好覆盖状�?)
                            getString(R.string.prediction_good_coverage, validation.totalCovered)
                        }
                        else -> {
                            logger.d("PredictionResult", "显示部分覆盖状�?)
                            getString(R.string.prediction_partial_coverage, validation.totalCovered)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.e(
                "PredictionResult", 
                "更新UI失败: ${e.message}", 
                e
            )
            showErrorState(getString(R.string.update_ui_error, e.localizedMessage))
        }
    }

    private fun showErrorState(message: String) {
        binding.apply {
            errorText.text = message
            errorText.visibility = View.VISIBLE
            contentGroup.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
