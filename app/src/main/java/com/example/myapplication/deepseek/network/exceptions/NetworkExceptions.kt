package com.example.myapplication.deepseek.network.exceptions

import java.io.IOException
import retrofit2.HttpException

class DeepSeekException(message: String) : IOException(message)
class SSLHandshakeException(message: String) : IOException(message)
class SocketTimeoutException(message: String) : IOException(message)