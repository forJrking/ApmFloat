package com.forjrking.apmlib;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Usage:
 * Sampler.getInstance().init(getApplicationContext(), 100L);
 * Sampler.getInstance().start();
 */
public class Sampler implements Runnable {
    private static final float UNIT_M = 1024 * 1024;
    private ActivityManager activityManager;
    private Long lastCpuTime;
    private Long lastAppCpuTime;
    private RandomAccessFile procStatFile;
    private RandomAccessFile appStatFile;
    private boolean mAboveAndroidO;
    public double mCpu;
    public double mMem;
    public float mMax;
    public float mUse;


    // freq为采样周期
    public void init(Context context) {
        mAboveAndroidO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void run() {
        mCpu = mAboveAndroidO ? sampleCPUForO() : sampleCPU();
        mMem = sampleMemory();
        mMax = maxJvmMemory();
        mUse = javaJvmMemory();
        if (mRun != null) {
            mRun.sampleCPU(mCpu);
            mRun.sampleMemory(mMem, mMax, mUse);
        }
    }

    private double sampleCPU() {
        long cpuTime;
        long appTime;
        double sampleValue = 0.0D;
        try {
            if (procStatFile == null || appStatFile == null) {
                procStatFile = new RandomAccessFile("/proc/stat", "r");
                appStatFile = new RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r");
            } else {
                procStatFile.seek(0L);
                appStatFile.seek(0L);
            }
            String procStatString = procStatFile.readLine();
            String appStatString = appStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (lastCpuTime == null && lastAppCpuTime == null) {
                lastCpuTime = cpuTime;
                lastAppCpuTime = appTime;
                return sampleValue;
            }
            sampleValue = ((double) (appTime - lastAppCpuTime) / (double) (cpuTime - lastCpuTime)) * 100D;
            lastCpuTime = cpuTime;
            lastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sampleValue;
    }

    private double sampleMemory() {
        double mem = 0.0D;
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
            if (memInfo.length > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                final int totalPss = memInfo[0].getTotalPss();
                if (totalPss >= 0) {
                    // Mem in MB
                    mem = totalPss / 1024.0D;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    private float javaJvmMemory() {
        float useMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / UNIT_M;
        return useMem;
    }

    private float maxJvmMemory() {
        float max = Runtime.getRuntime().maxMemory() / UNIT_M;
        return max;
    }

    private float sampleCPUForO() {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("top -n 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cpuIndex = -1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                int tempIndex = getCPUIndex(line);
                if (tempIndex != -1) {
                    cpuIndex = tempIndex;
                    continue;
                }
                if (line.startsWith(String.valueOf(Process.myPid()))) {
                    if (cpuIndex == -1) {
                        continue;
                    }
                    String[] param = line.split("\\s+");
                    if (param.length <= cpuIndex) {
                        continue;
                    }
                    String cpu = param[cpuIndex];
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"));
                    }
                    float rate = Float.parseFloat(cpu) / Runtime.getRuntime().availableProcessors();
                    return rate;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return 0;
    }

    private int getCPUIndex(String line) {
        if (line.contains("CPU")) {
            String[] titles = line.split("\\s+");
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].contains("CPU")) {
                    return i;
                }
            }
        }
        return -1;
    }

    OnSamplerRun mRun;

    public void setOnSamplerRun(OnSamplerRun run) {
        mRun = run;
    }

    interface OnSamplerRun {

        void sampleCPU(double cpu);

        /**
         * @param mem    占用内存
         * @param maxJvm 虚拟机可申请最大内存
         * @param useJvm 已用虚拟机内存
         */
        void sampleMemory(double mem, float maxJvm, float useJvm);
    }
}
