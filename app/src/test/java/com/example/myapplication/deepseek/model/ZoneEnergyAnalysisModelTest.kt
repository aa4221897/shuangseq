package com.example.lotteryprediction.deepseek.model

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.Timeout
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class ZoneEnergyAnalysisModelTest {
    @get:Rule
    val timeoutRule = Timeout.seconds(10)
    
    private lateinit var model: ZoneEnergyAnalysisModel

    @Before
    fun setUp() {
        model = ZoneEnergyAnalysisModel()
    }

    @Test
    fun testBasicAnalysis() {
        val records = listOf(
            LotteryRecord(period = "2023001", date = Date(), 
                         redNumbers = listOf(1, 2, 3, 4, 5, 6), blueNumber = 1),
            LotteryRecord(period = "2023002", date = Date(), 
                         redNumbers = listOf(7, 8, 9, 10, 11, 12), blueNumber = 2)
        )
        
        val result = model.analyze(records, 1) // 使用整数作为currentIndex
        assertNotNull(result)
    }

    @Test
    fun testLargeDataPerformance() {
        val largeHistory = generateTestData(10_000)
        
        val elapsedTime = measureTimeMillis {
            model.analyze(largeHistory, largeHistory.size - 1) // 使用整数作为currentIndex
        }
        
        println("Processed 10,000 records in $elapsedTime ms")
        assertTrue(elapsedTime < 5000)
    }

    @Test
    fun testConcurrentAccess() {
        val threadCount = 10
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val testData = generateTestData(100)
        
        repeat(threadCount) { i ->
            executor.submit {
                model.analyze(testData, i) // 使用整数作为currentIndex
                latch.countDown()
            }
        }
        
        latch.await()
        assertTrue(true) // 仅验证无异常
    }

    private fun generateTestData(count: Int): List<LotteryRecord> {
        val random = Random()
        return List(count) { i ->
            LotteryRecord(
                period = "test$i",
                date = Date(System.currentTimeMillis() - i * 86400000L),
                redNumbers = List(6) { random.nextInt(33) + 1 }.distinct().take(6),
                blueNumber = random.nextInt(16) + 1
            )
        }
    }
}
