package com.forjrking.tools.hook;

import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * @Author: 岛主
 * @CreateDate: 2019/3/29 0029 下午 5:16
 * @Version: 1.0.0
 */
public class ToastCompat {

    /**
     * DES: 主要hook toast
     * TIME: 2019/3/29 0029 下午 5:42
     */
    public static void setContextCompat(@NonNull View view, @NonNull Context context) {
        if (Build.VERSION.SDK_INT == 25) {
            try {
                Field field = View.class.getDeclaredField("mContext");
                field.setAccessible(true);
                field.set(view, context);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
