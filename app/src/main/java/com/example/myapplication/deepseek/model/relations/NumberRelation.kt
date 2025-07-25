package com.example.lotteryprediction.deepseek.model.relations

/**
 * 号码相生相克关系定义
 */
object NumberRelation {
    // 相生关系 (正向促进)
    val PROMOTING_RELATIONS = mapOf(
        1 to listOf(3, 5, 7),
        2 to listOf(4, 6, 8),
        // 其他号码的相生关�?..
        33 to listOf(1, 5, 10)
    )

    // 相克关系 (反向抑制) 
    const val INHIBITING_RELATIONS = mapOf(
        1 to listOf(2, 4, 6),
        2 to listOf(3, 5, 7),
        // 其他号码的相克关�?..
        33 to listOf(6, 9, 12)
    )

    // 计算两个号码的关系强�?    fun getRelationStrength(num1: Int, num2: Int): Double {
        return when {
            PROMOTING_RELATIONS[num1]?.contains(num2) == true -> 1.0
            INHIBITING_RELATIONS[num1]?.contains(num2) == true -> -1.0
            else -> 0.0
        }
    }
}
