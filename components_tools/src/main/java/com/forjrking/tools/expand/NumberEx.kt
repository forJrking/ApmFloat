package com.forjrking.tools.expand

import com.forjrking.tools.Cxt
import com.forjrking.tools.EmptyUtils
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


/**
 * dp转px
 */
val Float.dp
    get() = dp2px

/**
 * sp转px
 */
val Float.sp
    get() = sp2px

/**
 * dp转px
 */
val Int.dp
    get() = toFloat().dp

/**
 * sp转px
 */
val Int.sp
    get() = toFloat().sp

/**
 * dp转px
 */
val Float.dp2px
    get() = run {
        val scale = Cxt.res.displayMetrics.density
        this * scale + 0.5F
    }.toInt()

/**
 * dp转px
 */
val Int.dp2px
    get() = toFloat().dp2px

/**
 * sp转px
 */
val Float.sp2px
    get() = run {
        val fontScale =Cxt.res.displayMetrics.scaledDensity
        this * fontScale + 0.5F
    }.toInt()

/**
 * sp转px
 */
val Int.sp2px
    get() = toFloat().sp2px

/**
 * px转dp
 */
val Float.px2dp
    get() = run {
        val scale =Cxt.res.displayMetrics.scaledDensity
        this / scale + 0.5F
    }.toInt()

/**
 * px转dp
 */
val Int.px2dp
    get() = toFloat().px2dp

/**
 * px转sp
 */
val Float.px2sp
    get() = run {
        val fontScale =Cxt.res.displayMetrics.scaledDensity
        this / fontScale + 0.5f
    }.toInt()

/**
 * px转sp
 */
val Int.px2sp
    get() = toFloat().px2sp
