package com.example.lotteryprediction.ui.result

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lotteryprediction.R
import com.example.lotteryprediction.databinding.ActivityPredictionResultBinding
import com.example.lotteryprediction.domain.model.PredictionResult

class PredictionResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictionResultBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        showDisclaimer()
        
        val result = intent.getParcelableExtra<PredictionResult>("result") ?: return
        displayResult(result)
    }
    
    private fun showDisclaimer() {
        binding.disclaimerText.text = getString(R.string.base_disclaimer)
        
        AlertDialog.Builder(this)
            .setTitle(R.string.base_disclaimer_title)
            .setMessage(R.string.base_disclaimer)
            .setPositiveButton(android.R.string.ok, null)
            .show()
            
        binding.tvTitle.text = getString(R.string.prediction_title)
    
    private fun displayResult(result: PredictionResult) {
        binding.tvTitle.text = getString(R.string.prediction_title)
        binding.tvConfidence.text = "置信度: ${(result.confidence * 100).toInt()}%"
        binding.tvMethod.text = "使用策略: ${result.methodUsed}"
        
        // 显示红球和蓝球
        // 实现细节省略...
        
        // 确保免责声明可见
        binding.disclaimerText.visibility = View.VISIBLE
    }
}