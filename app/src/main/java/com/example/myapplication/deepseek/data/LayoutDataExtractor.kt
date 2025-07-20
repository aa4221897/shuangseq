package com.example.myapplication.deepseek.data

import android.content.res.Resources
import org.xmlpull.v1.XmlPullParser

/**
 * 布局数据提取器
 */
class LayoutDataExtractor(private val resources: Resources) {
    
    fun extractLotteryData(): List<LotteryLayoutInfo> {
        return listOf(
            parseLayout(R.layout.get_lottery_detail_activity),
            parseLayout(R.layout.special_lottery_result_ssq_dlt),
            parseLayout(R.layout.special_lottery_result_ssq_live)
        ).filterNotNull()
    }
    
    private fun parseLayout(layoutId: Int): LotteryLayoutInfo? {
        return try {
            val parser = resources.getLayout(layoutId)
            var eventType = parser.next()
            var lotteryInfo: LotteryLayoutInfo? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "TextView" -> {
                                val text = parser.getAttributeValue(null, "text")
                                if (text?.contains("双色球") == true) {
                                    lotteryInfo = LotteryLayoutInfo(
                                        layoutId = layoutId,
                                        displayType = when {
                                            text.contains("第") -> "期号显示"
                                            else -> "标题显示"
                                        },
                                        containsResultView = parser.name == "LotteryResultView2"
                                    )
                                }
                            }
                            "LotteryResultView2" -> {
                                lotteryInfo = lotteryInfo?.copy(
                                    containsResultView = true
                                )
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
            lotteryInfo
        } catch (e: Exception) {
            null
        }
    }
}

data class LotteryLayoutInfo(
    val layoutId: Int,
    val displayType: String,
    val containsResultView: Boolean
)