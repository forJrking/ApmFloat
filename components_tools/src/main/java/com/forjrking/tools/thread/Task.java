package com.forjrking.tools.thread;

import android.util.Log;
/**
 * DES: 任务中心主要处理线程工具回调
 * CHANGED: 岛主
 * TIME: 2019/5/16 0016 下午 5:31
 * */
public abstract class Task<T> implements Runnable {

    public abstract static class SimpleTask<T> extends Task<T> {
        @Override
        public void onSuccess(T result) {

        }

        @Override
        public void onCancel() {
            Log.e("ThreadUtils", "onCancel: " + Thread.currentThread());
        }

        @Override
        public void onFail(Throwable t) {
            Log.e("ThreadUtils", "onFail: ", t);
        }

    }
    /*** DES: 是否为定时等任务*  */
    public boolean isSchedule;

    private volatile int state;
    private static final int NEW = 0;
    private static final int COMPLETING = 1;
    private static final int CANCELLED = 2;
    private static final int EXCEPTIONAL = 3;

    public Task() {
        state = NEW;
    }
    /*** DES: io线程*  */
    public abstract T doInBackground();
    /*** DES: 成功主线程*  */
    public abstract void onSuccess(T result);
    /*** DES: 取消回调*  */
    public abstract void onCancel();
    /*** DES: 出现异常*  */
    public abstract void onFail(Throwable t);


    @Override
    public void run() {
        try {
            final T result = doInBackground();
            if (state != NEW) return;

            if (isSchedule) {
                Deliver.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(result);
                    }
                });
            } else {
                state = COMPLETING;
                Deliver.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(result);
                        ThreadUtils.removeScheduleByTask(Task.this);
                    }
                });
            }
        } catch (final Throwable throwable) {
            if (state != NEW) return;

            state = EXCEPTIONAL;
            Deliver.post(new Runnable() {
                @Override
                public void run() {
                    onFail(throwable);
                    ThreadUtils.removeScheduleByTask(Task.this);
                }
            });
        }
    }

    public void cancel() {
        if (state != NEW) return;

        state = CANCELLED;
        Deliver.post(new Runnable() {
            @Override
            public void run() {
                onCancel();
                ThreadUtils.removeScheduleByTask(Task.this);
            }
        });
    }
}