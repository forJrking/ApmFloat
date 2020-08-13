package com.forjrking.tools.time

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log

/**
 * <pre>
 * @author yangchong
 * email  : yangchong211@163.com
 * time  :  2020/5/26
 * desc  :  自定义倒计时器
 * revise:
</pre> *
 */
open class CountDownTimer {
    /**
     * 时间，即开始的时间，通俗来说就是倒计时总时间
     */
    private var mMillisInFuture: Long = 0

    /**
     * 布尔值，表示计时器是否被取消
     * 只有调用cancel时才被设置为true
     */
    private var mCancelled = false

    /**
     * 用户接收回调的时间间隔，一般是1秒
     */
    private var mCountdownInterval: Long = 0

    /**
     * 记录暂停时候的时间
     */
    private var mStopTimeInFuture: Long = 0

    /**
     * 暂停时，当时剩余时间
     */
    private var mCurrentMillisLeft: Long = 0

    /**
     * 是否暂停
     * 只有当调用pause时，才设置为true
     */
    private var mPause = false

    /** DES: 是否在倒计时运行中 */
    var isRunning = false

    /**
     * 监听listener
     */
    private var mCountDownListener: TimerListener? = null

    constructor() {}
    constructor(millisInFuture: Long, countdownInterval: Long) {
        val total = millisInFuture + 20
        mMillisInFuture = total
        //this.mMillisInFuture = millisInFuture;
        mCountdownInterval = countdownInterval
    }

    /**
     * 开始倒计时，每次点击，都会重新开始
     */
    @Synchronized
    fun start() {
        if (mMillisInFuture <= 0 && mCountdownInterval <= 0) {
            throw RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0")
        }
        mCancelled = false
        val elapsedRealtime = SystemClock.elapsedRealtime()
        mStopTimeInFuture = elapsedRealtime + mMillisInFuture
        TimerLogger.i("start → mMillisInFuture = " + mMillisInFuture + ", seconds = " + mMillisInFuture / 1000)
        TimerLogger.i("start → elapsedRealtime = $elapsedRealtime, → mStopTimeInFuture = $mStopTimeInFuture")
        mPause = false
        mHandler!!.sendMessage(mHandler.obtainMessage(MSG))
        mCountDownListener?.onStart()
        isRunning = true
    }

    /**
     * 取消计时器
     */
    @Synchronized
    fun cancel() {
        if (mHandler != null) {
            //暂停
            mPause = false
            mHandler.removeMessages(MSG)
            //取消
            mCancelled = true
            isRunning = false
        }
    }

    /**
     * 按一下暂停，再按一下继续倒计时
     */
    @Synchronized
    fun pause() {
        if (mHandler != null) {
            if (mCancelled) {
                return
            }
            if (mCurrentMillisLeft < mCountdownInterval) {
                return
            }
            if (!mPause) {
                mHandler.removeMessages(MSG)
                mPause = true
                isRunning = false
            }
        }
    }

    /**
     * 恢复暂停，开始
     */
    @Synchronized
    fun resume() {
        if (mMillisInFuture <= 0 && mCountdownInterval <= 0) {
            throw RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0")
        }
        if (mCancelled) {
            return
        }
        //剩余时长少于
        if (mCurrentMillisLeft < mCountdownInterval || !mPause) {
            return
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mCurrentMillisLeft
        mHandler!!.sendMessage(mHandler.obtainMessage(MSG))
        mPause = false
        isRunning = true
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            synchronized(this@CountDownTimer) {
                if (mCancelled) {
                    return
                }
                //剩余毫秒数
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                if (millisLeft <= 0) {
                    mCurrentMillisLeft = 0
                    mCountDownListener?.onFinish()
                    TimerLogger.i("onFinish → millisLeft = $millisLeft")
                    isRunning = false
                } else if (millisLeft < mCountdownInterval) {
                    mCurrentMillisLeft = 0
                    TimerLogger.i("handleMessage → millisLeft < mCountdownInterval !")
                    // 剩余时间小于一次时间间隔的时候，不再通知，只是延迟一下
                    sendMessageDelayed(obtainMessage(MSG), millisLeft)
                } else {
                    //有多余的时间
                    val lastTickStart = SystemClock.elapsedRealtime()
                    TimerLogger.i("before onTick → lastTickStart = $lastTickStart")
                    TimerLogger.i("before onTick → millisLeft = " + millisLeft + ", seconds = " + millisLeft / 1000)
                    mCountDownListener?.onTick(millisLeft)
                    TimerLogger.i("after onTick → elapsedRealtime = " + SystemClock.elapsedRealtime())
                    mCurrentMillisLeft = millisLeft
                    // 考虑用户的onTick需要花费时间,处理用户onTick执行的时间
                    // 打印这个delay时间，大概是997毫秒
                    var delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime()
                    TimerLogger.i("after onTick → delay1 = $delay")
                    // 特殊情况：用户的onTick方法花费的时间比interval长，那么直接跳转到下一次interval
                    // 注意，在onTick回调的方法中，不要做些耗时的操作
                    var isWhile = false
                    while (delay < 0) {
                        delay += mCountdownInterval
                        isWhile = true
                    }
                    if (isWhile) {
                        TimerLogger.i("after onTick执行超时 → delay2 = $delay")
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
        }
    }

    /**
     * 设置倒计时总时间
     *
     * @param millisInFuture 毫秒值
     */
    fun setMillisInFuture(millisInFuture: Long) {
        val total = millisInFuture + 20
        mMillisInFuture = total
    }

    /**
     * 设置倒计时间隔值
     *
     * @param countdownInterval 间隔，一般设置为1000毫秒
     */
    fun setCountdownInterval(countdownInterval: Long) {
        mCountdownInterval = countdownInterval
    }

    /**
     * 设置倒计时监听
     *
     * @param countDownListener listener
     */
    fun setCountDownListener(countDownListener: TimerListener?) {
        mCountDownListener = countDownListener
    }

    private object TimerLogger {
        fun i(args: String?) {
            Log.i("Countdown", args)
        }
    }

    companion object {
        /**
         * mas.what值
         */
        private const val MSG = 520
    }
}

/** DES: 倒计时转换 */
fun Long.toCountTime(showMinute: Boolean = false, showHour: Boolean = false): String? {
    //秒
    var totalTime = this / 1000
    //时，分，秒
    var hour: Long = 0
    var minute: Long = 0
    var second: Long = 0
    if (3600 <= totalTime) {
        hour = totalTime / 3600
        totalTime = totalTime - 3600 * hour
    }
    if (60 <= totalTime) {
        minute = totalTime / 60
        totalTime = totalTime - 60 * minute
    }
    if (0 <= totalTime) {
        second = totalTime
    }
    val sb = StringBuilder()

    if (hour > 0) {
        if (hour < 10) {
            sb.append("0").append(hour).append(":")
        } else {
            sb.append(hour).append(":")
        }
    } else if (showHour) {
        sb.append("00:")
    }
    if (minute > 0) {
        if (minute < 10) {
            sb.append("0").append(minute).append(":")
        } else {
            sb.append(minute).append(":")
        }
    } else if (showMinute) {
        sb.append("00:")
    }

    if (second < 10) {
        sb.append("0").append(second)
    } else {
        sb.append(second)
    }

    return sb.toString()
}