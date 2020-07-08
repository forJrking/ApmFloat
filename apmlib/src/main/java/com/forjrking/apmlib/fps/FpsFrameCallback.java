package com.forjrking.apmlib.fps;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.TreeMap;

/**
 * DES: FPS采集 使用对象 {@link FpsInfo}
 * CHANGED: 岛主
 * TIME: 2019/4/26 0026 下午 7:16
 */
public class FpsFrameCallback extends ChoreographerCompat.FrameCallback {

    @Keep
    public static class FpsInfo {

        public final int totalFrames; // 总帧
        public final int totalExpectedFrames; // 预计帧
        public final int total4PlusFrameStutters; //掉帧次数
        public final double fps; //fps
        public final int totalTimeMs; // 总耗时

        public FpsInfo(
                int totalFrames,
                int totalExpectedFrames,
                int total4PlusFrameStutters,
                double fps,
                int totalTimeMs) {
            this.totalFrames = totalFrames;
            this.totalExpectedFrames = totalExpectedFrames;
            this.total4PlusFrameStutters = total4PlusFrameStutters;
            this.fps = fps;
            this.totalTimeMs = totalTimeMs;
        }
    }

    private static final double EXPECTED_FRAME_TIME = 16.9;

    private final ChoreographerCompat mChoreographer;

    private boolean mShouldStop = false;
    private long mFirstFrameTime = -1;
    private long mLastFrameTime = -1;
    private int mNumFrameCallbacks = 0;
    private int mExpectedNumFramesPrev = 0;
    private int m4PlusFrameStutters = 0;
    private boolean mIsRecordingFpsInfoAtEachFrame = false;
    private @Nullable
    TreeMap<Long, FpsInfo> mTimeToFps;

    public FpsFrameCallback(ChoreographerCompat choreographer) {
        mChoreographer = choreographer;
    }

    @Override
    public void doFrame(long l) {
        if (mShouldStop) {
            return;
        }

        if (mFirstFrameTime == -1) {
            mFirstFrameTime = l;
        }
        mLastFrameTime = l;
        mNumFrameCallbacks++;
        // DES: 预期应该绘制帧
        int expectedNumFrames = getExpectedNumFrames();
        // DES: 掉帧 4个以上数据掉帧
        int framesDropped = expectedNumFrames - mExpectedNumFramesPrev - 1;
        if (framesDropped >= 4) {
            m4PlusFrameStutters++;
        }
        if (mIsRecordingFpsInfoAtEachFrame) {
            if (mTimeToFps == null) {
                return;
            }
            FpsInfo info = new FpsInfo(
                    getNumFrames(),
                    expectedNumFrames,
                    m4PlusFrameStutters,
                    getFPS(),
                    getTotalTimeMS());
            mTimeToFps.put(System.currentTimeMillis(), info);
        }
        mExpectedNumFramesPrev = expectedNumFrames;

        mChoreographer.postFrameCallback(this);
    }

    public void start() {
        mShouldStop = false;
        mChoreographer.postFrameCallback(this);
    }

    public void startAndRecordFpsAtEachFrame() {
        mTimeToFps = new TreeMap<Long, FpsInfo>();
        mIsRecordingFpsInfoAtEachFrame = true;
        start();
    }

    public void stop() {
        mShouldStop = true;
        mChoreographer.removeFrameCallback(this);
    }

    public double getFPS() {
        if (mLastFrameTime == mFirstFrameTime) {
            return 0;
        }
        return ((double) (getNumFrames()) * 1e9) / (mLastFrameTime - mFirstFrameTime);
    }

    public int getNumFrames() {
        return mNumFrameCallbacks - 1;
    }

    public int getExpectedNumFrames() {
        double totalTimeMS = getTotalTimeMS();
        int expectedFrames = (int) (totalTimeMS / EXPECTED_FRAME_TIME + 1);
        return expectedFrames;
    }

    public int get4PlusFrameStutters() {
        return m4PlusFrameStutters;
    }

    public int getTotalTimeMS() {
        return (int) ((double) mLastFrameTime - mFirstFrameTime) / 1000000;
    }

    /**
     * Returns the FpsInfo as if stop had been called at the given upToTimeMs. Only valid if
     * monitoring was started with {@link #startAndRecordFpsAtEachFrame()}.
     */
    public @Nullable
    FpsInfo getFpsInfo(long upToTimeMs) {
        if (mTimeToFps == null) {
            Log.e("FPS", "FPS was not recorded at each frame!must be call startAndRecordFpsAtEachFrame()");
            return null;
        }
        Map.Entry<Long, FpsInfo> bestEntry = mTimeToFps.floorEntry(upToTimeMs);
        if (bestEntry == null) {
            return null;
        }
        return bestEntry.getValue();
    }

    public void reset() {
        mFirstFrameTime = -1;
        mLastFrameTime = -1;
        mNumFrameCallbacks = 0;
        m4PlusFrameStutters = 0;
        mIsRecordingFpsInfoAtEachFrame = false;
        mTimeToFps = null;
    }
}