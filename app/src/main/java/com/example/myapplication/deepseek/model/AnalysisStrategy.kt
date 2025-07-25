package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.data.LotteryRecord

interface AnalysisStrategy {
    fun analyze(records: List<LotteryRecord>): Map<String, Any>
}
