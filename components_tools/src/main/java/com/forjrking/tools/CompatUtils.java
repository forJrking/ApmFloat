package com.forjrking.tools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * DES:  主要用于对系统做兼容方法处理
 * CHANGED: 岛主
 * TIME: 2018/11/2 0002 上午 10:32
 */
public class CompatUtils {

    public static void installApk(Context context, String path) {
        if (context == null || TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * DES:通过文件获取uri 可能为空
     * TIME: 2018/11/2 0002 上午 10:45
     */
    @Nullable
    public static Uri getUri4Path(Context context, String path) {
        File file = new File(path);
        return getUri4File(context, file);
    }


    /**
     * DES: 通过文件获取uri 可能为空
     * TIME: 2018/11/2 0002 上午 10:45
     */
    @Nullable
    private static Uri getUri4File(Context context, File file) {
        Uri uri = null;
        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            } else {
                uri = Uri.fromFile(file);
            }
        }
        return uri;
    }

    /**
     * DES: 悬浮窗权限申请
     * TIME: 2019/1/14 0014 上午 11:22
     */
    public static boolean overlayPermRequest(Context context) {
        boolean permNeeded = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    permNeeded = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permNeeded;
    }

    /**
     * DES: 悬浮窗兼容类型
     * TIME: 2019/1/14 0014 上午 11:21
     */
    public static int getFloatType(Context context) {
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

    /**
     * DES: 垃圾 8.0
     * TIME: 2019/3/11 0011 下午 4:31
     */
    public static void fixBug2O(Activity activity) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && CompatUtils.isTranslucentOrFloating(activity)) {
            boolean result = CompatUtils.fixOrientation(activity);
            Log.e("Android O", "onCreate fixOrientation when Oreo, result = " + result);
        }
    }

    /**
     * DES: 垃圾 8.0
     * TIME: 2019/3/11 0011 下午 4:31
     */
    public static boolean fixOrientation(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(activity);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * DES: 垃圾 8.0
     * TIME: 2019/3/11 0011 下午 4:31
     */
    public static boolean isTranslucentOrFloating(Activity activity) {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = activity.obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    public static String getUUID() {

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位
        final String ANDROID_ID = Settings.System.getString(Cxt.get().getContentResolver(), Settings.System.ANDROID_ID);
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), ANDROID_ID.hashCode()).toString();
    }

    /**
     * DES: 唤起startActivity 通过intent
     * TIME: 2019/7/5 0005 上午 9:27
     * startActivity(context,new Intent(context,Activity.class))
     */
    public static void startActivity(Context context, Intent intent) {
        if (context != null && intent != null) {
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            if (componentName != null) {
                context.startActivity(intent);
            } else {
                Log.e("startActivity", "intent not available");
            }
        }
    }


    /**
     * DES: 唤起 startService 通过intent 注意Android O以上建议使用 jobIntentService (v4包)
     * TIME: 2019/7/5 0005 上午 9:27
     * startService(context,new Intent(context,Activity.class))
     */
    public static ComponentName startService(Context context, Intent intent) {
        try {
            if (context != null && intent != null) {
                return context.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("startService", "startService has error");
        }
        return null;
    }

}
