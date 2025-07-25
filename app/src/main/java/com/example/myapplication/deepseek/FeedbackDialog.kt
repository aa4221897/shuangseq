package com.example.lotteryprediction.deepseek

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lotteryprediction.R
import com.example.lotteryprediction.databinding.DialogFeedbackBinding

class FeedbackDialog : DialogFragment() {
    private lateinit var binding: DialogFeedbackBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogFeedbackBinding.inflate(LayoutInflater.from(context))
        
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        
        binding.submitButton.setOnClickListener {
            val feedback = binding.feedbackEditText.text.toString()
            val rating = binding.ratingBar.rating
            
            if (feedback.isBlank()) {
                Toast.makeText(context, "请填写反馈内�?, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 保存反馈到本地或发送到服务�?            saveFeedback(feedback, rating)
            dismiss()
        }
    }

    private fun saveFeedback(feedback: String, rating: Float) {
        val prefs = requireContext().getSharedPreferences("feedback", Context.MODE_PRIVATE)
        val count = prefs.getInt("count", 0) + 1
        
        with(prefs.edit()) {
            putString("feedback_$count", feedback)
            putFloat("rating_$count", rating)
            putInt("count", count)
            apply()
        }
        
        Toast.makeText(context, "感谢您的反馈!", Toast.LENGTH_SHORT).show()
    }
}
