package dotc.android.happybuy.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.stat.analytics.AnalyticsSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by zhanqiang.mei on 2016/4/18.
 */
public final class Analytics {

    private Analytics() {
    }

    private static Context sAppContext;

    private static final String TAG = "Analytics";

    /*
     * 初始化，必须在应用启动的时候初始化
     */
    public static void init(Context context, String mtaKey) {
        Context appContext = context.getApplicationContext();
        sAppContext = appContext;
        MTAUtils.init(appContext, mtaKey);
    }

    /**
     * 考虑UI事件是重要的，所以会打到GA
     *
     * @param eventId
     * @param label
     * @param value   没有该值，请传入null
     */
    public static void sendABTestUIEvent(final String eventId, final String label, final Long value, final String eid) {
        HBLog.i(TAG+" sendUIEvent eventId "+eventId+" label "+label+" value "+value);
        Log.d(TAG," sendUIEvent eventId "+eventId);
        ThreadManager.runInEventThread(new Runnable() {
            @Override
            public void run() {
                MTAUtils.sendEvent(eventId, label, value == null ? null : String.valueOf(value));
                AnalyticsSdk.getInstance(GlobalContext.get()).sendEvent("ui",eventId,label,String.valueOf(value),null,eid);
            }
        });
    }
    public static void sendABTestUIEvent(final String eventId, final String label, final Long value) {
        sendABTestUIEvent(eventId,label,value, AbConfigManager.getInstance(sAppContext).getConfig().segment_id);
    }


    /**
     * 考虑UI事件是重要的，所以会打到GA
     *
     * @param eventId
     * @param label
     * @param value   没有该值，请传入null
     */
    public static void sendUIEvent(final String eventId, final String label, final Long value) {
        HBLog.i(TAG+" sendUIEvent eventId "+eventId+" label "+label+" value "+value);
        Log.d(TAG," sendUIEvent eventId "+eventId);
        ThreadManager.runInEventThread(new Runnable() {
            @Override
            public void run() {
                MTAUtils.sendEvent(eventId, label, value == null ? null : String.valueOf(value));
                AnalyticsSdk.getInstance(GlobalContext.get()).sendEvent("ui",eventId,label,String.valueOf(value));
            }
        });
    }

    /**
     * add battery uaEvent
     *
     * @param eventId
     * @param label
     * @param value   没有该值，请传入null
     */
    public static void sendUAEvent(final String eventId, final String label, final Long value,final String extra) {
        HBLog.i(TAG+" sendUAEvent eventId "+eventId+" label "+label+" value "+value+" extra "+extra);
        ThreadManager.runInEventThread(new Runnable() {
            @Override
            public void run() {
                MTAUtils.sendEvent(eventId, label, value == null ? null : String.valueOf(value));
            }
        });
    }

    /**
     * 发送不抽样事件
     *
     * @param eventId
     * @param label
     * @param value   没有该值，请传入null
     */
    public static void sendUnsamplingEvent(final String eventId, final String label, final Long value) {
        HBLog.i(TAG+" sendUnsamplingEvent eventId "+eventId+" label "+label+" value "+value);
        ThreadManager.runInEventThread(new Runnable() {
            @Override
            public void run() {
                MTAUtils.sendEvent(eventId, label, value == null ? null : String.valueOf(value));
            }
        });
    }

    public static void sendEvent(String eventId, String label) {
        sendEvent(eventId, label, 0l);
    }

    /**
     * modify 2016-12-28 17:59:24
     * @param eventId
     * @param label
     * @param value
     */
    public static void sendEvent(final String eventId, final String label, final Long value) {
        HBLog.i(TAG+" sendEvent eventId "+eventId+" label "+label+" value "+value);
        ThreadManager.runInEventThread(new Runnable() {
            @Override
            public void run() {
                MTAUtils.sendEvent(eventId, label, value == null ? null : String.valueOf(value));
//                // 考虑到服务器压力，后台事件一个一天只打一次
//                sendEventOncePerDayInternalA(sAppContext, eventId, label, value);
            }
        });
    }
//    private static final String START_EXTRA_CONTENT = "start extra content";
    public static void onActivityStart(Activity activity) {
        AnalyticsSdk.getInstance(activity).onPageBegin (activity,extra(activity));
        MTAUtils.onActivityStart(activity);
        // GA通过自动跟踪开启
    }

    public static void onActivityStop(Activity activity) {
        AnalyticsSdk.getInstance(activity).onPageEnd (activity);
        MTAUtils.onActivityStop(activity);
        // GA通过自动跟踪开启
    }

    private static String extra(Context activity){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", PrefUtils.getString(PrefConstants.Network.uid, ""));
            jsonObject.put("device_id", AppUtil.getDeviceId(activity));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

//    public static void onFragmentStart(String name) {
//        // GAUtils.onFragmentStart(name);
//        MTAUtils.onFragmentStart(name);
//    }
//
//    public static void onFragmentStop(String name) {
//        MTAUtils.onFragmentStop(name);
//    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    private static final String OPD_NAME = "my_opd_event_ids_day";
    private static final String OPD_NAME_NOT_UI_EVENT = "my_opd_event_ids_day_not_ui";

    /**
     * @param ctx
     * @param eventId
     * @param label
     */
    public static void sendUIEventOncePerDay(final Context ctx, final String eventId, final String label) {
        ThreadManager.executeInBackground(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(OPD_NAME, Context.MODE_PRIVATE);
                int day = sp.getInt("last_send_day" + eventId, 0);
                int today = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.CHINA).get(Calendar.DAY_OF_YEAR);
                if (today != day) {
                    sendUIEvent(eventId, label, null);
                    sp.edit().putInt("last_send_day" + eventId, today).apply();
                }
            }
        });
    }

    /** modify by cys  2016-12-28 18:00:24
     * @param ctx
     * @param eventId
     * @param label
     */
    public static void sendEventOncePerDayInternalA(final Context ctx, final String eventId, final String label, final Long value) {
        if (ctx != null) {
            SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(OPD_NAME_NOT_UI_EVENT, Context.MODE_PRIVATE);
            int day = sp.getInt("last_send_day" + eventId, 0);
            int today = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.CHINA).get(Calendar.DAY_OF_YEAR);
            if (today != day) {
                sendEvent(eventId,label,value);
                HBLog.i("checkLineIsInstalled", "sendEventOncePerDayInternalA");
                sp.edit().putInt("last_send_day" + eventId, today).apply();
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////

    private static final String PP_NAME = "my_status_report";
    private static final String PP_DD = "my_status_report_time";

    public static void updateStatusPreferences(final Context ctx, final String key, final String value) {
        ThreadManager.executeInBackground(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(PP_NAME, Context.MODE_PRIVATE);
                sp.edit().putString(key, value).apply();
                checkSendPreferencesIfNeed(ctx);
            }
        });
    }

    private synchronized static void checkSendPreferencesIfNeed(Context ctx) {
        SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(PP_DD, Context.MODE_PRIVATE);
        int day = sp.getInt("last_send_day", 0);
        int today = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.CHINA).get(Calendar.DAY_OF_YEAR);
        if (today != day) {
            if (sendPreferences(ctx)) {
                sp.edit().putInt("last_send_day", today).apply();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean sendPreferences(Context ctx) {
        SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(PP_NAME, Context.MODE_PRIVATE);
        final Map<String, String> all = (Map<String, String>) sp.getAll();
        if (all != null && all.size() > 0) {
            // 上报Preference
            final Set<String> keys = all.keySet();
            ThreadManager.runInEventThread(new Runnable() {
                @Override
                public void run() {
                    for (String key : keys) {
                        String val = all.get(key);
                        MTAUtils.sendEvent(key, val, null); // MTA
                    }
                }
            });
            return true;
        }
        return false;
    }

}

