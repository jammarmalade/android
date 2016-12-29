package com.jam00.www.util;

import android.util.Log;

/**
 * Created by Administrator on 2016/6/11.
 * 日志工具类
 */
public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int LEVEL = VERBOSE;
    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, cMsg(msg));
        }
    }
    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, cMsg(msg));
        }
    }
    public static void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag, cMsg(msg));
        }
    }
    public static void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag, cMsg(msg));
        }
    }
    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag, cMsg(msg));
        }
    }
    private static String cMsg(String msg){
        return Utility.getNowTime()+" "+msg;
    }
}
