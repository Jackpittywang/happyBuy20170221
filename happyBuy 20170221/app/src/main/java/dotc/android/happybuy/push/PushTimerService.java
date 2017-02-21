package dotc.android.happybuy.push;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangjun on 15/12/22.
 */
public class PushTimerService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    public static final String EXTRA_FROM = "extra_from";
    public static final String EXTRA_DATA = "extra_data";
    private AtomicInteger mStartIdSign = new AtomicInteger();
    private PushMessageDispatcher mPushMessageDispatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        PushLog.logD(TAG, "onCreate");
        mPushMessageDispatcher = new PushMessageDispatcher(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PushLog.logD(TAG, "onStartCommand startId:" + startId + " " + intent);
        if(intent!=null){
            mStartIdSign.incrementAndGet();
            final String from = intent.getStringExtra(EXTRA_FROM);
            final HashMap<String,String> data = (HashMap<String, String>) intent.getSerializableExtra(EXTRA_DATA);
            new AsyncTask<String,Integer,Boolean>(){
                protected Boolean doInBackground(String[] objects) {
                    return mPushMessageDispatcher.dispatch(from,data);
                }
                protected void onPostExecute(Boolean aBoolean) {
                    stopSelfIfNoTask();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void stopSelfIfNoTask(){
        mStartIdSign.decrementAndGet();
        PushLog.logD(TAG, "stopSelfIfNoTask " + mStartIdSign.get());
        if(mStartIdSign.get()==0){
            stopSelf();
        }
    }

    private boolean hasHoneycomb(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    private String readDetailUrl(String data){
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString("web_url");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return "";
    }

    private boolean isAppForground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
