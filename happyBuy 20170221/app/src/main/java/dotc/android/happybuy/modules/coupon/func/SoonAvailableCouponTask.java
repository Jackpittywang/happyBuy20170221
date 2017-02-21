package dotc.android.happybuy.modules.coupon.func;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoCoupons;
import dotc.android.happybuy.http.result.PojoCouponsItem;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.coupon.service.NotifyAvailableCouponService;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by LiShen
 * on 2016/12/23.
 */

public class SoonAvailableCouponTask {
    private Context context;
    private List<PojoCoupons> couponList;

    public SoonAvailableCouponTask() {
        context = GlobalContext.get();
    }

    public void check() {
        String url = HttpProtocol.URLS.COUPONS;
        Map<String, Object> param = new HashMap<>();
        param.put("status", RedPacketActivity.COUPON_TYPE_READYTO);
        Network.get(context).asyncPost(url, param, new Network.JsonCallBack<PojoCouponsItem>() {
            @Override
            public void onSuccess(PojoCouponsItem pojoCouponsItem) {
                if (pojoCouponsItem != null && pojoCouponsItem.list != null
                        && pojoCouponsItem.list.size() > 0) {
                    couponList = pojoCouponsItem.list;
                    new CheckSoonAvailableCouponTask().execute();
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
            }

            @Override
            public Class<PojoCouponsItem> getObjectClass() {
                return PojoCouponsItem.class;
            }
        });

    }

    private class CheckSoonAvailableCouponTask extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {

            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);

            String uid = PrefUtils.getString(PrefConstants.UserInfo.UID, "-1");

            Gson gson = new Gson();

            List<String> notifiedCouponIds = new ArrayList<>();
            try {
                notifiedCouponIds = gson.fromJson(PrefUtils.getString(context,
                        PrefConstants.NotifyAvailableCoupon.NOTIFIED_COUPON_IDS, "[]"),
                        new TypeToken<List<String>>() {
                        }.getType());
            } catch (Exception ignore) {
            }

            int lastNotifiedReqCode = PrefUtils.getInt(context,
                    PrefConstants.NotifyAvailableCoupon.LAST_NOTIFIED_REQ_CODE, 0) + 1;

            for (PojoCoupons coupon : couponList) {

                if (coupon.begin_time == 0 || TextUtils.isEmpty(coupon.id)) {
                    continue;
                }

                // old
                if (System.currentTimeMillis() > coupon.begin_time * 1000) {
                    continue;
                }

                String couponInfo = coupon.id + "$" + uid;

                // already set alarm
                if (notifiedCouponIds.contains(couponInfo)) {
                    continue;
                }


                HBLog.d("SoonAvailableCouponTask", couponInfo + " " + coupon.begin_time);
                HBLog.d("SoonAvailableCouponTask", "req: " + lastNotifiedReqCode);

                Intent intent = new Intent(context, NotifyAvailableCouponService.class);
                intent.putExtra(NotifyAvailableCouponService.EXTRA_COUPON_INFO, couponInfo);
                PendingIntent pendingIntent = PendingIntent.getService(context, lastNotifiedReqCode, intent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC, coupon.begin_time * 1000, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC, coupon.begin_time * 1000, pendingIntent);
                }

                lastNotifiedReqCode++;
                notifiedCouponIds.add(couponInfo);
            }

            PrefUtils.putInt(PrefConstants.NotifyAvailableCoupon.LAST_NOTIFIED_REQ_CODE,
                    lastNotifiedReqCode);
            PrefUtils.putString(PrefConstants.NotifyAvailableCoupon.NOTIFIED_COUPON_IDS,
                    gson.toJson(notifiedCouponIds));

            return Activity.RESULT_OK;
        }
    }
}
