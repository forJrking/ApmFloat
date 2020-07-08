package com.forjrking.tools

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/6 10:24
 * @version: 1.0.0
 */
class IActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private val activityStack: Stack<Activity> by lazy { Stack<Activity>() }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.push(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity)
    }

    open fun topActivity(): Activity = activityStack.firstElement()

    open fun bottomActivity(): Activity = activityStack.peek()

    /**
     * 彻底退出
     */
    fun finishAllActivity() {
        var activity: Activity
        while (!activityStack.empty()) {
            activity = activityStack.pop()
            activity?.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    fun finishActivity(vararg cls: Class<*>) {
        repeat(cls.count()) {

        }
    }

    /**
     * 查找栈中是否存在指定的activity
     *
     * @param cls
     * @return
     */
    fun hasActivity(cls: Class<*>): Boolean {
        for (activity in activityStack) {
            if (activity.javaClass == cls) {
                return true
            }
        }
        return false
    }

    /**
     * 结束指定的Activity
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activityStack.remove(activity)
            activity.finish()
        }
    }

}