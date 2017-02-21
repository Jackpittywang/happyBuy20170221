package dotc.android.happybuy.config;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.config.local.CategoryConfig;
import dotc.android.happybuy.config.local.H5Config;
import dotc.android.happybuy.config.local.IconConfig;
import dotc.android.happybuy.config.local.RechargeConfig;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoBilling;
import dotc.android.happybuy.http.result.PojoConfig;
import dotc.android.happybuy.http.result.PojoH5;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.http.result.PojoPayConfig;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/5/5.
 */
public class ConfigManager {

    private final String TAG = this.getClass().getSimpleName();
    private static ConfigManager mInstance;

    private Context mContext;
    private AtomicBoolean mFetching;
    private AtomicBoolean mFetchingPayment;

    private H5Config mH5Config;
    private CategoryConfig mCategoryConfig;
    private RechargeConfig mRechargeConfig;

    private ConfigManager(Context context){
        this.mContext = context;
        mFetching = new AtomicBoolean(false);
        mFetchingPayment = new AtomicBoolean(false);
    }

    public static ConfigManager get(Context context) {
        if (mInstance == null) {
            synchronized (ConfigManager.class) {
                if (mInstance == null) {
                    mInstance = new ConfigManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public void asyncFetch(boolean force){
        long lastUpdateTime = PrefUtils.getLong(mContext, PrefConstants.Config.LAST_UPDATE_TIME,0);
        if(force || System.currentTimeMillis() - lastUpdateTime>=1*24*60*60*1000){
            if(!mFetching.get()){
                mFetching.set(true);
                fetchRemoteConfig();
            }
        }
//        fetchPaymentConfig();
    }

    public void fetchPayment(){
        if(mFetchingPayment.get()){
            return;
        }
        long lastPaymentSuccessTime = PrefUtils.getLong(mContext, PrefConstants.Config.LAST_PAYMENT_SUCCESS_TIME,0);
        if(System.currentTimeMillis() - lastPaymentSuccessTime<1*24*60*60*1000){
            return;
        }
        long lastPaymentUpdateTime = PrefUtils.getLong(mContext, PrefConstants.Config.LAST_PAYMENT_UPDATE_TIME,0);
        if(System.currentTimeMillis() - lastPaymentUpdateTime>=5*60*1000){
            fetchPaymentConfig();
        }
    }

    public H5Config getH5Config(){
        if(mH5Config == null){
            mH5Config = H5Config.newLocalConfig();
        }
        return mH5Config;
    }

    public CategoryConfig getCategoryConfig(){
        if(mCategoryConfig == null){
            mCategoryConfig = CategoryConfig.newLocalConfig(mContext);
        }
        return mCategoryConfig;
    }

    public RechargeConfig getRechargeConfig(){
        if(mRechargeConfig == null){
            mRechargeConfig = RechargeConfig.newLocalConfig(mContext);
        }
        return mRechargeConfig;
    }

    private void fetchRemoteConfig(){
        HBLog.d(TAG + " fetchRemoteConfig");
        String url = HttpProtocol.URLS.CONFIG;
        Map<String,Object> params = new HashMap<>();
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoConfig>() {
            @Override
            public void onSuccess(PojoConfig config) {
                HBLog.d(TAG + " fetchRemoteConfig onSuccess " + config);
                mFetching.set(false);
                PrefUtils.putLong(mContext, PrefConstants.Config.LAST_UPDATE_TIME, System.currentTimeMillis());
                H5Config.saveToLocal(config.h5);
                CategoryConfig.saveToLocal(config.categories);
                IconConfig.saveToLocal(config.icons);
                PrefUtils.putString(PrefConstants.Config.AB_URL_PREFIX, config.ab_url_prefix);
                mCategoryConfig=CategoryConfig.newLocalConfig(mContext);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG +" fetchRemoteConfig onFailed " + code + " " + message + " " + e);
                mFetching.set(false);
            }

            @Override
            public Class<PojoConfig> getObjectClass() {
                return PojoConfig.class;
            }
        });
    }

    public void fetchPaymentConfig(){
        HBLog.d(TAG + " fetchPaymentConfig");
        String url = HttpProtocol.URLS.RECHARGE_LIST;
        Map<String,Object> params = new HashMap<>();
        params.put("app_id",HttpProtocol.AppId.APP_ID);
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoPayConfig>() {
            @Override
            public void onSuccess(PojoPayConfig config) {
                HBLog.d(TAG + " fetchRemoteConfig onSuccess " + config);
                mFetchingPayment.set(false);
                PrefUtils.putLong(mContext, PrefConstants.Config.LAST_PAYMENT_SUCCESS_TIME,System.currentTimeMillis());
                RechargeConfig.saveToLocal(config.list);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG +" fetchRemoteConfig onFailed " + code + " " + message + " " + e);
                mFetchingPayment.set(false);
            }

            @Override
            public Class<PojoPayConfig> getObjectClass() {
                return PojoPayConfig.class;
            }
        });
        PrefUtils.putLong(mContext, PrefConstants.Config.LAST_PAYMENT_UPDATE_TIME, System.currentTimeMillis());
        mFetchingPayment.set(true);
    }


}
