package com.example.myapplication.deepseek.util

import com.example.myapplication.deepseek.model.LotteryRecord

object ValidationUtils {
    fun validateRecords(records: List<LotteryRecord>) {
        require(records.isNotEmpty()) { "Records cannot be empty" }
        records.forEach { it.validateNumbers() }
    }
}
