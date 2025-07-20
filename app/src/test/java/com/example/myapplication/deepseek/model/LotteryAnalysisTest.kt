package com.example.myapplication.deepseek.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

import com.example.myapplication.deepseek.model.AnalysisResult.PositionAnalysis
import com.example.myapplication.deepseek.model.AnalysisResult.CrossPositionAnalysis
import java.util.Date

@RunWith(AndroidJUnit4::class)

class LotteryAnalysisTest {
    // 准备测试数据
    private val testHistory = listOf(
        LotteryRecord("2023001", Date(1672531200000), listOf(1, 7, 13, 19, 25, 31), 1),
        LotteryRecord("2023002", Date(1673136000000), listOf(2, 8, 14, 20, 26, 32), 2),
        LotteryRecord("2023003", Date(1673740800000), listOf(3, 9, 15, 21, 27, 33), 3),
        LotteryRecord("2023004", Date(1674345600000), listOf(4, 10, 16, 22, 28, 29), 4),
        LotteryRecord("2023005", Date(1674950400000), listOf(5, 11, 17, 23, 27, 30), 5),
        LotteryRecord("2023006", Date(1675555200000), listOf(6, 12, 18, 24, 28, 31), 6)
    )

    @Test
    fun testZoneDefinitions() {
        assertEquals(LotteryAnalysisModel.Zone.LOW, LotteryAnalysisModel.getNumberZone(1))
        assertEquals(LotteryAnalysisModel.Zone.MID_LOW, LotteryAnalysisModel.getNumberZone(7))
        assertEquals(LotteryAnalysisModel.Zone.MID, LotteryAnalysisModel.getNumberZone(13))
        assertEquals(LotteryAnalysisModel.Zone.MID_HIGH, LotteryAnalysisModel.getNumberZone(19))
        assertEquals(LotteryAnalysisModel.Zone.HIGH, LotteryAnalysisModel.getNumberZone(25))
    }

    @Test
    fun testPositionAnalysis() {
        val model = PositionAnalysisModel()
        val result = model.analyze(testHistory, testHistory.lastIndex)
        
        // 验证基本分析结果
        assertEquals(6, result.positionResults.size)
        assert(result.confidence in 0.0..1.0)
    }

    @Test
    fun testCrossPositionAnalysis() {
        val model = CrossPositionAnalysisModel()
        val result = model.analyze(testHistory, testHistory.lastIndex)
        
        // 验证跨位分析矩阵
        assertEquals(6, result.crossPositionResults.size)
        result.crossPositionResults.values.forEach { analysis ->
            assertEquals(5, analysis.sizeMatrix.size) // 5期回溯
            analysis.sizeMatrix.forEach { row ->
                assertEquals(6, row.size) // 6个位置
            }
        }
        assert(result.confidence in 0.0..1.0)
    }

    @Test
    fun testZoneEnergyAnalysis() {
        val model = ZoneEnergyAnalysisModel()
        val result = model.analyze(testHistory, testHistory.lastIndex)
        
        // 验证能量分析
        assertEquals(5, result.zoneEnergies.size) // 5个分区
        result.zoneEnergies.values.forEach { energy ->
            assert(energy in 0.0..6.0) // 每个分区最多6个球
        }
        assert(result.confidence in 0.0..1.0)
    }

    @Test
    fun testIntegratedPrediction() {
        val predictor = LotteryPredictor()
        val result = predictor.predict(testHistory, testHistory.lastIndex)
        
        // 验证集成预测结果
        assertEquals(6, result.positionAnalysis.size)
        assertEquals(6, result.crossPositionAnalysis.size)
        assertEquals(5, result.zoneEnergies.size)
        assert(result.confidence in 0.0..1.0)
    }

    // 边界测试用例
    @Test
    fun testEmptyHistory() {
        val model = PositionAnalysisModel()
        val result = model.analyze(emptyList(), 0)
        println("DEBUG: positionResults size = ${result.positionResults.size}")
        println("DEBUG: confidence = ${result.confidence}")
        println("DEBUG: crossPositionResults size = ${result.crossPositionResults.size}")
        println("DEBUG: zoneEnergies size = ${result.zoneEnergies.size}")
        assertEquals(0, result.positionResults.size)
        assertEquals(0.0, result.confidence, 0.001)
        assertEquals(0, result.crossPositionResults.size)
        assertEquals(0, result.zoneEnergies.size)
    }

    @Test
    fun testSingleRecordHistory() {
        val model = CrossPositionAnalysisModel()
        val result = model.analyze(listOf(testHistory.first()), 0)
        assertEquals(6, result.crossPositionResults.size)
        assert(result.confidence < 0.5) // 单条记录置信度应较低
    }

    @Test
    fun testAllSameNumbers() {
        val sameNumberHistory = List(5) { 
            LotteryRecord("202300${it+1}", Date(1672531200000 + it * 86400000L), List(6) { 1 }, it+1) 
        }
        val model = ZoneEnergyAnalysisModel()
        val result = model.analyze(sameNumberHistory, sameNumberHistory.lastIndex)
        val expectedZone = LotteryAnalysisModel.getNumberZone(1)
        
        // 添加调试日志
        println("Test input: ${sameNumberHistory.map { it.redNumbers }}")
        println("Expected zone: $expectedZone")
        
        // 验证模型正确处理全相同数字的情况
        val actualEnergy = result.zoneEnergies[expectedZone] ?: 0.0
        println("Actual energy: $actualEnergy")
        println("All zone energies: ${result.zoneEnergies}")
        println("Model confidence: ${result.confidence}")
        println("Position results: ${result.positionResults}")
        println("Cross position results: ${result.crossPositionResults}")
        assertEquals(6.0, actualEnergy, 0.001)
        assertTrue(result.confidence >= 0.0 && result.confidence <= 1.0)
        assertEquals(0, result.positionResults.size)
        assertEquals(0, result.crossPositionResults.size)
        
        // 验证其他分区能量值为0
        LotteryAnalysisModel.Zone.values().filter { it != expectedZone }.forEach { zone ->
            assertEquals(0.0, result.zoneEnergies[zone] ?: 0.0, 0.001)
        }
        
        // 调试信息
        println("TestAllSameNumbers - Model output:")
        println("Zone energies: ${result.zoneEnergies}")
        println("Confidence: ${result.confidence}")
        println("Position results: ${result.positionResults.size} entries")
        println("Cross position results: ${result.crossPositionResults.size} entries")
        
        // 验证模型返回了正确的AnalysisResult结构
        println("Zone energies details: ${result.zoneEnergies.entries.joinToString()}") 
        assertEquals(5, result.zoneEnergies.size) // 5个分区
        assertTrue("Zone energies should be 6.0 or 0.0", 
            result.zoneEnergies.values.all { it == 6.0 || it == 0.0 })
        
        // 添加更详细的断言信息
        println("Detailed zone energies validation:")
        LotteryAnalysisModel.Zone.values().forEach { zone ->
            val expected = if (zone == expectedZone) 6.0 else 0.0
            assertEquals("Zone $zone energy mismatch", 
                expected, 
                result.zoneEnergies[zone] ?: 0.0, 
                0.001)
        }
        
        // 调试输出实际值
        println("Actual zone energies: ${result.zoneEnergies}")
        println("Actual confidence: ${result.confidence}")
        
        // 验证分区定义是否正确
        assertEquals(LotteryAnalysisModel.Zone.LOW, expectedZone)
        
        // 添加更详细的调试信息
        println("Final validation results:")
        println("Zone energies: ${result.zoneEnergies.mapValues { "%.2f".format(it.value) }}")
        println("Confidence: %.2f".format(result.confidence))
        println("Position results count: ${result.positionResults.size}")
        println("Cross position results count: ${result.crossPositionResults.size}")
    }
}
