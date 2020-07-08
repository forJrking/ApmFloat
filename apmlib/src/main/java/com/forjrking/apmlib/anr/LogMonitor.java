package com.forjrking.apmlib.anr;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @Description: 500毫秒内未执行完的操作，将会记录相关的堆栈日志
 * @Author: WinterHuang
 * @CreateDate: 20190905
 * @Version: 1.0.0
 */
class LogMonitor {
    private static LogMonitor sInstance = new LogMonitor();
    private HandlerThread mLogThread = new HandlerThread("log");
    private Handler mIoHandler;
    //达到阈值实时上报-当前定为 TIME_BLOCK 毫秒，分段解决卡顿问题
    private static final long TIME_BLOCK = 1000L;

    private LogMonitor() {
        mLogThread.start();
        mIoHandler = new Handler(mLogThread.getLooper());
    }

    private static Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                StringBuilder sb = new StringBuilder();
                StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
                for (StackTraceElement s : stackTrace) {
                    sb.append(s.toString() + "\n");
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    };

    public static LogMonitor getInstance() {
        return sInstance;
    }

    public void startMonitor() {
        mIoHandler.postDelayed(mLogRunnable, TIME_BLOCK);
    }

    public void removeMonitor() {
        mIoHandler.removeCallbacks(mLogRunnable);
    }
}
