package dotc.android.happybuy.push;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.modules.active.UnAvaliablePushActivity;
import dotc.android.happybuy.modules.awarding.AwardingActivity;
import dotc.android.happybuy.modules.awarding.func.AwardingManager;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.persist.database.DaoProxy;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 */
public class PushMessageDispatcher {

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private Handler mHandler = new Handler();

    public PushMessageDispatcher(Context context) {
        mContext = context;//context.getApplicationContext();
    }

    public boolean dispatch(final String from, final Map<String,String> data) {
        if(DynamicTopicManager.getInstance(mContext).isDynamicTopic(from)) {
            return dispatchDynamicMessage(from, data);
        } else if (from.startsWith(PushConstance.LONGLIVE_TOPIC_GLOBAL)) {
            return dispatchSystemMessage(from, data);
        } else {
            // normal downstream message.
            return dispatchPushMessage(from, data);
        }
    }

    private boolean dispatchSystemMessage(final String from, final Map<String,String> data) {
        String subscribe = from.substring(from.lastIndexOf("/") + 1);
        PushLog.logD(TAG, "dispatchSystemMessage subscribe:" + subscribe);
        if (PushConstance.getSubGlobal().equals(subscribe)) {
            handleReceiveSystemMessage(from, data);
            return true;
        }
        return false;
    }

    private boolean dispatchDynamicMessage(final String from, final Map<String,String> data) {
        String type = data.get("type");
        PushLog.logD(TAG, "dispatchDynamicMessage type:" + type);
        if("3".equals(type)){
            Analytics.sendUIEvent(AnalyticsEvents.Push.Awarding_Receive_Data, data.get(PushProtocol.Awarding.PRODUCT_ITEM_ID),null);
            handleAwardingMessage(from,data);
            return true;
        }
        return false;
    }

    private boolean dispatchPushMessage(final String from, final Map<String,String> data) {
        String type = data.get("type");
        PushLog.logD(TAG, "dispatchPushMessage type:" + type);
        try {
            if("1".equals(type)){//延迟5s弹窗
                Analytics.sendUIEvent(AnalyticsEvents.Push.Awarded_Receive_Data, data.get(PushProtocol.Awarded.PRODUCT_ITEM_ID),null);
                mHandler.postDelayed(new Runnable() {//
                    @Override
                    public void run() {
                        try {
                            handleAwardMessage(data);
                        } catch (Exception e) {
                            PushLog.logD(TAG, "dispatchPushMessage e:" + e.toString());
                        }
                    }
                },5*1000);

            }
            if("2".equals(type)){
                //红包推送
//                CouponsPush(data);
            }
            if("4".equals(type)){
                //过期红包推送
                UnavalibleCouponsPush(data);
            }

        } catch (Exception e) {
            PushLog.logD(TAG, "dispatchPushMessage e:" + e.toString());
        }
        return false;
    }

    public boolean handleAwardingMessage(final String from, final Map<String,String> data){
        String productId = data.get(PushProtocol.Awarding.PRODUCT_ID);
        String productName = data.get(PushProtocol.Awarding.PRODUCT_NAME);
        String defaultImage = data.get(PushProtocol.Awarding.PRODUCT_DEFAULT_IMAGE);
        String productItemId = data.get(PushProtocol.Awarding.PRODUCT_ITEM_ID);
        String period = data.get(PushProtocol.Awarding.PRODUCT_PERIOD);
        String serverTime = data.get(PushProtocol.Awarding.SERVER_TIME);
        String awardTime = data.get(PushProtocol.Awarding.AWARD_TIME);
        DynamicTopicManager.getInstance(mContext).markFinish(productItemId);
        return AwardingManager.getInstance(mContext).showAwardingDialog(mContext,productId,productName,
                defaultImage,productItemId,period,serverTime,awardTime);
    }

    public void handleReceiveSystemMessage(final String from, final Map<String,String> data) {
        PushLog.logD(TAG, "handleReceiveSystemMessage ");

    }

    public void CouponsPush(final Map<String,String> data) throws JSONException{
        PushLog.logD(TAG, "handleAwardMessage jsonString:"+data);
        String title = data.get("title");
        String body = data.get("body");
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext);

        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.ic_white_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(GlobalContext.get().getResources(),
                R.mipmap.ic_launcher));
        builder.setTicker(title);

        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent;
        if(AccountHelper.getInstance(GlobalContext.get()).isLogin()){
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, RedPacketActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        }else {
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, LoginActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        }


//        long[] time = {0, 1000, 1000, 1000};
        builder.setAutoCancel(true);
//        builder.setVibrate(time);
        builder.setContentIntent(pendingIntent);
        Notification build = builder.build();
        manager.notify(1, build);

    }

    public void UnavalibleCouponsPush(final Map<String,String> data) throws JSONException{
        PushLog.logD(TAG, "handleAwardMessage jsonString:"+data);
        String title = data.get("title");
        String body = data.get("body");
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext);
        builder.setContentTitle(title);

        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.ic_white_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(GlobalContext.get().getResources(),
                R.mipmap.ic_launcher));
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setTicker(title);

        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(mContext, UnAvaliablePushActivity.class);
        PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        Notification build = builder.build();
        manager.notify(1, build);

    }

    private void handleAwardMessage(final Map<String,String> data) throws JSONException{
        String productId = data.get(PushProtocol.Awarded.PRODUCT_ID);
        String productName = data.get(PushProtocol.Awarded.PRODUCT_NAME);
        String defaultImage = data.get(PushProtocol.Awarded.PRODUCT_DEFAULT_IMAGE);
        String productItemId = data.get(PushProtocol.Awarded.PRODUCT_ITEM_ID);
        String period = data.get(PushProtocol.Awarded.PRODUCT_PERIOD);

        AwardingManager.getInstance(mContext).showAwardedDialog(mContext,productId,productName,defaultImage,productItemId, period);
//        showAwardDialog(productId,productName,defaultImage,productItemId,period);
    }

//    public void showAwardDialog(String productId,String productName,String defaultImage,String productItemId,String period){
//        //查询数据库，判断是否已经提示过中奖信息
//        if(!DaoProxy.getInstance(mContext).getAwardDao().isExistAward(PrefUtils.getString(PrefConstants.Network.uid, ""),productId,productItemId, Integer.parseInt(period))){
//            DaoProxy.getInstance(mContext).getAwardDao().saveOneAward(PrefUtils.getString(PrefConstants.Network.uid, ""),productId,productItemId, Integer.parseInt(period),"");
//            Intent intent = new Intent(mContext,AwardDialogActivity.class);
//            intent.putExtra(AwardDialogActivity.EXTRA_PRODUCT_ID,productId);
//            intent.putExtra(AwardDialogActivity.EXTRA_PRODUCT_NAME,productName);
//            intent.putExtra(AwardDialogActivity.EXTRA_PRODUCT_IMAGE,defaultImage);
//            intent.putExtra(AwardDialogActivity.EXTRA_PRODUCT_ITEM_ID,productItemId);
//            intent.putExtra(AwardDialogActivity.EXTRA_PRODUCT_PERIOD,period);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//            Analytics.sendUIEvent(AnalyticsEvents.PrizeNotifation.Click_Award_Notification, null, null);
//        }
//    }

}
