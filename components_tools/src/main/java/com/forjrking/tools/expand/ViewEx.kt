package com.forjrking.tools.expand

import android.app.Activity
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.forjrking.tools.Cxt
import com.forjrking.tools.UIUtil

/**
 * View 相关的Kotlin扩展方法
 * <p>
 *
 * @author ['s GitHub](https://github.com/Ayvytr)
 * @since 0.1.0
 */


/**
 * 隐藏 View
 */
fun View.hide(isTrue: Boolean = true) {
    visibility = if (isTrue) GONE else VISIBLE
}


/**
 * 显示/隐藏 View
 * @param isTrue {@code true } 显示<br>{@code false} 隐藏
 */
fun View.show(isTrue: Boolean = true) {
    visibility = if (isTrue) VISIBLE else GONE
}

/**
 * invisible/显示 View
 * @param `true` INVISIBLE, `false` VISIBLE
 */
fun View.invisible(isTrue: Boolean = true) {
    visibility = if (isTrue) INVISIBLE else VISIBLE
}


/**
 * 判断View是不是可见
 *
 * @return `true` 可见([View.getVisibility] == [View.VISIBLE])
 */
fun View.isVisible(): Boolean {
    return visibility == VISIBLE
}

/**
 * 判断View是不是可见
 *
 * @return `true` 可见([View.getVisibility] == [View.VISIBLE])
 */
fun View.isShow(): Boolean {
    return isVisible()
}

/**
 * 判断View是不是可见
 *
 * @return `true` 可见([View.getVisibility] == [View.INVISIBLE])
 */
fun View.isInvisible(): Boolean {
    return visibility == INVISIBLE
}

/**
 * 判断View是不是可见
 *
 * @return `true` 可见([View.getVisibility] == [View.GONE])
 */
fun View.isGone(): Boolean {
    return visibility == GONE
}


/**
 * 判断View是不是可见
 *
 * @return `true` 可见([View.getVisibility] == [View.GONE])
 */
fun View.isHide(): Boolean {
    return isGone()
}


/**
 * 判断View是不是可见
 *
 * @return `true` 可见([View.getVisibility] == [View.GONE])
 */
fun View.getActivity(): Activity {
    return Cxt.getActivity(this)
}

/**
 * [EditText]文本变化监听器，文本变化后，以[timeout]为时间间隔触发[action]，[ignoreEmpty]=true: 输入文本为
 * 空时，不触发[action].
 * [timeout]默认300ms，默认[ignoreEmpty]=true，忽略空字符串.
 * @since 3.0.0
 * 灵感：RxBinding TextView.textChanges。这里直接以[EditText]实现，[android.widget.TextView]没有必要.
 */
fun TextView.textChange(
    timeout: Int = 300,
    ignoreEmpty: Boolean = true,
    action: (text: String) -> Unit
) {
    Views.textChange(this, timeout, ignoreEmpty, action)
}

/**
 * [View]点击事件，点击[doActionAfterTimes]次后执行[action]，如果[doActionAfterTimes]==1,
 * [millisecondInterval]是防止重复点击的间隔；如果[doActionAfterTimes]>1，相当于双击，多次点击，
 * 以[millisecondInterval]内点击记录点击次数，点击次数到[doActionAfterTimes]，触发[action].
 *
 * [doActionAfterTimes]默认为1 [millisecondInterval]默认500ms.
 * @since 3.0.0
 */
fun View.onFastClick(
    doActionAfterTimes: Int = 1, millisecondInterval: Int = 500,
    action: () -> Unit
) {
    Views.onClick(this, doActionAfterTimes, millisecondInterval, action)
}

/**
 * 获取[View] id，如果没有id：如果SDK>17, 使用[View.generateViewId]；否则使用[View.hashCode]
 */
fun View.getViewId(): Int {
    var id = id
    if (id == NO_ID) {
        id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            generateViewId()
        } else {
            this.hashCode()
        }
    }
    return id
}

/**
 *为View增加点击区域面积，[size] 为px
 */
fun View.expandTouchArea(size: Int = 30) {
    UIUtil.expandTouchArea(this, size)
}

/**
 * @since 3.0.0
 */

object Views {

    private val clickMap by lazy { hashMapOf<Int, Triple<Int, Int, Long>>() }

    fun onClick(
        view: View,
        doActionAfterTimes: Int = 1,
        millisecondInterval: Int = 300,
        action: () -> Unit
    ) {
        val viewId = view.getViewId()
        clickMap.remove(viewId)
        view.setOnClickListener {
            val t = clickMap[viewId]
            var times = doActionAfterTimes
            if (times < 1) {
                times = 1
            }

            val third = System.currentTimeMillis()
            if (times == 1) {
                if (t == null || (third - t.third >= millisecondInterval)) {
                    action.invoke()
                    clickMap[viewId] = Triple(1, 1, third)
                }
            } else {
                if (t == null) {
                    clickMap[viewId] = Triple(times, 1, third)
                } else {
                    if (third - t.third >= millisecondInterval) {
                        clickMap[viewId] = Triple(times, 1, third)
                    } else {
                        val newT = t.copy(second = t.second + 1, third = third)
                        clickMap[viewId] = newT
                        if (newT.second != newT.first) {
                            //不做任何操作
                        } else {
                            action.invoke()
                            clickMap.remove(viewId)
                        }
                    }
                }
            }
        }
    }

    private val textChangeMap by lazy { hashMapOf<TextView, Pair<TextWatcher, Runnable>>() }

    private val textChangeObserver by lazy {
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed() {
                textChangeMap.forEach {
                    val et = it.key
                    val triple = textChangeMap[et]
                    et.removeCallbacks(triple?.second)
                    et.removeTextChangedListener(triple?.first)
                }
                if (textChangeMap.isNotEmpty()) {
                    val et = textChangeMap.keys.first()
                    (et.context as? FragmentActivity)?.lifecycle?.removeObserver(this)
                    textChangeMap.clear()
                }
            }
        }
    }

    fun textChange(
        et: TextView,
        timeout: Int,
        ignoreEmpty: Boolean,
        action: (text: String) -> Unit
    ) {
        if (!textChangeMap.containsKey(et)) {
            (et.context as? FragmentActivity)?.lifecycle?.addObserver(textChangeObserver)
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    val runnable =
                        if (!textChangeMap.containsKey(et))
                            Runnable { action.invoke(s.toString()) }
                        else textChangeMap[et]!!.second

                    if (!textChangeMap.containsKey(et)) {
                        textChangeMap[et] = Pair(this, runnable)
                    }

                    et.handler.removeCallbacks(runnable)
                    /**
                     * 写在这里，不是只排除[s]内容为空的情况，而是这次时间间隔所有的输入，所以之前的
                     * removeCallbacks必不可少
                     */
                    if (ignoreEmpty && s.toString().isEmpty()) {
                        //no op
                    } else {
                        et.handler.postDelayed(runnable, timeout.toLong())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            et.addTextChangedListener(textWatcher)
        }
    }
}