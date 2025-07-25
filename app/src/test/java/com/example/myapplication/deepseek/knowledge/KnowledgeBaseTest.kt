package com.example.lotteryprediction.deepseek.knowledge

import com.example.lotteryprediction.deepseek.data.LotteryRecord
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KnowledgeBaseTest {
    private val testRecords = listOf(
        LotteryRecord(
            redNumbers = listOf(1, 2, 3, 4, 5, 6),
            blueNumber = 7,
            period = 1,
            date = Date()
        ),
        LotteryRecord(
            redNumbers = listOf(1, 2, 3, 4, 5, 7),
            blueNumber = 8,
            period = 2,
            date = Date()
        )
    )

    @Test
    fun testKnowledgeUpdate() {
        val initialVersion = KnowledgeBase.version
        KnowledgeBase.update("test_key", "test_value")
        assertEquals(initialVersion + 1, KnowledgeBase.version)
        assertEquals("test_value", KnowledgeBase.get("test_key"))
    }

    @Test
    fun testHistoricalDataLoading() {
        KnowledgeBase.loadHistoricalData(testRecords)
        val stats = KnowledgeBase.get("history_stats") as Map<*, *>
        assertEquals(2, stats["total_periods"])
        
        val hotNumbers = stats["hot_numbers"] as List<*>
        assertTrue(hotNumbers.contains(1))
        assertTrue(hotNumbers.contains(2))
        
        val coldNumbers = stats["cold_numbers"] as List<*>
        assertTrue(coldNumbers.size > 0)
    }

    @Test
    fun testMetadataTracking() {
        val metadata = KnowledgeBase.get("metadata") as Map<*, *>
        assertNotNull(metadata["created"])
        assertNotNull(metadata["last_updated"])
    }
}
