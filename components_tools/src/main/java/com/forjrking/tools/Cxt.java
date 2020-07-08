package com.forjrking.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.view.View;

import androidx.annotation.ArrayRes;

/**
 * DES: CXT 全局Context 控制存取类
 * CHANGED: 岛主
 * TIME: 2019/3/8 0008 下午 3:23
 */
public class Cxt {

    private static Context _cxt;
    private static boolean _isDebug;
    private static Resources _res;
    private static Application _application;


    public static Application getApplication() {
        return _application;
    }

    public static void initialize(Application _application) {
        Cxt._application = _application;
        Cxt._cxt = _application.getApplicationContext();
    }

    public static Context get() {
        if (_cxt == null) throw new NullPointerException("must set() Cxt");
        return _cxt;
    }

    public static Resources getRes() {
        if (_res == null) {
            _res = _cxt.getResources();
        }
        return _res;
    }

    public static <T> T getSystemService(String name) {
        return (T) _cxt.getSystemService(name);
    }

    public static String getStr(int resId) {
        return _cxt.getString(resId);
    }

    public static int getColor(int resId) {
        return getRes().getColor(resId);
    }

    public static String getStr(int resId, Object... fmtArgs) {
        return _cxt.getString(resId, fmtArgs);
    }

    public static String[] getStrArray(@ArrayRes int resId) {
        return getRes().getStringArray(resId);
    }

    public static void setIsDebug(boolean isDebug) {
        Cxt._isDebug = isDebug;
    }

    public static boolean isDebug() {
        return _isDebug;
    }

    /**
     * DES: 获取Activity
     */
    public static Activity getActivity(View view) {
        // DES: 获取当前View的上下文
        Context context = view.getContext();
        // DES: 循环判断该上下文对象是否继承于ContextWrapper
        while (context instanceof ContextWrapper) {
            // DES: 判断当前上下文对象是否继承于Activity
            if (context instanceof Activity) {
                return (Activity) context;
            }
            // DES: 获取基类的Context对象
            context = ((ContextWrapper) context).getBaseContext();
        }
        throw new IllegalStateException("The View's Context is not an Activity.");
    }

}

