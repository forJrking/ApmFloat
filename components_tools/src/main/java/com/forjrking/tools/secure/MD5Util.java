package com.forjrking.tools.secure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {


    private static final String MD5 = "MD5";

    public static String md5(byte[] value) {
        if (value == null)
            return "";
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            md.update(value);
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 对一个文件获取md5值
     *
     * @return md5串
     */
    public static String file2md5(File file) {
        String value = "";
        FileInputStream in = null;
        try {
            int numRead;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            while ((numRead = in.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
            value = String.format("%32s", value).replace(' ', '0');
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return value;
    }

}
