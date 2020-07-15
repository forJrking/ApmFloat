package com.forjrking.tools

import android.app.Activity
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/6 10:24
 * @version: 1.0.0
 */
class ActivityManager() {

    private val CHECK_DELAY = 600L

    private val lifecycleCallbacks: IActivityLifecycleCallbacks = IActivityLifecycleCallbacks()

    private fun getStack() = lifecycleCallbacks.activityStack

    private val listeners = CopyOnWriteArrayList<ForegroundCallbacks>()

    companion object {
        @JvmStatic
        val instances by lazy { ActivityManager() }
    }

    fun initialize(_application: Application) {
        _application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    open fun topActivity(): Activity? = getStack().let { if (it.isNotEmpty()) it.peek() else null }

    open fun bottomActivity(): Activity? =
        getStack().let { if (it.isNotEmpty()) it.firstElement() else null }

    /**
     * 彻底退出
     */
    fun finishAllActivity() {
        var activity: Activity
        while (!getStack().empty()) {
            activity = getStack().pop()
            activity?.finish()
        }
    }

    fun appExit() {
        finishAllActivity()
        System.exit(0)
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    fun finishActivity(vararg cls: Class<Activity>, isOther: Boolean = false) {
        val iterator = getStack().iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (isOther) {
                if (!cls.contains(next.javaClass)) {
                    next.finish()
                    iterator.remove()
                }
            } else {
                if (cls.contains(next.javaClass)) {
                    next.finish()
                    iterator.remove()
                }
            }
        }
    }

    /**
     * 查找栈中是否存在指定的activity
     * @param cls
     * @return >=0 成功
     */
    fun hasActivity(cls: Class<Activity>): Int {
        getStack().forEachIndexed { index, activity ->
            if (activity.javaClass == cls) {
                return@hasActivity index
            }
        }
        return -1
    }

    /**
     * 结束指定的Activity
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            getStack().remove(activity)
            activity.finish()
        }
    }

    /**
     * 跳转到管理栈堆的指定的activity
     * @param cls
     */
    fun jump2Activity(cls: Class<Activity>): Boolean {
        val index = hasActivity(cls)
        if (index >= 0) {
            repeat(index) {
                val activity = getStack().removeAt(it)
                activity.finish()
            }
            return true
        }
        return false
    }


    private inner class IActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        open val activityStack: Stack<Activity> by lazy { Stack<Activity>() }

        /**前后台监控 延时*/
        private val handler by lazy { Handler(Looper.getMainLooper()) }

        /**前后台监控*/
        private var check: Runnable? = null

        // 监听切换到前台
        private var foreground = false
        private var paused = true

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityStack.push(activity)
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            paused = false
            val wasBackground: Boolean = !foreground
            foreground = true
            if (check != null) {
                handler.removeCallbacks(check)
            }
            if (wasBackground) {
                listeners.forEach {
                    it.onBecameForeground(activity)
                }
            }
        }

        override fun onActivityPaused(activity: Activity) {
            paused = true
            if (check != null) {
                handler.removeCallbacks(check)
            }
            handler.postDelayed(Runnable {
                if (foreground && paused) {
                    foreground = false
                    listeners.forEach {
                        it.onBecameBackground()
                    }
                }
            }.also { check = it }, CHECK_DELAY)
        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            activityStack.remove(activity)
        }

    }

    fun addListener(listener: ForegroundCallbacks?) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: ForegroundCallbacks?) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
        }
    }

}