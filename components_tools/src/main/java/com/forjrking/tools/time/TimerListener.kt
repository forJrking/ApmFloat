package com.forjrking.tools.time

interface TimerListener {
    /**
     * 当倒计时开始
     */
    fun onStart() {}

    /**
     * 当倒计时结束
     */
    fun onFinish()

    /**
     * @param millisUntilFinished 剩余时间
     */
    fun onTick(millisUntilFinished: Long)
}