package com.example.lotteryprediction.deepseek.model

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import java.util.Date
import kotlin.collections.List
import kotlin.collections.mutableListOf
import kotlin.collections.emptyList
import kotlin.Double
import kotlin.Boolean

class CoverageValidatorTest {
    private lateinit var validator: CoverageValidator
    private val testDate = Date()
    private val testRecords: List<LotteryRecord> = mutableListOf(
        LotteryRecord(period = "2023001", date = testDate, redNumbers = mutableListOf(1, 2, 3, 4, 5, 6), blueNumber = 1),
        LotteryRecord(period = "2023002", date = testDate, redNumbers = mutableListOf(7, 8, 9, 10, 11, 12), blueNumber = 2),
        LotteryRecord(period = "2023003", date = testDate, redNumbers = mutableListOf(13, 14, 15, 16, 17, 18), blueNumber = 3)
    )

    @Before
    fun setup() {
        validator = CoverageValidator(testRecords)
    }

    @Test
    fun testIsNumberCoveredInHistory() {
        assertTrue(validator.isNumberCoveredInHistory(1, "2023004"))
        assertFalse(validator.isNumberCoveredInHistory(1, "2023001"))
    }

    @Test
    fun testGetContextRecords() {
        val context = validator.getContextRecords("2023003", 2)
        assertEquals(2, context.size)
        assertEquals("2023001", context[0].period)
    }

    @Test
    fun testClassifyCoverage() {
        val record = LotteryRecord(period = "2023004", date = testDate, redNumbers = listOf(2, 4, 6, 8, 10, 12), blueNumber = 4)
        val type = validator.classifyCoverage(record, emptyList())
        assertEquals(CoverageOptimizer.CoverageType.DANMA_DRIVEN, type)
    }

    @Test
    fun testAnalyzeNumberDistribution() {
        val distribution = validator.analyzeNumberDistribution(testRecords)
        assertEquals(0.5, distribution.evenRatio, 0.01)
    }

    @Test
    fun testValidate() {
        val record = LotteryRecord(period = "2023004", date = testDate, redNumbers = listOf(1, 2, 3, 4, 5, 6), blueNumber = 5)
        val predictions = LotteryPredictionManager.Predictions(
            groups = listOf(listOf(1, 2, 3)),
            danma = listOf(4, 5),
            killers = listOf(6)
        )
        val result = validator.validate(record, predictions)
        assertTrue(result.isFullCoverage)
    }
}
