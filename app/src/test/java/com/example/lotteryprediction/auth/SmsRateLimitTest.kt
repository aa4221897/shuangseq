package com.example.lotteryprediction.auth

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lotteryprediction.LotteryPredictionApp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SmsRateLimitTest {
    private lateinit var authViewModel: AuthViewModel
    private val testPhone = "13800138000"
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<LotteryPredictionApp>()
        authViewModel = AuthViewModel(context)
    }

    @Test
    fun testSmsSendInterval() {
        // 第一次发送成功
        assertTrue(authViewModel.sendVerificationCode(testPhone))
        
        // 立即再次发送应该失败
        assertFalse(authViewModel.sendVerificationCode(testPhone))
        
        // 模拟等待1分钟
        Thread.sleep(TimeUnit.MINUTES.toMillis(1))
        
        // 再次发送应该成功
        assertTrue(authViewModel.sendVerificationCode(testPhone))
    }

    @Test
    fun testInvalidPhoneFormat() {
        // 测试短于11位
        assertFalse(authViewModel.sendVerificationCode("1380013800"))
        
        // 测试非数字
        assertFalse(authViewModel.sendVerificationCode("1380013800a"))
        
        // 测试正确格式
        assertTrue(authViewModel.sendVerificationCode(testPhone))
    }
}