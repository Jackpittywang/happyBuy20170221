package dotc.android.happybuy.push.autolog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.persist.database.DaoProxy;
import dotc.android.happybuy.push.PushHelper;
import dotc.android.happybuy.push.PushLog;
import dotc.android.happybuy.push.PushMessageDispatcher;
import dotc.android.happybuy.push.PushTimerService;
import dotc.android.happybuy.push.TokenManager;
import mobi.andrutil.cm.CloudMessageListener;

/**
 * Created by wangjun on 16/9/2.
 */
public class AutologCloudReceiver extends CloudMessageListener {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private PushMessageDispatcher mPushMessageDispatcher;

    public AutologCloudReceiver(Context context){
        super();
        mPushMessageDispatcher = new PushMessageDispatcher(context);
        this.context = context;
    }

    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String,String> data = message.getData();
        PushLog.logD(TAG,"onMessageReceived from:"+from+" "+data);
        String messageId = data.get("msg_id");
        if(!TextUtils.isEmpty(messageId)){
            boolean isDuplicateMessage = DaoProxy.getInstance(context).getMessageDao().isExistMessage(null,messageId);
            if(!isDuplicateMessage){
                handleMessageReceice(from,data);
            }
        } else {
            handleMessageReceice(from,data);
        }

    }

    public void onTokenRefresh() {
        String refreshedToken = TokenManager.get(GlobalContext.get()).getFirebaseToken();
        PushLog.logD(TAG,"onTokenRefresh refreshedToken:"+refreshedToken);
        if(!TextUtils.isEmpty(refreshedToken)){
            PushHelper.setGCMToken(GlobalContext.get(),refreshedToken);
            sendRegistrationToServer(refreshedToken);
        }
    }

    private void sendRegistrationToServer(final String token) {
        TokenManager.get(GlobalContext.get()).uploadTokenToServerIfNeeded(token);
    }


    private void handleMessageReceice(String from,Map<String,String> data){
        PushLog.logD(TAG,"handleMessageReceice from:"+from+" "+data);
        if("1".equals(data.get("timer"))||("2".equals(data.get("timer")))){//定时通知
            startTimerAlarm(from,data);
        } else {
            mPushMessageDispatcher.dispatch(from,data);
        }
    }

    private void startTimerAlarm(String from, Map<String,String> data){
        int hour = Integer.parseInt(data.get("timerDate"));
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY)<hour){
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
        } else {
            calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR)+1);
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
        }

        Intent intent = new Intent(context, PushTimerService.class);
        intent.putExtra(PushTimerService.EXTRA_FROM,from);
        intent.putExtra(PushTimerService.EXTRA_DATA, (HashMap<String,String>) data);
        PendingIntent pending = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pending);
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
    }

}
