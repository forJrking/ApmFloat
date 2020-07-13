package com.forjrking.tools;

import android.os.SystemClock;
import android.view.View;

import androidx.collection.LruCache;

import static android.view.View.NO_ID;


/**
 * DES: 点击工具类
 * TIME: 2018/10/22 0022 下午 6:21
 */
public class ClickUtils {
    /**
     * 防止连续点击间隔时间
     */
    public static final int SECOND = 1;
    private static final long DEFAULT_INTERVAL_MILLIS = 10000;

    //可以使用 resize 调整大小
    public static LruCache<Integer, Long> sLruCache = new LruCache<>(10);

    /**
     * Don't let anyone instantiate this class.
     */
    private ClickUtils() {
        throw new UnsupportedOperationException("Do not need instantiate!");
    }

    /**
     * 是否是快速点击
     *
     * @param v 点击的控件
     * @return true:是，false:不是
     */
    public static boolean isFastDoubleClick(View v) {
        return isFastDoubleClick(v, DEFAULT_INTERVAL_MILLIS);
    }

    /**
     * 是否是快速点击
     *
     * @param v              点击的控件
     * @param intervalMillis 时间间期（毫秒）
     * @return true:是，false:不是
     */
    public static boolean isFastDoubleClick(View v, long intervalMillis) {
        int id = v.getId() == NO_ID ? v.hashCode() : v.getId();
        return isFastExecute(id, intervalMillis);
    }

    /**
     * DES: method 必须为唯一ID
     * TIME: 2019/5/16 0016 下午 7:43
     */
    public static boolean isFastExecute(int methodId, long intervalMillis) {
        long time = System.currentTimeMillis();
        Long aLong = sLruCache.get(methodId);
        long timeD = time - (aLong == null ? 0 : aLong);
        if (0 < timeD && timeD < intervalMillis) {
            return true;
        } else {
            sLruCache.put(methodId, time);
            return false;
        }
    }

    //    并发支持2个
    public static LruCache<Integer, long[]> sInvCache = new LruCache<>(2);

    /**
     * intervalMillis 毫秒内点击sum次 执行方法
     * DES: method 必须为唯一ID
     * TIME: 2019/5/16 0016 下午 7:43
     */
    public static boolean isExecuteInterval(int methodId, int frequency, long intervalMillis) {
        long[] inval = sInvCache.get(methodId);
        if (inval == null || inval.length != frequency) {
            inval = new long[frequency];
        }
        System.arraycopy(inval, 1, inval, 0, inval.length - 1);
        inval[inval.length - 1] = SystemClock.uptimeMillis();
        if (inval[0] >= (SystemClock.uptimeMillis() - intervalMillis)) {
            sInvCache.remove(methodId);
            return true;
        } else {
            sInvCache.put(methodId, inval);
            return false;
        }
    }

    /**防止重复点击的回调*/
    public static void setOnClickListener(final View view, final View.OnClickListener listener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && isFastDoubleClick(view)) {
                    listener.onClick(v);
                }
            }
        });
    }
}
