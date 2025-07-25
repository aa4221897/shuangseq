package com.example.lotteryprediction.deepseek.data

import android.content.Context
import com.example.lotteryprediction.R

/**
 * 资源映射服务
 */
class ResourceMapper(private val context: Context) {
    
    fun getTrendChartResources(): Map<String, Int> {
        return mapOf(
            "history_icon" to R.drawable.icon_single_arrow,
            "lottery_bg" to R.drawable.get_lottery_detail_member_bg,
            "unfinished_icon" to R.drawable.get_lottery_not_finished
        ).filterValues { it != 0 }
    }
    
    fun getLayoutToDrawableMap(): Map<Int, List<Int>> {
        return mapOf(
            R.layout.special_lottery_result_ssq_dlt to listOf(
                R.drawable.icon_single_arrow
            ),
            R.layout.special_lottery_result_ssq_live to listOf(
                R.drawable.icon_single_arrow
            ),
            R.layout.get_lottery_detail_activity to listOf(
                R.drawable.get_lottery_not_finished,
                R.drawable.get_lottery_detail_member_bg
            )
        )
    }
}
