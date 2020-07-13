package com.forjrking.tools.thread;


import android.os.Looper;

/**
 * @创建者 froJrking
 * @创建时间 2017/3/15 10:26
 * @描述 ${全局使用} 切换主线程
 */

public class GoHandler extends android.os.Handler {

    protected GoHandler(Looper looper) {
        super(looper);
    }

    static GoHandler mHandler;

    public static GoHandler getInstance() {
        if (mHandler == null) {
            synchronized (GoHandler.class) {
                if (mHandler == null) {
                    mHandler = new GoHandler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }

    /**防止一股脑使用导致Looper阻塞*/
    public void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }
}
