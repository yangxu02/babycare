package com.linkx.babycare.utils;

import android.os.Environment;
import android.text.format.DateFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created by ulyx.yang on 2016/9/16.
 */
public class IOUtil {
    private final static String DAY_FORMAT = "yyyyMMdd";
    private final static String DATA_DIR = ".babycare/data";
    private final static String TMP_DIR = ".babycare/tmp";

    public static String dataFileDir() {
        File dir = Environment.getExternalStorageDirectory();
        return Joiner.on('/').join(dir.getAbsolutePath(), DATA_DIR);
    }

    public static String dataFileName(String tag) {
        return dataFileName(tag, System.currentTimeMillis());
    }

    public static String dataFileName(String tag, String dayStr) {
        File dir = Environment.getExternalStorageDirectory();
        return Joiner.on('/').join(dir.getAbsolutePath(), DATA_DIR, dayStr, tag);
    }

    public static String dataFileName(String tag, long mills) {
        String dayStr = DateFormat.format(DAY_FORMAT, mills).toString();
        return dataFileName(tag, dayStr);
    }

     public static String tmpFileName(String tag) {
         File dir = Environment.getExternalStorageDirectory();
         return Joiner.on('/').join(dir.getAbsolutePath(), TMP_DIR, tag);
    }


    public static String dayStr(long mills) {
        return DateFormat.format(DAY_FORMAT, mills).toString();
    }

    public static void appendLine(String fileName, String data) throws IOException {
        File file = new File(fileName);
        Files.createParentDirs(file);
        Files.append(data + "\n", file, Charsets.UTF_8);
    }

    public static void writeLine(String fileName, String data) throws IOException {
        File file = new File(fileName);
        Files.createParentDirs(file);
        Files.write(data + "\n", file, Charsets.UTF_8);
    }

    public static String readFirstLine(String fileName) throws IOException {
        File file = new File(fileName);
        return Files.readFirstLine(file, Charsets.UTF_8);
    }

    public static void truncate(String fileName) throws IOException {
        File file = new File(fileName);
        Files.write("\r", file, Charsets.UTF_8);
    }
}
