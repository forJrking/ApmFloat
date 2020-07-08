/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.forjrking.apmlib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;



/**
 * Helper class for controlling overlay view with FPS and JS FPS info
 * that gets added directly to @{link WindowManager} instance.
 */
public class ApmOverlayController {

    public static void initialize(Context cxt,boolean isDebug) {
        //APM相关初始化
        new ApmOverlayController(cxt.getApplicationContext()).setApmViewVisible(isDebug);
    }

    public static void requestPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get permission to show debug overlay in dev builds.
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.w("TAG", "Overlay permissions needs to be granted in order for react native apps to run in dev mode");
                if (canHandleIntent(context, intent)) {
                    context.startActivity(intent);
                }
            }
        }
    }

    private static boolean permissionCheck(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get permission to show debug overlay in dev builds.
            if (!Settings.canDrawOverlays(context)) {
                // overlay permission not yet granted
                return false;
            } else {
                return true;
            }
        }
        // on pre-M devices permission needs to be specified in manifest
        return hasPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
    }

    private static boolean hasPermission(Context context, String permission) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    if (p.equals(permission)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Error while retrieving package info", e);
        }
        return false;
    }

    private static boolean canHandleIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

    private final WindowManager mWindowManager;
    private final Context mContext;

    private ViewGroup mApmView;

    public ApmOverlayController(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setApmViewVisible(boolean fpsDebugViewVisible) {
        if (fpsDebugViewVisible && mApmView == null) {
            if (!permissionCheck(mContext)) {
                Log.d("TAG", "Wait for overlay permission to be set");
                return;
            }
            mApmView = new ApmView(mContext);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    getFloatType(mContext),
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.START | Gravity.TOP;
            try {
                mWindowManager.addView(mApmView, params);
                initEvent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!fpsDebugViewVisible && mApmView != null) {
            mApmView.removeAllViews();
            mWindowManager.removeView(mApmView);
            mApmView = null;
        }
    }

    private static int getFloatType(Context context) {
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", context.getPackageName()));
            if (permission || "Xiaomi".equals(Build.MANUFACTURER) || "vivo".equals(Build.MANUFACTURER)) {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }
        return type;
    }

    private void initEvent() {
        mApmView.setOnTouchListener(new View.OnTouchListener() {
            float lastX, lastY, x, y;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateXY((int) (x - lastX) / 3, (int) (y - lastY) / 3);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return true;
            }
        });
    }

    private void updateXY(int x, int y) {
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) mApmView.getLayoutParams();
        params.x += x;
        params.y += y;
        mWindowManager.updateViewLayout(mApmView, params);
    }
}
