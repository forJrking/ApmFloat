package com.forjrking.tools.expand

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/9 10:18·
 * @version: 1.0.0
 */

/**
 * 让double精确到 $dec 位
 * @param exact 精确到几位，四舍五入
 **/
inline fun Double.exact(exact: Int): Double = BigDecimal(this).let {
    it.setScale(exact, RoundingMode.HALF_UP)
    it.toDouble()
}

/**
 * 让double展示不包含 .0
 **/
inline fun Double.formatStr(): String =
    if (this.roundToLong() - this == 0.0) {
        this.toLong().toString()
    } else {
        this.toString()
    }