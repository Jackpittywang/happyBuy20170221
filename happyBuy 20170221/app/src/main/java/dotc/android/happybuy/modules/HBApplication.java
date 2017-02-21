package dotc.android.happybuy.modules;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.appsflyer.AppsFlyerProperties;
import com.facebook.FacebookSdk;
import com.google.android.gms.update.UpdateSdk;
import com.stat.analytics.AnalyticsSdk;
import com.stat.analytics.util.StringUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.push.autolog.AutologCloudReceiver;
import dotc.android.happybuy.util.AppUtil;
import mobi.andrutil.autolog.AnalyticsProvider;
import mobi.andrutil.autolog.AutologManager;

/**
 * Created by wangjun on 16/3/28.
 */
public class HBApplication extends MultiDexApplication implements Runnable {

    private final String TAG = this.getClass().getSimpleName();
    private String mProcessName;
    private List<WeakReference<Activity>> mActivityStack = new ArrayList<WeakReference<Activity>>();

    @Override
    public void attachBaseContext(Context base) {
        ActivityManager.RunningAppProcessInfo rail = takeCurrentRAPI(base);
        Log.d(TAG,"attachBaseContext "+rail.processName);
        if(rail.processName.equals(base.getPackageName())){
            initAutoLog(base);
        }
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.setContext(this);
        Languages.getInstance().initWhenLauncher();
        ActivityManager.RunningAppProcessInfo rail = takeCurrentRAPI(this);
        mProcessName = rail.processName;
        Log.d(TAG," onCreate "+mProcessName+" "+Process.myPid()+" mProcessName:"+mProcessName);
        if(mProcessName.equals(getPackageName())){
            FacebookSdk.sdkInitialize(getApplicationContext());
            initBuglyConfig();
            initMtaConfig();
            ConfigManager.get(this).asyncFetch(false);
            initCPlusReport();
            initUpdateSdk();
//            SdkManager.getInstance(this).initSdks();
            new Thread(this).start();
        } else if(mProcessName.equals(getPackageName()+":tools")){

        }
    }

    private void initAutoLog(final Context context){
        int bucketId = AnalyticsSdk.getBucketId(context);
        AutologManager.Config config = new AutologManager.Config();
        config.setAnalyticsProvider(bucketId,new AnalyticsProvider() {
            @Override
            public void sendEvent(String eventId, String label, String value, String extra, String eid) {
                AnalyticsSdk.getInstance(context).sendEvent(null,eventId,label,value,extra,eid);
            }
        });
        config.setRouserEnable(false);
        config.setUninstallFeedbackHost("http://ufeedback.gogobuy.info");
        config.setCloudMessageReportHost(build.Environment.REPORT_URL);
        config.setCloudMessageListener(new AutologCloudReceiver(context));
        AutologManager.init(context, config);
    }

    private void initUpdateSdk(){
//        UpdateSdk.shared().setDebugMode(true);
        // "http://192.168.5.222:12204/v3/config"
        try {
            UpdateSdk.shared().init(this, build.Environment.CONFIG_URL, "515", new UpdateSdk.StatProvider() {
                @Override
                public void sendEvent(String category, String action, String label, Long value, String extra, String eid) {
                    AnalyticsSdk.getInstance(GlobalContext.get()).sendEvent(category,action,label,String.valueOf(value),extra,eid);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private ActivityManager.RunningAppProcessInfo takeCurrentRAPI(Context context) {
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()
                .iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo rai = iterator.next();
            if (rai.pid == Process.myPid()) {
                return rai;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HBLog.d(TAG + " onTerminate "+mProcessName);
    }

    private void initBuglyConfig() {
        String bugly_app_key = getResources().getString(R.string.bugly_app_key_dev);

        CrashReport.initCrashReport(this, bugly_app_key, false);
    }

    private void initMtaConfig() {
        String mta_android_app_key = getResources().getString(R.string.mta_android_app_key_dev);
        Analytics.init(this, mta_android_app_key);
    }

    private void initCPlusReport(){
        // 初始化
//        String url = "http://192.168.5.222:11011";
        String url = build.Environment.REPORT_URL;
        String referrer = StringUtil.toString(AppsFlyerProperties.getInstance().getReferrer(this));
        int trafficId = AppUtil.getMetaInt(this,"hb_tid");//公司分配的
        String channel = AppUtil.getMetaData(this,"hb_channel");
        String installChannel = AppUtil.getMetaData(this,"InstallChannel");
        HBLog.d(TAG+" initCPlusReport tid:"+trafficId+" channel:"+channel+" ");
        String googleAdId = "";
        String source = ""; //安装来源    0 未知来源    1 google play
        AnalyticsSdk.Configuration.Builder builder = new AnalyticsSdk.Configuration.Builder();
        builder.setAnalyticsUrl(url);
        builder.setGooglePushTokenUrl("");
        builder.setReferrer(referrer);
        builder.setTrafficId(String.valueOf(trafficId));
        builder.setChannel(channel);
        builder.setInstallChannel(installChannel);
        builder.setSource(source);
        builder.setGoogleAdId(googleAdId);
        builder.setSampler(null); // 采样，默认不采样
//        builder.setMtaAppKey(null); // mta app key, null不开启mta
//        builder.setMtaAutoExceptionCaught(true); // mta 捕获异常
//        builder.setMtaSendCountableEvent(true); // mta上报计数事件
//        builder.setMtaEventTranslator(AnalyticsSdk.MTA_EVENT_TRANSLATOR_SIMPLE); // mta 事件翻译, AnalyticsSdk.MTA_EVENT_TRANSLATOR_DEFAULT  AnalyticsSdk.MTA_EVENT_TRANSLATOR_SIMPLE
//        builder.setBuglyAppId(null); // buglyAppId
//        builder.setBuglyDebugMode(true); // bugly debug mode
        builder.setAppsFlyerKey("ELctKLYrDm4fb6desm4gmm"); // appsflyer key
        builder.setAvazuTrackingEnable(true);
        builder.setUploadInstalledApps(false); // 上传应用列表
        builder.setCategoryCanBeEmpty(true); // category 是否允许为空
        builder.setActionCanBeEmpty(true); // action 是否允许为空
        AnalyticsSdk.getInstance(this).setDebugMode(false);
        AnalyticsSdk.getInstance(this).init(builder.build());
    }

    public void addTaskStack(Activity activity){
        mActivityStack.add(new WeakReference(activity));
    }

    public void removeTaskStack(Activity activity){
        for(int i = 0;i<mActivityStack.size();i++){
            WeakReference<Activity> wr = mActivityStack.get(i);
            if(wr.get()!=null&&activity==wr.get()){
                mActivityStack.remove(wr);
                return;
            }
        }
    }

    public int getStackTaskCount(){
        return mActivityStack.size();
    }

    public void finishAllActivity() {
        for (int i = 0; i < mActivityStack.size(); i++) {
            WeakReference<Activity> weakReference = mActivityStack.get(i);
            if (weakReference!=null&&weakReference.get()!=null) {
                weakReference.get().finish();
            }
        }
        mActivityStack.clear();
    }

    @Override
    public ComponentName startService(Intent service) {
        try {
            return super.startService(service);
        } catch (Exception e){}
        return null;
    }

    @Override
    public void run() {
        String country=AppUtil.getMetaData(this,"country");
        String dbFile;
        int locationDB;
        if(country.equals("th")){
            locationDB=R.raw.location_th;
            dbFile = "/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + GlobalContext.get().getPackageName()+ "/" + "city_th.s3db";
        }else if(country.equals("vn")){
            locationDB=R.raw.location_vn;
            dbFile = "/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + GlobalContext.get().getPackageName()+ "/" + "city_vn.s3db";
        }else {
            locationDB=R.raw.location_th;
            dbFile = "/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + GlobalContext.get().getPackageName()+ "/" + "city_th.s3db";
        }
        copyLocationDBFileIfNeeded(dbFile,locationDB);

    }

    private void copyLocationDBFileIfNeeded(String dbFile,int locationDB){
        /*String dbFile = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + GlobalContext.get().getPackageName()+ "/" + "city_th.s3db";*/
        HBLog.d(TAG+" copyLocationDBFileIfNeeded "+dbFile);
        File file = new File(dbFile);
        if (!file.exists()) {
            try {
                copyDbFile(dbFile,locationDB);
            } catch (IOException e){}
        }
    }

    private void copyDbFile(String dbfile,int locationDB) throws IOException {
        HBLog.d(TAG+" copyDbFile dbfile:" + dbfile);
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getResources().openRawResource(locationDB);
            fos = new FileOutputStream(dbfile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
                fos.flush();
            }
        } finally {
            if(is!=null){
                is.close();
            }
            if(fos!=null){
                fos.close();
            }
        }
    }

}
