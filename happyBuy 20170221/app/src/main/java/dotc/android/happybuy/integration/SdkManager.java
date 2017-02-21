//package dotc.android.happybuy.integration;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.google.android.gms.fc.core.analytics.IAnalytics;
//import com.google.android.gms.fc.sdk.FastCharge;
//import com.max.maxlibrary.FastScan;
//
//import java.util.ArrayList;
//
//import mobi.android.boostball.BoostBallSdk;
//
///**
// * Created by wangjun on 16/10/11.
// */
//public class SdkManager {
//    private static SdkManager mInstance;
//    public static SdkManager getInstance(Context context){
//        if(mInstance == null){
//            mInstance = new SdkManager(context);
//        }
//        return mInstance;
//    }
//
//    private Context mContext;
//
//    private SdkManager(Context context){
//        this.mContext = context;
//    }
//
//    public void initSdks(){
//        initStandBy(mContext);
//        initFastScan();
//        initFastCharge(mContext);
//        initClean(mContext);
//    }
//
//    /**
//     * 锁屏
//     */
//    private void initStandBy(Context context) {
////        DefenderSDK.setAdInfo(Keys.STAND_BY_ID);
////        DefenderLog.DEBUG = true;
////        DefenderSDK.initialize(context, analyticsListener, Keys.STANDBY_CONFIG_URL);
//    }
//
//    //
//    private void initFastScan() {
//        FastScan.initSlotInfo(Keys.COVER_SLOT_ID);//传入fastScan ID
//    }
//
//    /**
//     * 快充
//     *
//     * @param context
//     */
//    private void initFastCharge(Context context) {
//        ArrayList<String> idList = new ArrayList<>();
//        idList.add(Keys.BATTERY_LOCK_SCREEN_SLOT_ID_1);
//        idList.add(Keys.BATTERY_LOCK_SCREEN_SLOT_ID_2);
//        idList.add(Keys.BATTERY_LOCK_SCREEN_SLOT_ID_3);
//        idList.add(Keys.BATTERY_LOCK_SCREEN_SLOT_ID_4);
//        FastCharge.setLogAble(context, true);
//        FastCharge.init(context, Keys.FAST_CHARGE_CONFIG_URL/*对应APK的配置(需申请)*/, idList/*快充的ID*/, Keys.BATTERY_GIFT_ID/*礼物ID*/, "#58afe7"/*显示效果颜色*/, iAnalytics);
//    }
//
//    /**
//     * 清理
//     */
//    private void initClean(Context context) {
//        BoostBallSdk.setAdInfo(Keys.CLEAN_SLOT_ID);//清理的ID
//        BoostBallSdk.initialize(context, Keys.CLEAN_CONFIG_URL);//清理的配置
//    }
//
//    private IAnalytics iAnalytics = new IAnalytics() {
//        @Override
//        public void sendEvent(String action, String label, Long value) {
//            Log.d("Analytics", action + "," + label + "," + value);
//        }
//
//        @Override
//        public void sendEventOncePerDay(String action, String label) {
//            Log.d("Analytics", action + "," + label);
//        }
//    };
//}
