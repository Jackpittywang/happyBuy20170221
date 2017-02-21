package dotc.android.happybuy.config.abtest;

import android.content.Context;

import com.google.android.gms.update.util.StringUtil;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.config.abtest.bean.AbtestConfigBean;
import dotc.android.happybuy.config.abtest.core.ConfigLoader;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/11/30.
 */

public class AbConfigManager {
    private final static String TAG = AbConfigManager.class.getSimpleName();
    private static AbConfigManager sInstance;
    public static AbConfigManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AbConfigManager.class) {
                if (sInstance == null) {
                    sInstance = new AbConfigManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }
    private Context mContext;
    private ConfigLoader<AbtestConfigBean> mLoader;
    private AbtestConfigBean mAppBean;

    private AbConfigManager(Context context) {
        this.mContext = context;
        mLoader = new ConfigLoader<>(mContext,AbtestConfigBean.class,"app_config.json","96");
        mAppBean = mLoader.loadConfig();
        updateFromServerIfNeeded();
    }

    public AbtestConfigBean getConfig(){
        return mAppBean;
    }

    private void updateFromServerIfNeeded(){
        long lastUpdateTimestamp = PrefUtils.getLong(mContext, PrefConstants.AbtestConfig.LAST_UPDATE_TIME,0);
        if(System.currentTimeMillis() - lastUpdateTimestamp > 4*60*60*1000){
            loadRemoteConfig();
        }
    }

    private String url(){
        StringBuilder sb = new StringBuilder();
        String abUrl = PrefUtils.getString(mContext, PrefConstants.Config.AB_URL_PREFIX);
        if (StringUtil.isEmpty(abUrl)) {
            abUrl = HttpProtocol.URLS.ABTEST;
        }
        sb.append(abUrl);
        if(abUrl.contains("?")){
            if(!abUrl.endsWith("?")){
                sb.append("&");
            }
        } else {
            sb.append("?");
        }
        sb.append("pubid=");
        sb.append("515");
        sb.append("&moduleid=");
        sb.append("96");
        sb.append("&bid=");
        sb.append(AppUtil.getBucketId(mContext));
        sb.append("&pkg_name=");
        sb.append(mContext.getPackageName());
        sb.append("&pkg_ver=");
        sb.append(AppUtil.getVersionCode(mContext));
        sb.append("&").append(HttpProtocol.Header.TOKEN).append("=");
        sb.append(PrefUtils.getString(PrefConstants.Token.TOKEN, ""));
        sb.append("&").append(HttpProtocol.Header.UID).append("=");
        sb.append(PrefUtils.getString(PrefConstants.UserInfo.UID, ""));
        sb.append("&").append(HttpProtocol.Header.APP_ID).append("=");
        sb.append(HttpProtocol.AppId.APP_ID);
        sb.append("&").append(HttpProtocol.Header.PRD_ID).append("=");
        sb.append(HttpProtocol.ProducteId.PRD_ID);
        sb.append("&").append(HttpProtocol.Header.COUNTRY).append("=");
        sb.append(AppUtil.getMetaData(GlobalContext.get(),"country"));
        sb.append("&").append(HttpProtocol.Header.LANGUAGE).append("=");
        sb.append(Languages.getInstance().getLanguage());
        return sb.toString();
    }

    private void loadRemoteConfig(){
        String url = url();
        HBLog.d(TAG,"loadRemoteConfig "+url);
        Network.get(GlobalContext.get()).asyncCPlusConfig(url, new Network.JsonCallBack<AbtestConfigBean>() {
            @Override
            public void onSuccess(AbtestConfigBean appBean) {
                HBLog.d(TAG,"loadRemoteConfig onSuccess "+appBean);
                if(appBean.isValid()&&(mAppBean == null||appBean.getVersion().compareTo(mAppBean.getVersion())>0)){
                    PrefUtils.putLong(mContext, PrefConstants.AbtestConfig.LAST_UPDATE_TIME,System.currentTimeMillis());
                    mLoader.updateConfig(appBean);
                    mAppBean = appBean;
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG,"loadRemoteConfig onFailed code:"+code+" message:"+message);
            }

            @Override
            public Class<AbtestConfigBean> getObjectClass() {
                return AbtestConfigBean.class;
            }
        });
    }

//    public interface OnConfigChangeListener{
//        public void onConfigChange(T oldConfig,T newConfig);
//    }
}
