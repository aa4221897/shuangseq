package com.example.lotteryprediction.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lotteryprediction.R
import com.example.lotteryprediction.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val disclaimerShownKey = "disclaimer_shown"
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        if (!isDisclaimerShown()) {
            showDisclaimer()
        } else {
            setupViews()
            setupObservers()
        }
    }
    
    fun showDisclaimer() {
        AlertDialog.Builder(this)
            .setTitle(R.string.base_disclaimer_title)
            .setMessage(R.string.base_disclaimer)
            .setPositiveButton(R.string.base_i_understand) { _, _ ->
                setDisclaimerShown()
                setupViews()
                setupObservers()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun isDisclaimerShown(): Boolean {
        return getSharedPreferences("disclaimer", MODE_PRIVATE)
            .getBoolean(disclaimerShownKey, false)
    }
    
    private fun setDisclaimerShown() {
        getSharedPreferences("disclaimer", MODE_PRIVATE)
            .edit()
            .putBoolean(disclaimerShownKey, true)
            .apply()
    }
    
    private fun setupViews() {
        binding.btnPredict.setOnClickListener {
            lifecycleScope.launch {
                viewModel.predict()
            }
        }
    }

    private fun setupObservers() {
        viewModel.recentResults.observe(this) { results ->
            // 更新最近开奖结果展�?
        }
        
        viewModel.predictionResult.observe(this) { result ->
            // 处理预测结果
        }
    }
}
