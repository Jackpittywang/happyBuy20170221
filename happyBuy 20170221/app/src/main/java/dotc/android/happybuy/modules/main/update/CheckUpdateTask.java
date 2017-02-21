package dotc.android.happybuy.modules.main.update;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.AbtestConfigBean;
import dotc.android.happybuy.config.abtest.bean.UpgradeInfo;
import dotc.android.happybuy.http.result.PojoUpgradeNotify;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by LiShen
 * on 2016/12/8.
 */
@SuppressLint("SimpleDateFormat")
public class CheckUpdateTask extends AsyncTask<Context, Integer, Integer> {
    private Context context;

    private NotificationCompat.Builder builder;

    private static final int NEED_TO_NOTIFY = 0;
    private static final int NO_NEED_TO_NOTIFY = -1;

    @Override
    protected Integer doInBackground(Context... params) {
        this.context = params[0];
        AbtestConfigBean config = AbConfigManager.getInstance(context).getConfig();
        if (config == null || config.update == null) {
            return NO_NEED_TO_NOTIFY;
        }
        UpgradeInfo upgradeInfo = config.update;
        if (!(upgradeInfo.version_code > AppUtil.getAppVersionCode(context))) {
            return NO_NEED_TO_NOTIFY;
        }
        // has new version
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Gson gson = new Gson();
        String infoStr = PrefUtils.getString(context,
                PrefConstants.AppUpgrade.NOTIFY_UPGRADE_INFO, "{}");
        PojoUpgradeNotify localNotifyTimes = new PojoUpgradeNotify();
        try {
            localNotifyTimes = gson.fromJson(infoStr, PojoUpgradeNotify.class);
        } catch (Exception ignore) {
        }

        if (localNotifyTimes != null) {
            HBLog.i("Before: " + localNotifyTimes.toString());
        }

        // same day notify max times
        if (localNotifyTimes != null && today.equals(localNotifyTimes.notifyDate) &&
                localNotifyTimes.notifyTimes >= upgradeInfo.notify_times_day_max) {
            return NO_NEED_TO_NOTIFY;
        }

        builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(upgradeInfo.title.getText());
        builder.setContentText(upgradeInfo.message.getText());
        builder.setSmallIcon(R.drawable.ic_white_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher));
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setTicker(upgradeInfo.title.getText());
        builder.setAutoCancel(true);


        // check GooglePlay installed
        boolean isGpInstalled = AppUtil.isAppInstalled(context, "com.android.vending");
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra(UpdateActivity.INTENT_IS_GP_INSTALLED, isGpInstalled);
        intent.putExtra(UpdateActivity.INTENT_GP_LINK, upgradeInfo.gp_link);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        // local times +1
        if (localNotifyTimes == null) {
            localNotifyTimes = new PojoUpgradeNotify();
        }

        if (!localNotifyTimes.notifyDate.equals(today)) {
            localNotifyTimes.notifyTimes = 0;
        }

        localNotifyTimes.notifyDate = today;
        localNotifyTimes.notifyTimes = localNotifyTimes.notifyTimes + 1;
        PrefUtils.putString(PrefConstants.AppUpgrade.NOTIFY_UPGRADE_INFO,
                gson.toJson(localNotifyTimes));
        Analytics.sendUIEvent(AnalyticsEvents.Notification.Send_NativeNoti,
                "app_update_notify", null);

        HBLog.i("After: " + localNotifyTimes.toString());

        return NEED_TO_NOTIFY;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        // notification
        if (result == NEED_TO_NOTIFY) {
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(10000, builder.build());
        }
    }
}
