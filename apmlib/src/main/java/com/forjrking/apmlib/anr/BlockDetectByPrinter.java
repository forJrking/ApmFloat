package com.forjrking.apmlib.anr;

import android.os.Looper;
import android.util.Printer;
/**
 * @Description: 基于Android项目组在Looper中的println，通过监测Dispatching和finished之间的耗时，来实现检测UI线程卡顿
 * @Author: WinterHuang
 * @CreateDate: 20190905
 * @Version: 1.0.0
 */
public class BlockDetectByPrinter {
    public static void start() {

        Looper.getMainLooper().setMessageLogging(new Printer() {

            private static final String START = ">>>>> Dispatching";
            private static final String END = "<<<<< Finished";

            @Override
            public void println(String x) {
                if (x.startsWith(START)) {
                    LogMonitor.getInstance().startMonitor();
                }
                if (x.startsWith(END)) {
                    LogMonitor.getInstance().removeMonitor();
                }
            }
        });

    }
}
