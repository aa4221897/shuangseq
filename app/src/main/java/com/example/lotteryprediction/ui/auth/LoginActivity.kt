package com.example.lotteryprediction.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lotteryprediction.R
import com.example.lotteryprediction.auth.AuthViewModel
import com.example.lotteryprediction.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthUiState.Loading -> showLoading()
                is AuthViewModel.AuthUiState.CodeSent -> showCodeSent()
                is AuthViewModel.AuthUiState.Success -> navigateToMain()
                is AuthViewModel.AuthUiState.Error -> showError(state.message)
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.btnRequestCode.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            if (phone.length == 11) {
                viewModel.requestSMSCode(phone)
            } else {
                Toast.makeText(this, R.string.auth_invalid_phone, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, R.string.auth_invalid_code, Toast.LENGTH_SHORT).show()
        }

        binding.btnVerify.setOnClickListener {
            val code = binding.etCode.text.toString()
            if (code.length == 6) {
                viewModel.verifyCode(binding.etPhone.text.toString(), code)
            } else {
                Toast.makeText(this, R.string.auth_invalid_code, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading() {
        // 显示加载状态
    }

    private fun showCodeSent() {
        // 显示验证码已发送
    }

    private fun navigateToMain() {
        // 跳转到主界面
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}