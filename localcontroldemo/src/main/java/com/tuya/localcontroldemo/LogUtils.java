package com.tuya.localcontroldemo;

import android.util.Log;

import com.tuya.smart.android.common.utils.L;

/**
 * log类，输出本地日志
 */

public class LogUtils {
    private static String TAG = "tuya_local_server";

    public static void i(String message) {
        L.i(TAG, message);
    }

    public static void d(String message) {
        L.d(TAG, message);
    }

    public static void w(String message) {
        L.w(TAG, message);
    }

    public static void w(Throwable e) {
        L.w(TAG, e.toString());
    }

    public static void e(Throwable e) {
        if (L.getLogStatus()) {
            Log.e("Tuya", TAG + e.toString(), e);
        }
    }

    public static void e(String e) {
        L.e(TAG, e);
    }

}
