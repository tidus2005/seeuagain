package com.sansec.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil {
    public static void write(String filename, byte[] data) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(data);
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] read(String filename) {
        byte[] data = null;
        try {
            FileInputStream in = new FileInputStream(filename);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (Exception ex) {
            System.out.println("读文件失败:" + ex.getMessage());
            return null;
        }

        return data;
    }
}
