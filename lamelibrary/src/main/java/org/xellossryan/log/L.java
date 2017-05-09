package org.xellossryan.log;

import android.util.Log;

/**
 * Created by Liang on 2017/5/3.
 */

public class L {
    public static final String TAG = "AudioEncoder";
    public static boolean ON = true;

    public static void d(String t) {
        if (ON)
            Log.d(TAG, t);
    }

    public static void i(String t) {
        if (ON)
            Log.i(TAG, t);
    }

    public static void w(String t) {
        if (ON)
            Log.w(TAG, t);
    }

    public static void e(String t) {
        if (ON)
            Log.e(TAG, t);
    }

    public static void v(String t) {
        if (ON)
            Log.v(TAG, t);
    }
}
