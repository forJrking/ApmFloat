package com.forjrking.apmlib;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forjrking.apmlib.fps.ChoreographerCompat;
import com.forjrking.apmlib.fps.FpsFrameCallback;

public class ApmView extends LinearLayout implements Sampler.OnSamplerRun, CurAcFr.OnChangeListener {

    private static final int UPDATE_INTERVAL_MS = 900;

    private final TextView mFpsTextView, mCurTextView;
    private final TextView mCpuTextView;
    private final TextView mMemTextView;
    private final FpsFrameCallback mFrameCallback;
    private final FPSMonitorRunnable mFPSMonitorRunnable;
    private final Sampler mSampler;
    private final Handler mHandler;
    private final CurAcFr mCurAcFrSampler;
    private final ThreadMonitor mThMonitor;

    public ApmView(Context context) {
        super(context);
        setBackgroundColor(Color.parseColor("#a4141823"));
        setMinimumWidth(250);
        setMinimumHeight(80);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        setLayoutParams(params);
        setOrientation(VERTICAL);
        mCurTextView = addText(context);
        mFpsTextView = addText(context);
        mCpuTextView = addText(context);
        mMemTextView = addText(context);
        mFrameCallback = new FpsFrameCallback(ChoreographerCompat.getInstance());
        mFPSMonitorRunnable = new FPSMonitorRunnable();

        mCurAcFrSampler = new CurAcFr();
        mCurAcFrSampler.init(context);
        mCurAcFrSampler.setOnChangeListener(this);
        mSampler = new Sampler();
        mSampler.init(context);
        mSampler.setOnSamplerRun(this);
        mThMonitor= new ThreadMonitor();
        HandlerThread apm = new HandlerThread("apm");
        apm.start();
        mHandler = new Handler(apm.getLooper());
    }

    private TextView addText(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        textView.setTextColor(Color.WHITE);
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.setMargins(5, 5, 5, 5);
        addView(textView, layoutParams);
        return textView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFrameCallback.reset();
        mFrameCallback.start();
        mFPSMonitorRunnable.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFrameCallback.stop();
        mFPSMonitorRunnable.stop();
    }

    private void setCurrentFPS(final double currentFPS, int droppedUIFrames, int total4PlusFrameStutters) {
        post(new Runnable() {
            @Override
            public void run() {
                if (currentFPS <= 30) {
                    mFpsTextView.setTextColor(Color.parseColor("#F44336"));
                } else if (currentFPS <= 40) {
                    int color = Color.parseColor("#FF9800");
                    mFpsTextView.setTextColor(color);
                } else {
                    mFpsTextView.setTextColor(Color.WHITE);
                }

                String fps = String.format("FPS %.1f | CFD %s", currentFPS,mThMonitor.currentFD());
                String cpu = String.format("CPU %.1f | THD %s", mSampler.mCpu, Thread.activeCount());
                String mem = String.format("MEM %.1f | JVM %.1f/%.1f", mSampler.mMem, mSampler.mUse, mSampler.mMax);
                mFpsTextView.setText(fps);
                mCpuTextView.setText(cpu);
                mMemTextView.setText(mem);
                //暂时注释，排查oom问题
//                ApmRecorder.getInstance().cpu(cpu);
//                ApmRecorder.getInstance().fps(fps);
//                ApmRecorder.getInstance().mem(mem);
                // DES: 记录线程
//                ThreadMonitor.recordThreads();
            }
        });
    }

    @Override
    public void sampleCPU(final double cpu) {
    }

    @Override
    public void sampleMemory(double mem,float maxJvm,float useJvm) {
    }

    @Override
    public void onCurrentActivityChange(String currentActivityName) {
        mCurTextView.setText(currentActivityName);
    }

    @Override
    public void onCurrentFragmentChange(String currentFragmentName) {
//        mCurTextView.setText(mCurTextView.getText() + "$" + currentFragmentName);
    }

    /**
     * Timer that runs every UPDATE_INTERVAL_MS ms and updates the currently displayed FPS.
     */
    private class FPSMonitorRunnable implements Runnable {

        private boolean mShouldStop = false;
        private int mTotalFramesDropped = 0;
        private int mTotal4PlusFrameStutters = 0;

        @Override
        public void run() {
            if (mShouldStop) {
                return;
            }
            mTotalFramesDropped += mFrameCallback.getExpectedNumFrames() - mFrameCallback.getNumFrames();
            mTotal4PlusFrameStutters += mFrameCallback.get4PlusFrameStutters();
            setCurrentFPS(mFrameCallback.getFPS(), mTotalFramesDropped, mTotal4PlusFrameStutters);
            mFrameCallback.reset();
            mSampler.run();
            mHandler.postDelayed(this, UPDATE_INTERVAL_MS);
        }

        public void start() {
            mShouldStop = false;
            mHandler.post(this);
        }

        public void stop() {
            mShouldStop = true;
        }
    }
}