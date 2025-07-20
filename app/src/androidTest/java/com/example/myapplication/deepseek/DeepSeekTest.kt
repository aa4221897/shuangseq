package com.example.myapplication.deepseek

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeepSeekTest {
    private lateinit var context: Context
    private lateinit var config: DeepSeekConfig

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        config = DeepSeekConfig(context)
    }

    @Test
    fun testConfigInitialization() {
        assertNotNull(config)
        assertEquals("", config.apiKey)
        assertEquals("https://api.deepseek.com/v1", config.endpoint)
    }

    @Test
    fun testChatFunction() {
        val service = DeepSeekService(config)
        assertNotNull(service)
    }

    @Test
    fun testKnowledgeBase() {
        val service = DeepSeekService(config)
        service.initKnowledgeBase(context)
        
        // 测试知识库加载
        val knowledge = service.getKnowledgeBase()
        assertNotNull(knowledge)
        assertTrue(knowledge.getLayoutKnowledge().isNotEmpty())
        
        // 测试资源工厂
        val resource = knowledge.getResourceFactory().create("test")
        assertNotNull(resource)
    }

    @Test
    fun testNetworkUtils() {
        // 测试网络工具类
        assertFalse(NetworkUtils.isNetworkAvailable(context))
    }

    @Test
    fun testPerformanceMonitor() {
        // 测试性能监控
        PerformanceMonitor.start("test")
        Thread.sleep(100)
        val duration = PerformanceMonitor.end("test")
        assertTrue(duration >= 100)
    }
}
