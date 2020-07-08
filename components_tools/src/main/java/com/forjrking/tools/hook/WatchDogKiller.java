package com.forjrking.tools.hook;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * DES: 用于解决 timeOut ANR问题
 * CHANGED: 岛主
 * TIME: 2019/1/17 0017 下午 3:27
 */
public class WatchDogKiller {
    private static final String TAG = "WatchDogKiller";
    private static volatile boolean sWatchdogStopped = false;

    public static boolean checkWatchDogAlive() {
        final Class clazz;
        try {
            clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            final Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            final Object watchdog = field.get(null);
            Method isRunningMethod = clazz.getSuperclass().getDeclaredMethod("isRunning");
            isRunningMethod.setAccessible(true);
            boolean isRunning = (boolean) isRunningMethod.invoke(watchdog);
            return isRunning;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void stopWatchDog(boolean debug) {
        // 建议在在debug包或者灰度包中不要stop，保留发现问题的能力。为了Sample效果，先注释
        if (debug) {
            return;
        }

        // Android P 以后不能反射FinalizerWatchdogDaemon
        if (Build.VERSION.SDK_INT >= 28) {
            Log.w(TAG, "stopWatchDog, do not support after Android P, just return");
            return;
        }
        if (sWatchdogStopped) {
            Log.w(TAG, "stopWatchDog, already stopped, just return");
            return;
        }
        sWatchdogStopped = true;
        Log.w(TAG, "stopWatchDog, try to stop watchdog");

        try {
            final Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            final Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            final Object watchdog = field.get(null);
            try {
                final Field thread = clazz.getSuperclass().getDeclaredField("thread");
                thread.setAccessible(true);
                thread.set(watchdog, null);
            } catch (final Throwable t) {
                Log.e(TAG, "stopWatchDog, set null occur error:" + t);
                t.printStackTrace();
                try {
                    // 直接调用stop方法，在Android 6.0之前会有线程安全问题
                    final Method method = clazz.getSuperclass().getDeclaredMethod("stop");
                    method.setAccessible(true);
                    method.invoke(watchdog);
                } catch (final Throwable e) {
                    Log.e(TAG, "stopWatchDog, stop occur error:" + t);
                    t.printStackTrace();
                }
            }
        } catch (final Throwable t) {
            Log.e(TAG, "stopWatchDog, get object occur error:" + t);
            t.printStackTrace();
        }
    }
}
