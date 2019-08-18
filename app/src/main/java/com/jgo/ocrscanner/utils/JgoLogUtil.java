package com.jgo.ocrscanner.utils;

import android.util.Log;

/**
 * Created by ke-oh on 2019/08/16.
 *
 */

public class JgoLogUtil {
    private static boolean debug = true;

    public static void d(String tag, String logMsg) {
        if (debug) {
            Log.d(tag, logMsg);
        }
    }

    public static void e(String tag, String logMsg) {
        if (debug) {
            Log.e(tag, logMsg);
        }
    }

    public static void w(String tag, String logMsg) {
        if (debug) {
            Log.w(tag, logMsg);
        }
    }
}
