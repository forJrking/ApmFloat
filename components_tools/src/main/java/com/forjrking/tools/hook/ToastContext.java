package com.forjrking.tools.hook;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * DES: 主要用于解决 25版本 toast崩溃问题
 * CHANGED: 岛主
 * TIME: 2019/3/29 0029 下午 5:43
 */
public class ToastContext extends ContextWrapper {

    private @NonNull
    Toast toast;

    public ToastContext(@NonNull Context base, @NonNull Toast toast) {
        super(base);
        this.toast = toast;
    }

    @Override
    public Context getApplicationContext() {
        return new ApplicationContextWrapper(getBaseContext().getApplicationContext());
    }

    private final class ApplicationContextWrapper extends ContextWrapper {

        private ApplicationContextWrapper(@NonNull Context base) {
            super(base);
        }


        @Override
        public Object getSystemService(@NonNull String name) {
            if (Context.WINDOW_SERVICE.equals(name)) {
                // noinspection ConstantConditions
                return new WindowManagerWrapper((WindowManager) getBaseContext().getSystemService(name));
            }
            return super.getSystemService(name);
        }
    }


    private final class WindowManagerWrapper implements WindowManager {

        private static final String TAG = "WindowManagerWrapper";
        private final @NonNull
        WindowManager base;


        private WindowManagerWrapper(@NonNull WindowManager base) {
            this.base = base;
        }


        @Override
        public Display getDefaultDisplay() {
            return base.getDefaultDisplay();
        }


        @Override
        public void removeViewImmediate(View view) {
            base.removeViewImmediate(view);
        }


        @Override
        public void addView(View view, ViewGroup.LayoutParams params) {
            try {
                Log.d(TAG, "WindowManager's addView(view, params) has been hooked.");
                base.addView(view, params);
            } catch (Throwable throwable) {
                Log.e(TAG, "[addView]", throwable);
            }
        }


        @Override
        public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
            base.updateViewLayout(view, params);
        }


        @Override
        public void removeView(View view) {
            base.removeView(view);
        }
    }
}