package com.example.lotteryprediction.deepseek

import android.content.Context
import com.example.lotteryprediction.deepseek.model.LotteryPredictor
import com.example.lotteryprediction.deepseek.model.LotteryRecord
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DeepSeekService(private val config: DeepSeekConfig) {
    private val predictor = LotteryPredictor()
    
    val client = OkHttpClient.Builder()
        .connectTimeout(DeepSeekConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(DeepSeekConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DeepSeekConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(config.endpoint)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    fun initKnowledgeBase(context: Context) {
        // 初始化彩票知识库
        LotteryKnowledgeBase.init(context)
    }

    /**
     * 预测下一期双色球号码
     * @param history 历史开奖记�?按时间顺序排�?
     * @return 预测结果
     */
    fun predictLottery(history: List<LotteryRecord>): LotteryPredictor.PredictionResult {
        require(history.size >= 6) { "至少需�?期历史数�? }
        return predictor.predict(history, history.lastIndex)
    }
}

object LotteryKnowledgeBase {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        // 加载本地知识库数�?    }

    fun getHistoryData(): List<VersionHistoryManager.LotteryHistory> {
        // 模拟返回彩票历史数据
        return listOf(
            VersionHistoryManager.LotteryHistory("2023001", listOf(1, 2, 3, 4, 5, 6), "2023-01-01"),
            VersionHistoryManager.LotteryHistory("2023002", listOf(7, 8, 9, 10, 11, 12), "2023-01-08"),
            VersionHistoryManager.LotteryHistory("2023003", listOf(13, 14, 15, 16, 17, 18), "2023-01-15")
        )
    }
}
