package com.forjrking.tools

import android.app.Activity
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.forjrking.tools.log.KLog
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/6 10:24
 * @version: 1.0.0
 */
class ActivityManager() {

    private val lifecycleCallbacks: IActivityLifecycleCallbacks = IActivityLifecycleCallbacks()

    companion object {
        @JvmStatic
        val instances by lazy { ActivityManager() }
    }

    init {
        Cxt.application?.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    private fun getStack() = lifecycleCallbacks.activityStack

    private val listeners = CopyOnWriteArrayList<ForegroundCallbacks>()

    open fun topActivity(): Activity = getStack().peek()

    open fun bottomActivity(): Activity = getStack().firstElement()

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

    // 监听切换到前台
    private var resumedCount = 0

    inner class IActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        open val activityStack: Stack<Activity> by lazy { Stack<Activity>() }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityStack.push(activity)
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            if (resumedCount++ == 0 && isForeground()) {
                listeners.forEach {
                    it.onBecameForeground(activity)
                }
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (--resumedCount == 0 && isBackground()) {
                listeners.forEach {
                    it.onBecameBackground()
                }
            }
        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            activityStack.remove(activity)
        }

    }

    fun isForeground(): Boolean {
        return isAppOnForeground(Cxt.get())
    }

    fun isBackground(): Boolean {
        return !isForeground()
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

    /**
     * 判断当前程序是否在前台
     */
    private fun isAppOnForeground(context: Context?): Boolean {
        val activityManager =
            context?.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        val appProcesses = activityManager?.runningAppProcesses
        if (appProcesses == null || appProcesses.size <= 0) {
            return true
        }
        for (appProcess in appProcesses) {
            if (appProcess == null) {
                KLog.w("getProcess == null")
                return true
            } else if (appProcess.processName == context.packageName) {
                KLog.i("isAppOnForeground importance = " + appProcess.importance)
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcess.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
            }
        }
        return false
    }

}