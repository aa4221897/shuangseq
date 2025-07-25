package com.example.lotteryprediction.deepseek.model

/**
 * 双色球分析指标常量定�? */
object LotteryAnalysisConstants {
    // 基础指标
    const val POSITIONAL = "positional" // 定位分析
    const val AMPLITUDE = "amplitude" // 振幅
    const val CONSECUTIVE = "consecutive" // 连号
    
    // 数值特�?    const val ODD_EVEN = "odd_even" // 奇偶
    const val PRIME_COMPOSITE = "prime_composite" // 质合
    const val SIZE = "size" // 大小
    
    // 和值相�?    const val SUM = "sum" // 和�?    const val SUM_TAIL = "sum_tail" // 和值尾�?    
    // 尾数分析
    const val TAIL_SIZE = "tail_size" // 尾数大小
    const val TAIL_ODD_EVEN = "tail_odd_even" // 尾数奇偶
    
    // 完整指标列表(此处省略部分指标...)
    val ALL_INDICATORS = listOf(
        POSITIONAL, AMPLITUDE, CONSECUTIVE, 
        ODD_EVEN, PRIME_COMPOSITE, SIZE,
        SUM, SUM_TAIL, TAIL_SIZE, TAIL_ODD_EVEN
    )
}
