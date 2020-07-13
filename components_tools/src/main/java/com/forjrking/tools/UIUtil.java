package com.forjrking.tools;

import android.app.Activity;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;

import com.forjrking.tools.log.KLog;


/**
 * Created by forjrking  13/9/14.
 */
public class UIUtil {

    private UIUtil() {
    }

    /**
     * 切换View 显示
     *
     * @param invisible
     * @param views
     * @return
     */
    public static boolean setViewInvisible(boolean invisible, View... views) {
        int visibility = invisible ? View.INVISIBLE : View.VISIBLE;
        for (View view : views) {
            if (view != null) {
                view.setVisibility(visibility);
            }
        }
        return invisible;
    }

    /**
     * 切换View 显示
     *
     * @return
     */
    public static boolean setGoneOrVisible(boolean gone, View... views) {
        int visibility = gone ? View.GONE : View.VISIBLE;
        for (View view : views) {
            if (view != null) {
                view.setVisibility(visibility);
            }
        }
        return gone;
    }

    /**
     * 判断View 是否显示
     *
     * @param view
     * @return
     */
    public static boolean isVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }


    /**
     * setEnable View
     *
     * @return
     */
    public static void setEnable(boolean enable, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setEnabled(enable);
            }
        }
    }

    /**
     * 单项选择
     *
     * @param selectedView 选中的view
     * @param views
     */
    public static void setSingleSelected(View selectedView, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setSelected(false);
            }
        }
        selectedView.setSelected(true);
    }

    public static Boolean checkIsVisible(Activity activity, View view) {
        //如果已经加载了，判断view是否显示出来，然后展示引导图
        int screenWidth = getScreenMetrics(activity).x;
        int screenHeight = getScreenMetrics(activity).y;
        int height = screenHeight - DpUtil.dp2px(49);
        Rect rect = new Rect(0, 0, screenWidth, height);
        int[] location = new int[2];
        int dip2px = DpUtil.dp2px(49);
        KLog.i("checkIsVisible",
                "location11===" + location[1]
                        + "---dip2px===" + dip2px
                        + "---screenWidth===" + screenWidth
                        + "---screenHeight===" + screenHeight
                        + "---location00===" + location[0]
                        + "---height===" + height);
        view.getLocationInWindow(location);
        if (view.getLocalVisibleRect(rect)) {
            return true;
        } else {
            //view已不在屏幕可见区域;
            return false;
        }
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     * @param activity
     * @return
     */
    public static Point getScreenMetrics(Activity activity) {
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);
    }

    public static void setVisible(View view) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * DES: 设置EditText Hint文字
     * TIME: 2018/11/14 11:41
     */
    public static void setHintText(EditText editText, CharSequence text) {
        if (editText != null && text != null) {
            editText.setHint(text);
        }
    }

    /**
     * DES: 获取EditText文字
     * TIME: 2018/11/20 10:28
     */
    public static String getText(EditText editText) {
        if (editText != null) {
            return editText.getText().toString().trim();
        }
        return "";
    }

    /**
     * DES: 获取EditText文字
     * TIME: 2018/11/20 10:28
     */
    public static String getHintText(EditText editText) {
        if (editText != null) {
            return editText.getHint().toString().trim();
        }
        return "";
    }


    /**
     * DES: 扩大View的点击范围 30像素
     * TIME: 2019/1/31 0031 下午 2:52
     */
    public static void expandTouchArea(View view) {
        expandTouchArea(view, 30);
    }

    /**
     * DES: 扩大View的点击范围
     * TIME: 2019/1/31 0031 下午 2:52
     */
    public static void expandTouchArea(final View view, final int size) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                view.getHitRect(rect);
                rect.top -= size;
                rect.bottom += size;
                rect.left -= size;
                rect.right += size;
                parentView.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }

    public static void setRound(final View view, final int radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setClipToOutline(true);
            view.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            });
        } else {
            throw new RuntimeException("don’t support  < Android L");
        }

    }

    /**
     * Return the width of view.
     *
     * @param view The view.
     * @return the width of view
     */
    public static int getMeasuredWidth(final View view) {
        return measureView(view)[0];
    }

    /**
     * Return the height of view.
     *
     * @param view The view.
     * @return the height of view
     */
    public static int getMeasuredHeight(final View view) {
        return measureView(view)[1];
    }

    /**
     * Measure the view.
     *
     * @param view The view.
     * @return arr[0]: view's width, arr[1]: view's height
     */
    public static int[] measureView(final View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int lpHeight = lp.height;
        int heightSpec;
        if (lpHeight > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(widthSpec, heightSpec);
        return new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
    }
}
