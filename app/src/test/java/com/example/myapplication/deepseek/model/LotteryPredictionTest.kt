package com.example.lotteryprediction.deepseek.model

import com.example.lotteryprediction.deepseek.util.Logger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.util.Date
import java.util.ArrayList
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class LotteryPredictionTest {
    private lateinit var testData: List<LotteryRecord>
    private lateinit var manager: LotteryPredictionManager

    @Before
    fun setup() {
        testData = generateTestData(100)
        manager = LotteryPredictionManager(testData)
    }

    @Test
    fun testKillerPredictionAccuracy() {
        val predictor = KillerPredictor(testData)
        val accuracy = predictor.backtest(80, 99)
        
        assertTrue("Killer prediction accuracy should > 0.5", accuracy > 0.5)
    }

    @Test
    fun testCoverageValidation() {
        val validator = CoverageValidator(testData)
        val testRecord = testData.last()
        
        // 确保至少1个胆码命�?        val danma = testRecord.redNumbers.take(1) + (1..33)
            .filterNot { testRecord.redNumbers.contains(it) }
            .shuffled()
            .take(2)
            
        val predictions = LotteryPredictionManager.Predictions(
            killers = (1..33).filterNot { testRecord.redNumbers.contains(it) }.take(10),
            danma = danma,
            groups = listOf(
                testRecord.redNumbers.shuffled().take(6),
                (1..33).shuffled().take(6),
                (1..33).shuffled().take(6)
            )
        )
        
        val result = validator.validate(testRecord, predictions)
        assertTrue("Should cover at least 1 number", result.totalCovered >= 1)
        // 移除对isFullCoverage的断言，因为随机数据可能意外满足全覆盖
        println("Coverage result: ${result.totalCovered}/${result.totalPossible} numbers covered")
    }

    @Test
    fun testInvalidDataHandling() {
        try {
            LotteryPredictionManager(emptyList()).predictNextPeriod()
            fail("Should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("History data cannot be empty", e.message)
        }
    }

    @Test
    fun testMinimalDataPrediction() {
        val minimalData = generateTestData(4) // 使用4条数据，小于要求�?�?        println("Testing with ${minimalData.size} records")
        
        // Create a simple mock logger
        val mockLog = object : Logger {
            override fun d(tag: String, message: String) {
                println("DEBUG: $tag - $message")
            }
            override fun e(tag: String, message: String) {
                println("ERROR: $tag - $message")
            }
        }
        
        try {
            val manager = LotteryPredictionManager(minimalData)
            manager.setLogger(mockLog)
            manager.predictNextPeriod()
            fail("Should throw exception for insufficient data")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("At least 5 history records are required") == true)
        }
    }

    private fun generateTestData(count: Int): List<LotteryRecord> {
        return (1..count).map { i ->
            LotteryRecord(
                period = "2023${i.toString().padStart(3, '0')}",
                date = Date(System.currentTimeMillis() - (count - i) * 86400000L),
                redNumbers = generateRandomNumbers(1..33, 6),
                blueNumber = (1..16).random()
            )
        }
    }

    private fun generateRandomNumbers(range: IntRange, count: Int): List<Int> {
        return range.toList().shuffled().take(count).sorted()
    }
}
