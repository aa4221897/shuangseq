package com.example.lotteryprediction.deepseek.util

import com.example.lotteryprediction.deepseek.model.LotteryRecord

object ValidationUtils {
    fun validateRecords(records: List<LotteryRecord>) {
        require(records.isNotEmpty()) { "Records cannot be empty" }
        records.forEach { it.validateNumbers() }
    }
}
