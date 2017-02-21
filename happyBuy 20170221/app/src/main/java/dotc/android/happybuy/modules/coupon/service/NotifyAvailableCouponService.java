package dotc.android.happybuy.modules.coupon.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by LiShen
 * on 2016/12/23.
 */

public class NotifyAvailableCouponService extends Service {

    public static final String EXTRA_COUPON_INFO = "extra_coupon_info";
    public static final int NOTIFY_ID = 1864618;

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        String couponUid = i.getStringExtra(EXTRA_COUPON_INFO);
        String localInfo = PrefUtils.getString(getApplicationContext(),
                PrefConstants.NotifyAvailableCoupon.NOTIFIED_COUPON_IDS, "[]");
        String uid = PrefUtils.getString(getApplicationContext(), PrefConstants.UserInfo.UID, "");

        HBLog.i("SoonAvailableCouponTask", "couponUid: " + couponUid);
        HBLog.i("SoonAvailableCouponTask", "uid: " + uid);
        HBLog.i("SoonAvailableCouponTask", "localInfo: " + localInfo);

        List<String> notifiedCouponIds = new ArrayList<>();
        try {
            Gson gson = new Gson();
            notifiedCouponIds = gson.fromJson(localInfo, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception ignore) {
        }

        if (notifiedCouponIds != null && notifiedCouponIds.contains(couponUid)
                && uid.equals(couponUid.split("\\$")[1])) {
            NotificationCompat.Builder builder;
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentTitle(getApplicationContext().getString(R.string.notify_available_coupon_title));
            builder.setContentText(getApplicationContext().getString(R.string.notify_available_coupon_content));
            builder.setSmallIcon(R.drawable.ic_white_notification);
            builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.mipmap.ic_launcher));
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setTicker(getApplicationContext().getString(R.string.notify_available_coupon_title));
            builder.setAutoCancel(true);

            Intent intent = new Intent(getApplicationContext(), RedPacketActivity.class);
            intent.putExtra(RedPacketActivity.EXTRA_AVAILABLE_COUPONS_NOTIFICATION_ENTER, true);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFY_ID, builder.build());

            Analytics.sendUIEvent(AnalyticsEvents.Notification.Send_NativeNoti,
                    "available_coupons_notify_send", null);
        }
        stopSelf();
        return super.onStartCommand(i, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
