package com.example.myapplication.deepseek.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.deepseek.model.CoverageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LotteryViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LotteryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LotteryViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        assertTrue(viewModel.uiState.value is LotteryViewModel.LotteryUiState.Loading)
    }

    @Test
    fun testLoadDataSuccess() = runTest {
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is LotteryViewModel.LotteryUiState.Success)
        assertEquals(3, (state as LotteryViewModel.LotteryUiState.Success).prediction.killers.size)
    }

    @Test
    fun testCacheBehavior() = runTest {
        // 第一次加载
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // 第二次加载(应使用缓存)
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertNotNull((state as LotteryViewModel.LotteryUiState.Success).prediction)
    }

    @Test
    fun testForceRefresh() = runTest {
        // 第一次加载
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // 强制刷新
        viewModel.loadData(forceRefresh = true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is LotteryViewModel.LotteryUiState.Success)
    }
}