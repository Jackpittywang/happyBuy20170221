package dotc.android.happybuy.modules.push;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.update.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.NotifyInfo;
import dotc.android.happybuy.modules.active.NoTopUpActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.PushLog;
import dotc.android.happybuy.modules.splash.SplashActivity;

/**
 * Created by wangzhiyuan on 16/12/07.
 *
 * 开启用户n天未充值系统提示
 *
 */
public class RechargeAlarmService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    public static final String EVENT = "event";
    public static final String HAVE_RECHARGE = "have_recharge";
    public static final String NO_RECHARGE_LOOP = "no_recharge_loop";


    @Override
    public void onCreate() {
        super.onCreate();
        PushLog.logD(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PushLog.logD(TAG, "onStartCommand recharge:" + startId + " " + intent);
        if(intent!=null){
            String from=intent.getStringExtra(EVENT);
            boolean haveFinishedRecharge= PrefUtils.getBoolean(PrefConstants.FINISHFIRSTRECHARGE.HAVE_FINISHED_FIRST_RECHARGE,false);
            if(!haveFinishedRecharge){
                NotifyInfo.LongTopup longTopup = AbConfigManager.getInstance(this).getConfig().notify.long_topup;
                noRecharge(longTopup);
                startNoRechargeLoop(from,longTopup);
            }
            stopSelf();
        }
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        PushLog.logD(TAG, "onDestroy");
        super.onDestroy();
    }

    private void startNoRechargeLoop(String from,NotifyInfo.LongTopup longTopup){
        if (StringUtil.isEmpty(longTopup.interval)) {
            return;
        }
        String interval = longTopup.interval;
        String[] intervalTime = interval.split("\\|");
        int position = PrefUtils.getInt(PrefConstants.FINISHFIRSTRECHARGE.RECHARGE_INTERVAL_POSITION, 0);
        if(position<intervalTime.length) {
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent loopintent = new Intent(this, RechargeAlarmService.class);
            loopintent.putExtra(RechargeAlarmService.EVENT, NO_RECHARGE_LOOP);
            PendingIntent pending = PendingIntent.getService(this, 0, loopintent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            if (from.equals(HAVE_RECHARGE)) {
                alarm.cancel(pending);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(AlarmManager.RTC, System.currentTimeMillis() + Integer.parseInt(intervalTime[position]) *24*60 * 60 * 1000, pending);
            } else {
                alarm.set(AlarmManager.RTC, System.currentTimeMillis() + Integer.parseInt(intervalTime[position]) *24*60 * 60 * 1000, pending);
            }
            PrefUtils.putInt(PrefConstants.FINISHFIRSTRECHARGE.RECHARGE_INTERVAL_POSITION, position + 1);
        }
    }

    private void noRecharge(NotifyInfo.LongTopup longTopup) {
        Analytics.sendUIEvent(AnalyticsEvents.Notification.Send_NativeNoti, "no_recharge", null);
        String title = longTopup.title.getText();
        String body = longTopup.content.getText();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.ic_white_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(GlobalContext.get().getResources(),
                R.mipmap.ic_launcher));
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setTicker(title);
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NoTopUpActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        Notification build = builder.build();
        manager.notify(1, build);

    }


}
