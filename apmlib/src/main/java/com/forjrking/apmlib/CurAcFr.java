package com.forjrking.apmlib;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;



/**
 * DES: 当前页面采集
 * CHANGED: 岛主
 * TIME: 2019/5/17 0017 上午 11:34
 */
public class CurAcFr {
    private String TAG = "CurAcFrTag";

    private FragmentManager.FragmentLifecycleCallbacks supportFragmentLifecycleCallbacks;

    private android.app.FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks;

    private OnChangeListener mOnChangeListener;

    private String mCurrentFragmentName;


    public void init(Context context) {
        final Application app = (Application) context.getApplicationContext();
        if (app == null) {
            throw new NullPointerException("Application is null");
        }

        Application.ActivityLifecycleCallbacks sActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                if (activity instanceof FragmentActivity) {
//                    FragmentActivity fragmentActivity = (FragmentActivity) activity;
//                    fragmentActivity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(supportFragmentLifecycleCallbacks, true);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        fragmentActivity.getFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
//                    }
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        activity.getFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
//                    }
//                }

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                String sCurrentActivityName = activity.getClass().getSimpleName();
                Log.d(TAG, sCurrentActivityName);
                if (mOnChangeListener != null) {
                    mOnChangeListener.onCurrentActivityChange(sCurrentActivityName);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                if (activity instanceof FragmentActivity) {
//                    FragmentActivity fragmentActivity = (FragmentActivity) activity;
//                    fragmentActivity.getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(supportFragmentLifecycleCallbacks);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        fragmentActivity.getFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
//                    }
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        activity.getFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
//                    }
//                }
            }
        };

//        supportFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
//            @Override
//            public void onFragmentResumed(FragmentManager fm, Fragment f) {
//                if (f.isAdded() && f.isResumed() && f.isVisible()&& f.getUserVisibleHint()) {
//                    mCurrentFragmentName = f.getClass().getSimpleName();
//                    Log.d(TAG, mCurrentFragmentName);
//                    if (mOnChangeListener != null) {
//                        mOnChangeListener.onCurrentFragmentChange(mCurrentFragmentName);
//                    }
//                }
//            }
//        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fragmentLifecycleCallbacks = new android.app.FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentResumed(android.app.FragmentManager fm, android.app.Fragment f) {
                    if (f.isAdded() && f.isResumed() && f.isVisible()&& f.getUserVisibleHint()) {
                        mCurrentFragmentName = f.getClass().getSimpleName();
                        Log.d(TAG, mCurrentFragmentName);
                        if (mOnChangeListener != null) {
                            mOnChangeListener.onCurrentFragmentChange(mCurrentFragmentName);
                        }
                    }
                }
            };
        }

        app.registerActivityLifecycleCallbacks(sActivityLifecycleCallbacks);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    public interface OnChangeListener {
        void onCurrentActivityChange(String currentActivityName);

        void onCurrentFragmentChange(String currentFragmentName);
    }
}