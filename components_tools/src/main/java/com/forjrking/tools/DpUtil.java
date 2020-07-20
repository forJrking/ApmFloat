package com.forjrking.tools;

import android.content.Context;
import android.content.res.Resources;

public class DpUtil {

    /*** 获取手机屏幕宽度*/
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        } else {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
    }


    /*** 获取手机屏幕高度*/
    public static int getScreenHeight(Context context) {
        if (context == null) {
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        } else {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static float sp2px(float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    public static int px2dp(final float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Value of px to value of sp.
     *
     * @param pxValue The value of px.
     * @return value of sp
     */
    public static int px2sp(final float pxValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


}
