package dotc.android.happybuy.modules.splash;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.update.util.StringUtil;
import com.stat.analytics.AnalyticsSdk;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatService;
import com.tencent.stat.common.StatConstants;

import java.util.HashMap;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.NotifyInfo;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.login.func.RegistManager;
import dotc.android.happybuy.modules.login.func.UserTokenManager;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.push.AlarmService;
import dotc.android.happybuy.modules.push.RechargeAlarmService;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.ShortCutUtils;

/**
 * Created by huangli on 16/4/11.
 */
public class SplashActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    public final static String EXTRA_FROM = "extra_from";
    public static String KEY_FRIST_RUN = "frist_run_key";

    private String mFcmFrom;
    private HashMap<String, String> mFcmData;
    private final int WHAT_TIME = 0x00;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this, MainTabActivity.class);
            if (!TextUtils.isEmpty(mFcmFrom)) {
                intent.putExtra(MainTabActivity.EXTRA_FCM_FROM, mFcmFrom);
                intent.putExtra(MainTabActivity.EXTRA_FCM_DATA, mFcmData);
            }
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (window != null) {
                window.requestFeature(Window.FEATURE_NO_TITLE);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
        setContentView(R.layout.activity_splash);
        HBLog.d(TAG + " onCreate " + getIntent());
        checkIntent(getIntent());
        UserTokenManager.getInstance(this).verifyToken();
        mHandler.sendEmptyMessageDelayed(WHAT_TIME, 3000);
        AnalyticsSdk.getInstance(this).sendRealActive();
        boolean isfrist = PrefUtils.getBoolean(GlobalContext.get(), KEY_FRIST_RUN, true);
        if (isfrist && !UserTokenManager.getInstance(GlobalContext.get()).isTokenValid()) {
            RegistManager.get(this).anonymousRegister();
        }
        NotifyInfo.LongNoopenApp longNoopenApp = AbConfigManager.getInstance(this).getConfig().notify.long_noopen_app;
        openApplication(longNoopenApp);

        NotifyInfo.LongTopup longTopup = AbConfigManager.getInstance(this).getConfig().notify.long_topup;
        boolean haveFinishedRecharge = PrefUtils.getBoolean(PrefConstants.FINISHFIRSTRECHARGE.HAVE_FINISHED_FIRST_RECHARGE, false);
        if (!haveFinishedRecharge) {
            notificationRecharge(longTopup);
        }
        ShortCutUtils.setUpShortCut(this, getString(R.string.hb_app_name), R.mipmap.ic_launcher, SplashActivity.class);

//        StatConfig.setDebugEnable(false);
//        initBtaConfig();
//        StatService.trackCustomEvent(this,"onCreate","");
    }


    private void notificationRecharge(NotifyInfo.LongTopup longTopup) {
        PrefUtils.putInt(PrefConstants.FINISHFIRSTRECHARGE.RECHARGE_INTERVAL_POSITION, 0);
        if (StringUtil.isEmpty(longTopup.interval)) {
            return;
        }
        String interval = longTopup.interval;
        String[] intervalTime = interval.split("\\|");
        int position = PrefUtils.getInt(PrefConstants.FINISHFIRSTRECHARGE.RECHARGE_INTERVAL_POSITION, 0);
        if (position < intervalTime.length) {
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, RechargeAlarmService.class);
            intent.putExtra(RechargeAlarmService.EVENT, RechargeAlarmService.HAVE_RECHARGE);
            PendingIntent pending = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            alarm.cancel(pending);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(AlarmManager.RTC, System.currentTimeMillis() + Integer.parseInt(intervalTime[position]) * 24 * 60 * 60 * 1000, pending);
            } else {
                alarm.set(AlarmManager.RTC, System.currentTimeMillis() + Integer.parseInt(intervalTime[position]) * 24 * 60 * 60 * 1000, pending);
            }
            PrefUtils.putInt(PrefConstants.FINISHFIRSTRECHARGE.RECHARGE_INTERVAL_POSITION, position + 1);
        }
    }

    private void openApplication(NotifyInfo.LongNoopenApp longNoopenApp) {
        PrefUtils.putInt(PrefConstants.LONGTIMENOOPEN.INTERVAL_POSITION, 0);
        if (StringUtil.isEmpty(longNoopenApp.interval)) {
            return;
        }
        String interval = longNoopenApp.interval;
        String[] intervalTime = interval.split("\\|");
        int position = PrefUtils.getInt(PrefConstants.LONGTIMENOOPEN.INTERVAL_POSITION, 0);
        if (position < intervalTime.length) {
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmService.class);
            intent.putExtra(AlarmService.EVENT, AlarmService.HAVE_OPEN);
            PendingIntent pending = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            alarm.cancel(pending);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(AlarmManager.RTC, System.currentTimeMillis() + Integer.parseInt(intervalTime[position]) * 24 * 60 * 60 * 1000, pending);
            } else {
                alarm.set(AlarmManager.RTC, System.currentTimeMillis() + Integer.parseInt(intervalTime[position]) * 24 * 60 * 60 * 1000, pending);
            }
            PrefUtils.putInt(PrefConstants.LONGTIMENOOPEN.INTERVAL_POSITION, position + 1);
        }
    }


    private void initBtaConfig() {
        String appKey = getResources().getString(R.string.mta_android_app_key_dev);
        try {
            StatService.startStatService(this, appKey, StatConstants.VERSION);
        } catch (MtaSDkException e) {
            e.printStackTrace();
            HBLog.e(TAG + "initBtaConfig error:" + e.toString());
        }

//        Analytics.init(this, mta_android_app_key);
    }

    private void checkIntent(Intent intent) {
        try {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String from = extras.getString("from");
                    if (!TextUtils.isEmpty(from)) {
                        handleFirebaseMessage(from, extras);
                    }
                }
            }
        } catch (Exception e) {
            //catch at extras.getString("from");
        }
    }

    private void handleFirebaseMessage(String from, Bundle extras) {
        HashMap<String, String> data = bundleToMap(extras);
        HBLog.d(TAG + " handleFirebaseMessage from:" + from + " " + extras);
        mFcmFrom = from;
        mFcmData = data;
    }

    private HashMap<String, String> bundleToMap(Bundle extras) {
        HashMap<String, String> map = new HashMap<>();
        for (String key : extras.keySet()) {
            map.put(key, String.valueOf(extras.get(key)));
        }
        return map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HBLog.d(TAG + " onDestroy ");
        if (mHandler.hasMessages(WHAT_TIME)) {
            mHandler.removeMessages(WHAT_TIME);
        }
    }
}
