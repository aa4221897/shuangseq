package com.example.myapplication.deepseek.model

import com.example.myapplication.deepseek.data.LotteryRecord

interface AnalysisStrategy {
    fun analyze(records: List<LotteryRecord>): Map<String, Any>
}