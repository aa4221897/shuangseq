package com.example.myapplication.deepseek.util

import java.net.URL
import javax.net.ssl.HttpsURLConnection

object NetworkUtils {
    fun verifySSLCertificate(urlString: String): Boolean {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpsURLConnection
            connection.connect()
            connection.disconnect()
            true
        } catch (e: Exception) {
            false
        }
    }
}