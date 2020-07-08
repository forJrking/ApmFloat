package com.forjrking.tools.thread;

import android.os.Handler;
import android.os.Looper;


/**
 * DES: 主线程调度器
 * CHANGED: 岛主
 * TIME: 2019/5/16 0016 下午 4:58
 * */
public class Deliver {

    private static final Handler MAIN_HANDLER;

    static {
        Looper looper;
        try {
            looper = Looper.getMainLooper();
        } catch (Exception e) {
            looper = null;
        }
        if (looper != null) {
            MAIN_HANDLER = new Handler(looper);
        } else {
            MAIN_HANDLER = null;
        }
    }

    public static void post(final Runnable runnable) {
        if (MAIN_HANDLER != null) {
            MAIN_HANDLER.post(runnable);
        } else {
            runnable.run();
        }
    }
}