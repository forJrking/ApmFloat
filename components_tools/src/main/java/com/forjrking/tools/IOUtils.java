package com.forjrking.tools;

import java.io.Closeable;
import java.io.IOException;

/**
 * DES:
 * TIME: 2018/12/6 0006 下午 2:06
 */
public class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
                closeable = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源
     */
    private void close(Closeable... closeables) {
        int length = closeables.length;
        try {
            for (int i = 0; i < length; i++) {
                Closeable closeable = closeables[i];
                if (null != closeable)
                    closeables[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }
}
