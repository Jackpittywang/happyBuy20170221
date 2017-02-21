package dotc.android.happybuy.log;

import android.util.Log;

import build.Environment;

/**
 * Created by wangjun on 16/3/28.
 */
public class HBLog {
    private final static boolean DEBUG = Environment.LOG_ENABLE;
    private static final String TAG = "hb";

    public static boolean isLogEnable(){
        return DEBUG;
    }

    public static void i(String tag,Object o) {
        if (DEBUG) {
            Log.i(TAG, tag+" "+String.valueOf(o));
        }
    }

    public static void i(Object o) {
        if (DEBUG) {
            Log.i(TAG, String.valueOf(o));
        }
    }


    public static void d(String tag,Object o) {
        if (DEBUG) {
            Log.d(TAG, tag+" "+String.valueOf(o));
        }
    }

    public static void d(Object o) {
        if (DEBUG) {
            Log.i(TAG, String.valueOf(o));
        }
    }

    public static void e(String tag,Object o) {
        if (DEBUG) {
            Log.e(TAG, tag+" "+String.valueOf(o));
        }
    }

    public static void e(Object o) {
        Log.e(TAG, String.valueOf(o));
    }

    public static void e(Object o,Throwable t) {
        t.printStackTrace();
        Log.e(TAG, String.valueOf(o) + " error:" + t.getMessage());
    }

    public static void w(Object o) {
        if (DEBUG) {
            Log.w(TAG, String.valueOf(o));
        }
    }

    public static void w(String tag,Object o) {
        if (DEBUG) {
            Log.w(TAG, tag+" "+String.valueOf(o));
        }
    }

//    public static void w(Object o,Throwable t) {
//        if (DEBUG) {
//            t.printStackTrace();
//            Log.w(TAG, String.valueOf(o) + " error:" + t.getMessage());
//        }
//    }
    public static void v(Object o) {
        if (DEBUG) {
            Log.v(TAG, String.valueOf(o));
        }
    }

    public static void v(String tag,Object o) {
        if (DEBUG) {
            Log.v(TAG, tag+" "+String.valueOf(o));
        }
    }

    public static void d(Object... os) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o: os) {
                stringBuilder.append(String.valueOf(o)).append(" ");
            }
            Log.d(TAG, stringBuilder.toString());
        }
    }
}
