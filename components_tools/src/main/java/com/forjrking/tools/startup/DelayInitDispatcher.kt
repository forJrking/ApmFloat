package com.forjrking.tools.startup

import android.os.AsyncTask
import android.os.Looper
import android.os.MessageQueue
import android.util.Log
import java.util.*

/***
 *空闲环境延迟初始化封装 利用｛android.os.MessageQueue.IdleHandler｝
 */
open class DelayInitDispatcher {

    private val mDelayTasks by lazy { LinkedList<Task>() }

    private val mIdleHandler = MessageQueue.IdleHandler {
        //一次空闲初始化一个
        if (mDelayTasks.size > 0) {
            val task = mDelayTasks.poll()
            DispatchRunnable(task).run()
        }
        !mDelayTasks.isEmpty()
    }

    fun addTask(task: Task?): DelayInitDispatcher {
        if (task != null) {
            mDelayTasks.add(task)
        }
        return this
    }

    fun start() {
        Looper.myQueue().addIdleHandler(mIdleHandler)
    }

    open abstract class Task(var doMain: Boolean = true, var name: String = "") : Runnable

    class DispatchRunnable(private var task: Task) {

        fun run() {
            if (task.doMain) {
                task.run()
            } else {
                AsyncTask.execute(task)
            }
            Log.d("DelayInitDispatcher", "task:${task.name} complete")
        }
    }
}