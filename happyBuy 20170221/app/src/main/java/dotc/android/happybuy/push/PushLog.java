package dotc.android.happybuy.push;

import android.util.Log;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/1/21.
 */
public class PushLog {

    private final static boolean enable = true;
    private final static String TAG = "hb_push";

    public static void logD(String tag,String message){
        if(enable&&HBLog.isLogEnable()){
            HBLog.d(TAG+ " "+tag+" "+message);
            Log.d(TAG, tag + " " + message);
        }
    }

    public static void logW(String tag,String message){
        if(enable&&HBLog.isLogEnable()){
            HBLog.w(TAG+ " "+tag+" "+message);
            Log.w(TAG, tag + " " + message);
        }
    }
}
