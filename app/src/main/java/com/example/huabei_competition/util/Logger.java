package com.example.huabei_competition.util;

import android.util.Log;

/**
 * Create by FanChenYang at 2021/4/14
 */
public class Logger {
    public static final String TAG = "Log";
    public static final boolean DEBUG = true;

    public static String getMessage(Object o) {
        return o == null ? "null" : o.toString();
    }

    public static void i(Object msg) {
        if (DEBUG)
            Log.i(TAG, getMessage(msg));
    }

    public static void d(Object msg) {
        if (DEBUG)
            Log.d(TAG, getMessage(msg));
    }

    public static void e(Object msg) {
        if (DEBUG)
            Log.e(TAG, getMessage(msg));
    }

    public static void w(Object msg) {
        if (DEBUG)
            Log.w(TAG, getMessage(msg));
    }
}
