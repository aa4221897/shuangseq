package com.example.lotteryprediction.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor() {
    val deepSeekService: DeepSeekService = DeepSeekService.create()
}
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.DEEPSEEK_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: DeepSeekService = retrofit.create(DeepSeekService::class.java)
}