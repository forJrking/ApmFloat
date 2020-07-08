package com.forjrking.apmlib;

import android.os.Looper;
import android.os.Process;

import androidx.collection.ArrayMap;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @Description:
 * @Author: 岛主
 * @CreateDate: 2019/9/11 0011 下午 3:01
 * @Version: 1.0.0
 */
public class ThreadMonitor {

    /**
     * DES: 线程总数
     * TIME: 2019/8/2 0002 上午 11:44
     */
    public static Collection<Thread> threads() {
        try {
            final ThreadGroup root = Looper.getMainLooper().getThread().getThreadGroup().getParent();
            final Thread[] src = new Thread[root.activeCount()];
            final int n = root.enumerate(src);

            if (n != src.length) {
                final Thread[] target = new Thread[n];
                System.arraycopy(src, 0, target, 0, n);
                return Arrays.asList(target);
            } else {
                return Arrays.asList(src);
            }
        } catch (final Throwable t) {
            return Thread.getAllStackTraces().keySet();
        }
    }


    /**
     * DES: 获取当前打开文件流总数
     * TIME: 2019/9/11 0011 下午 3:15
     */
    public int currentFD() {
        File fdFile = new File("/proc/" + Process.myPid() + "/fd");
        File[] files = fdFile.listFiles();
        if (files != null) {
            return files.length;
        } else {
            return 0;
        }
    }

    static final String sepOtherThreads = "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---";
    static final String sepOtherThreadsEnding = "+++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++";

    public static ArrayMap<String, Integer> getThreadsCount() {
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            Thread thd = entry.getKey();
            String key = thd.toString();
            if (arrayMap.containsKey(key)) {
                Integer integer = arrayMap.get(key);
                arrayMap.put(key, integer++);
            } else {
                arrayMap.put(key, 1);
            }
        }
        return arrayMap;
    }
    /**
     * DES: 获取线程对应数量信息
     * AUTHOR: 岛主
     * TIME: 2020/3/18 0018 上午 9:42
     **/
    public static String getThreadsCountInfo() {
        ArrayMap<String, Integer> threadsCount = getThreadsCount();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : threadsCount.entrySet()) {
            sb.append("thread name：").append(entry.getKey())
                    .append("\t").append("count：").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }


    public static String getOtherThreadsInfo(Thread crashedThread) {

        int thdDumped = 0;

        StringBuilder sb = new StringBuilder();
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {

            Thread thd = entry.getKey();
            StackTraceElement[] stacktrace = entry.getValue();
            //skip the crashed thread
            String name = crashedThread == null ? "null" : crashedThread.getName();
            if (thd.getName().equals(name)) continue;

            sb.append(sepOtherThreads + "\n");
            sb.append("tid: ").append(thd.getId()).append(", name: ").append(thd.getName()).append(" <<<\n");
            sb.append("\n");
            sb.append("java stacktrace:\n");
            for (StackTraceElement element : stacktrace) {
                sb.append("    at ").append(element.toString()).append("\n");
            }
            sb.append("\n");

            thdDumped++;
        }

        if (map.size() > 1) {
            if (thdDumped == 0) {
                sb.append(sepOtherThreads + "\n");
            }

            sb.append("total JVM threads (exclude the crashed thread): ").append(map.size() - 1).append("\n");
            sb.append("dumped JVM threads:").append(thdDumped).append("\n");
            sb.append(sepOtherThreadsEnding + "\n");
        }

        return sb.toString();
    }


}
