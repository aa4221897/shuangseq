package com.example.myapplication.deepseek.data

import android.content.Context
import com.example.myapplication.deepseek.model.LotteryRecord
import com.example.myapplication.deepseek.util.NetworkUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DataCollectionServiceTest {
    private lateinit var context: Context
    private lateinit var service: DataCollectionService

    @Before
    fun setup() {
        context = mock(Context::class.java)
        service = DataCollectionService(context)
    }

    @Test
    fun testAddValidRecords() {
        val validRecord = LotteryRecord(
            redNumbers = listOf(1, 2, 3, 4, 5, 6),
            blueNumber = 7,
            date = Date()
        )
        assertTrue(service.addRecords(listOf(validRecord)))
    }

    @Test
    fun testValidateInvalidRecords() {
        val invalidRecords = listOf(
            LotteryRecord(redNumbers = listOf(1, 1, 3, 4, 5, 6), blueNumber = 7, date = Date()), // 重复号码
            LotteryRecord(redNumbers = listOf(1, 3, 5, 7, 9, 11), blueNumber = 7, date = Date()), // 无连续号码
            LotteryRecord(redNumbers = listOf(1, 2, 3, 4, 5, 6), blueNumber = 17, date = Date()) // 蓝球越界
        )
        invalidRecords.forEach { record ->
            assertFalse(service.validateRecord(record))
        }
    }

    @Test
    fun testCompressRecords() {
        val record = LotteryRecord(
            redNumbers = listOf(1, 2, 3, 4, 5, 6),
            blueNumber = 7,
            date = Date(1640995200000) // 2022-01-01
        )
        val compressed = service.compressRecords(listOf(record))
        assertEquals("1,2,3,4,5,6:7:1640995200000", compressed.toString(Charsets.UTF_8))
    }

    @Test
    fun testCalculateDataQuality() {
        val perfectRecord = LotteryRecord(
            redNumbers = (1..6).toList(),
            blueNumber = 7,
            date = Date()
        )
        assertEquals(1.0, service.calculateDataQuality(listOf(perfectRecord)))
    }
}