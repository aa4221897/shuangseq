package com.example.myapplication.deepseek.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class NetworkUtilsTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var capabilities: NetworkCapabilities

    @Before
    fun setup() {
        connectivityManager = mock(ConnectivityManager::class.java)
        network = mock(Network::class.java)
        capabilities = mock(NetworkCapabilities::class.java)
        
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(capabilities)
    }

    @Test
    fun testNetworkQualityWifi() {
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)
        assertEquals(NetworkUtils.NetworkQuality.EXCELLENT, NetworkUtils.getNetworkQuality(context))
    }

    @Test
    fun testAutoRetrySuccess() {
        var retryCount = 0
        var successCalled = false
        
        NetworkUtils.withAutoRetry(context,
            operation = { 
                retryCount++
                if (retryCount == 1) throw RuntimeException() else "Success" 
            },
            onSuccess = { successCalled = true },
            onFailure = { fail("Should not be called") }
        )
        
        assertTrue(successCalled)
        assertEquals(2, retryCount)
    }

    @Test
    fun testCacheStrategy() {
        val strategy = object : NetworkUtils.CacheStrategy<String> {
            override fun getFromCache() = "CachedData"
            override fun saveToCache(data: String) {}
            override fun shouldUseCache(networkQuality: NetworkUtils.NetworkQuality) = 
                networkQuality.shouldUseCache()
        }
        
        assertTrue(strategy.shouldUseCache(NetworkUtils.NetworkQuality.POOR))
        assertFalse(strategy.shouldUseCache(NetworkUtils.NetworkQuality.EXCELLENT))
    }

    @Test
    fun testDiagnoseNetwork() {
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)
        val diagnosis = NetworkUtils.diagnoseNetwork(context)
        
        assertEquals(true, diagnosis["networkAvailable"])
        assertEquals("EXCELLENT", diagnosis["networkQuality"])
        assertNotNull(diagnosis["timestamp"])
    }
}