package dotc.android.happybuy.http;

import android.util.Log;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/9/5.
 */
public class HttpLog {

    private final static boolean enable = true;
    private final static String TAG = "hb_http";

    public static void d(String tag,String message){
        if(enable&&HBLog.isLogEnable()){
//            HBLog.d(TAG+ " "+tag+" "+message);
            Log.d(TAG, tag + " " + message);
        }
    }

    public static void w(String tag,String message){
        if(enable&&HBLog.isLogEnable()){
//            HBLog.w(TAG+ " "+tag+" "+message);
            Log.w(TAG, tag + " " + message);
        }
    }

}
