package com.forjrking.tools.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DES: 工厂类 主要目的管理线程调度优先级和 对线程重命名
 * CHANGED: 岛主
 * TIME: 2019/5/16 0016 下午 5:22
 * <p>
 * {@link java.util.concurrent.Executors.DefaultThreadFactory} 默认线程优先级
 */
public final class FreedomThreadFactory extends AtomicLong implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final int priority;

    FreedomThreadFactory(String prefix, int priority) {
        SecurityManager s = System.getSecurityManager();
        group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        this.priority = priority;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(group, r, namePrefix + getAndIncrement(), 0) {
            @Override
            public void run() {
//                try {
                super.run();
//                } catch (Throwable t) {
//                    Log.e("ThreadUtils", "Request threw uncaught throwable", t);
//                }
            }
        };
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        t.setPriority(priority);
        return t;
    }
}