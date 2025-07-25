package com.example.lotteryprediction.deepseek.task

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.lotteryprediction.deepseek.data.LotteryDao
import com.example.lotteryprediction.deepseek.data.AppDatabase
import com.example.lotteryprediction.deepseek.ml.FeatureEngineer
import com.example.lotteryprediction.deepseek.model.LotteryRecord
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.MockitoAnnotations
import android.os.Build
import org.robolectric.annotation.Config
import org.mockito.Mockito.mock
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class FeatureOptimizationCheckpointTest {
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()
        workerParams = mock(WorkerParameters::class.java, RETURNS_DEEP_STUBS)
    }

    @Test
    fun testDoWorkWithEmptyData() = runBlocking {
        val mockDao = mock(LotteryDao::class.java).apply {
            whenever(getAllRecords()).thenReturn(emptyList())
        }
        val mockDb = mock(AppDatabase::class.java).apply {
            whenever(lotteryDao()).thenReturn(mockDao)
        }
        val mockFeatureEngineer = mock(FeatureEngineer::class.java)

        val worker = TestListenableWorkerBuilder<FeatureOptimizationCheckpoint>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    return FeatureOptimizationCheckpoint(
                        appContext,
                        workerParameters,
                        mockDb,
                        mockFeatureEngineer
                    )
                }
            })
            .build()
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun testDoWorkWithMockData() = runBlocking {
        val mockRecords = listOf(
            LotteryRecord("2023001", Date(1672531200000), listOf(1, 2, 3, 4, 5, 6), 1),
            LotteryRecord("2023002", Date(1673136000000), listOf(7, 8, 9, 10, 11, 12), 2)
        )
        
        val mockDao = mock(LotteryDao::class.java).apply {
            whenever(getAllRecords()).thenReturn(mockRecords)
        }
        val mockDb = mock(AppDatabase::class.java).apply {
            whenever(lotteryDao()).thenReturn(mockDao)
        }
        val mockFeatureEngineer = mock(FeatureEngineer::class.java).apply {
            whenever(extractFeatures(any())).thenReturn(emptyList())
            whenever(normalizeFeatures(any())).thenReturn(emptyList())
        }

        val worker = TestListenableWorkerBuilder<FeatureOptimizationCheckpoint>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    return FeatureOptimizationCheckpoint(
                        appContext,
                        workerParameters,
                        mockDb,
                        mockFeatureEngineer
                    )
                }
            })
            .build()
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun testDoWorkWithError() = runBlocking {
        val mockDao = mock(LotteryDao::class.java).apply {
            whenever(getAllRecords()).thenThrow(RuntimeException("Test error"))
        }
        val mockDb = mock(AppDatabase::class.java).apply {
            whenever(lotteryDao()).thenReturn(mockDao)
        }
        val mockFeatureEngineer = mock(FeatureEngineer::class.java)

        val worker = TestListenableWorkerBuilder<FeatureOptimizationCheckpoint>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    return FeatureOptimizationCheckpoint(
                        appContext,
                        workerParameters,
                        mockDb,
                        mockFeatureEngineer
                    )
                }
            })
            .build()
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }
}
