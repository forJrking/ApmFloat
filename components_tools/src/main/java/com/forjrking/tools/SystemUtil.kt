package com.forjrking.tools

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.Context
import android.os.Process
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

object SystemUtil {
    /**
     * DES: 是否是同一进程
     */
    fun isMainProcess(cxt: Context): Boolean {
        var processName = getProcessName(Process.myPid())
        // DES: 获取当前包名
        val packageName = cxt.packageName
        // DES: 判断进程名称是否为空
        if (processName == null) {
            // DES: 进程名称取不到
            processName = packageName
        }
        // DES: 多进程只初始化一次
        return packageName != null && packageName == processName
    }

    // DES: 获取当前进程名称,多进程只初始化一次
    fun getProcessName(cxt: Context): String? {
        try {
            val pid = Process.myPid()
            val am =
                cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps =
                am.runningAppProcesses
            if (runningApps != null && !runningApps.isEmpty()) {
                for (procInfo in runningApps) {
                    if (procInfo.pid == pid) {
                        return procInfo.processName
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }

    val isVMMultidexCapable: Boolean
        get() = isVMMultidexCapable(System.getProperty("java.vm.version"))

    //MultiDex 拷出来的的方法，判断VM是否支持多dex
    fun isVMMultidexCapable(versionString: String?): Boolean {
        var isMultidexCapable = false
        if (versionString != null) {
            val matcher =
                Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString)
            if (matcher.matches()) {
                try {
                    val major = matcher.group(1).toInt()
                    val minor = matcher.group(2).toInt()
                    isMultidexCapable = major > 2 || major == 2 && minor >= 1
                } catch (var5: NumberFormatException) {
                }
            }
        }
        Log.i(
            "MultiDex",
            "VM with version " + versionString + if (isMultidexCapable) " has multidex support" else " does not have multidex support"
        )
        return isMultidexCapable
    }

    /**
     * 判断当前程序是否在前台
     */
    fun onForeground(context: Context?): Boolean {
        val activityManager =
            context?.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        val appProcesses = activityManager?.runningAppProcesses
        if (appProcesses == null || appProcesses.size <= 0) {
            return true
        }
        var isForeground = false;
        val packageName = context.packageName
        for (appProcess in appProcesses.filter { it.processName.contains(packageName) }) {
            if (appProcess.processName == packageName) {
//                主进程
                val importance = appProcess.importance
                isForeground =
                    importance == IMPORTANCE_FOREGROUND || importance == IMPORTANCE_VISIBLE
            } else if (appProcess.processName.startsWith("$packageName:")) {
                //子进程
                val importance = appProcess.importance
                isForeground =
                    importance == IMPORTANCE_FOREGROUND || importance == IMPORTANCE_VISIBLE
            }
        }
        return isForeground
    }
}