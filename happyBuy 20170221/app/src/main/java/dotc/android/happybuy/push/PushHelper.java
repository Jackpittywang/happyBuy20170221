package dotc.android.happybuy.push;

import android.content.Context;

import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 */
public class PushHelper {

    private final static String GCM_TOKEN = "gcm_token";
    private final static String GCM_LAST_UPDATE_TIME = "gcm_last_update_time";
    private final static String GCM_LAST_CHECK_TIME = "gcm_last_check_time";
    private final static String LAST_UPLOAD_GCM_TOKEN = "last_upload_gcm_token";
    private final static String UPLOAD_GCM_TOKEN_SUCCESS = "upload_gcm_token_success";

    public static String getGCMToken(Context context){
        return PrefUtils.getString(context,GCM_TOKEN,null);
    }

    public static void setGCMToken(Context context, String token){
        PrefUtils.putString(context, GCM_TOKEN, token);
    }


    public static void setGCMUpdateTime(Context context, long time){
        PrefUtils.putLong(context, GCM_LAST_UPDATE_TIME, time);
    }

    public static long getGCMUpdateTime(Context context){
        return PrefUtils.getLong(context, GCM_LAST_UPDATE_TIME, 0);
    }


    public static void setGCMCheckTime(Context context, long time){
        PrefUtils.putLong(context, GCM_LAST_CHECK_TIME, time);
    }

    public static long getGCMCheckTime(Context context){
        return PrefUtils.getLong(context, GCM_LAST_CHECK_TIME, 0);
    }


    public static void setLastUploadGCMToken(Context context, String token){
        PrefUtils.putString(context, LAST_UPLOAD_GCM_TOKEN, token);
    }

    public static String getLastUploadGCMToken(Context context){
        return PrefUtils.getString(context, LAST_UPLOAD_GCM_TOKEN, null);
    }


    public static void setUploadGCMTokenSuccess(Context context, boolean success){
        PrefUtils.putBoolean(context, UPLOAD_GCM_TOKEN_SUCCESS, success);
    }

    public static boolean getUploadGCMTokenSuccess(Context context, boolean defaultValue){
        return PrefUtils.getBoolean(context, UPLOAD_GCM_TOKEN_SUCCESS, defaultValue);
    }

}
