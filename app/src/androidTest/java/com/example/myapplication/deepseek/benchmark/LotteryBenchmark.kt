package com.example.myapplication.deepseek.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.deepseek.model.LotteryRecord
import com.example.myapplication.deepseek.model.PositionAnalysisModel
import com.example.myapplication.deepseek.model.CrossPositionAnalysisModel
import com.example.myapplication.deepseek.model.ZoneEnergyAnalysisModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LotteryBenchmark {
    private val testHistory = List(100) { i ->
        LotteryRecord(
            "2023${i.toString().padStart(3, '0')}",
            listOf(
                (1..6).random(),
                (7..12).random(),
                (13..18).random(),
                (19..24).random(),
                (25..33).random(),
                (1..33).random()
            ).sorted(),
            i + 1
        )
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun benchmarkPositionAnalysis() {
        val model = PositionAnalysisModel()
        benchmarkRule.measureRepeated {
            model.analyze(testHistory, testHistory.lastIndex)
        }
    }

    @Test
    fun benchmarkCrossPositionAnalysis() {
        val model = CrossPositionAnalysisModel()
        benchmarkRule.measureRepeated {
            model.analyze(testHistory, testHistory.lastIndex)
        }
    }

    @Test
    fun benchmarkZoneEnergyAnalysis() {
        val model = ZoneEnergyAnalysisModel()
        benchmarkRule.measureRepeated {
            model.analyze(testHistory, testHistory.lastIndex)
        }
    }
}
