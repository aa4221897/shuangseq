package com.example.lotteryprediction.deepseek.ml

import com.example.lotteryprediction.deepseek.model.LotteryRecord
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class FeatureEngineerTest {
    private val engineer = FeatureEngineer()

    @Test
    fun testFeatureExtraction() {
        val records = listOf(
            LotteryRecord(redNumbers = listOf(1, 2, 3, 4, 5, 6), blueNumber = 7, date = System.currentTimeMillis()),
            LotteryRecord(redNumbers = listOf(2, 3, 4, 5, 6, 7), blueNumber = 8, date = System.currentTimeMillis())
        )
        
        val features = engineer.extractFeatures(records)
        assertEquals(2, features.size)
        assertTrue(features.all { it.size > 10 }) // 确保提取了足够多的特�?
    }

    @Test
    fun testFeatureNormalization() {
        val features = listOf(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(4.0, 5.0, 6.0)
        )
        
        val normalized = engineer.normalizeFeatures(features)
        assertEquals(2, normalized.size)
        assertTrue(normalized.all { vec -> vec.size == 3 })
    }

    @Test
    fun testFeatureSelection() {
        val features = listOf(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(4.0, 5.0, 6.0)
        )
        
        // 模拟特征重要�?
        engineer.calculateFeatureImportance(features, listOf(1, 0))
        
        val selected = engineer.selectFeatures(features, 0.5)
        assertTrue(selected.all { vec -> vec.size <= 3 })
    }

    @Test
    fun testMathFeatures() {
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(3.5, numbers.average())
        assertEquals(1.8708, numbers.standardDeviation(), 0.0001)
        assertEquals(2.9937, numbers.geometricMean(), 0.0001)
        assertEquals(2.4489, numbers.harmonicMean(), 0.0001)
    }
}
