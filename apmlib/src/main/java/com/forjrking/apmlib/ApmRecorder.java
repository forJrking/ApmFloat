package com.forjrking.apmlib;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @DES: 日志记录器
 * @AUTHOR: 岛主
 * @TIME: 2019/7/17 0017 上午 11:51
 */
public class ApmRecorder {

    public static final String YAPM = "NXAPM";

    public static ApmRecorder getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static ApmRecorder instance = new ApmRecorder();

        private Holder() {
        }
    }

    private HashMap<String, File> files = new HashMap<String, File>();

    private static final String FPS = "FPS.log";
    private static final String CPU = "CPU.log";
    private static final String MEM = "MEM.log";

    private ApmRecorder() {
        String filePath = getPath(YAPM);
        String[] fileNames = {FPS, CPU, MEM};
        addRecordFile(filePath, fileNames);
    }

    public void addRecordFile(String filePath, String[] fileNames) {
        for (String fileName : fileNames) {
            File file = new File(filePath, fileName);
            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            files.put(fileName, file);
        }
    }

    public void cpu(String cpu) {
        writeTxt(files.get(CPU), cpu);
    }

    public void fps(String fps) {
        writeTxt(files.get(FPS), fps);
    }

    public void mem(String mem) {
        writeTxt(files.get(MEM), mem);
    }

    private void writeTxt(final File file, final String content) {
        //生成文件夹之后，再生成文件，不然会出错
        // 每次写入时，都换行写
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String strContent = content + "\r\n";
                RandomAccessFile raf = null;
                try {
                    raf = new RandomAccessFile(file, "rws");
                    raf.seek(file.length());
                    raf.write(strContent.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (raf != null) {
                        try {
                            raf.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private String getPath(String filePath) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date data = new Date();
        String format = sdf.format(data);
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + filePath + File.separator + format;
    }

}