package dotc.android.happybuy.modules.login.func;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoUserInfo;
import dotc.android.happybuy.http.result.PojoVerfyToken;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/12/8.
 */

public class UserTokenManager {

    private final static String TAG = AbConfigManager.class.getSimpleName();
    private static UserTokenManager sInstance;

    public static UserTokenManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserTokenManager.class) {
                if (sInstance == null) {
                    sInstance = new UserTokenManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private Context mContext;
    private boolean mLogined;
    private boolean mTokenValid;

    private UserTokenManager(Context context) {
        this.mContext = context;
        mLogined = !TextUtils.isEmpty(getUid());
        mTokenValid = isTokenAvailable();
    }

    public boolean isLogined(){
        return mLogined;
    }

    public boolean isTokenValid(){
        return mTokenValid;
    }

    public String getUid(){
        return PrefUtils.getString(mContext, PrefConstants.UserInfo.UID);
    }

    public void setToken(String uid,String token,long expireTimestamp){
        saveToken(uid,token,expireTimestamp);
        mLogined = true;
        mTokenValid = true;
    }

    public void clear(){
        clearToken();
        clearUserInfo();
        mTokenValid = false;
        mLogined = false;
    }

    private void saveToken(String uid,String token,long expireTimestamp){
        PrefUtils.putString(mContext, PrefConstants.UserInfo.UID,uid);
        PrefUtils.putString(mContext, PrefConstants.Token.TOKEN,token);
        PrefUtils.putLong(mContext, PrefConstants.Token.CREATE_TIME,System.currentTimeMillis());
        PrefUtils.putLong(mContext, PrefConstants.Token.EXPIRE_TIME,expireTimestamp);
    }

    private boolean isTokenAvailable(){
        String strToken = PrefUtils.getString(mContext, PrefConstants.Token.TOKEN);
        if (TextUtils.isEmpty(strToken)) {
            return false;
        } else {
            String loginType = PrefUtils.getString(mContext, PrefConstants.UserInfo.BIND_TYPE);
            if(HttpProtocol.UserType.ANONYMOUS.equals(loginType)){
                return true;
            } else {
                long tokenCreateTime = PrefUtils.getLong(mContext,PrefConstants.Token.CREATE_TIME);
                long tokenExpireTime = PrefUtils.getLong(mContext,PrefConstants.Token.EXPIRE_TIME);
                return System.currentTimeMillis() < tokenCreateTime + tokenExpireTime;//
            }
        }
    }

    private void clearToken(){
        PrefUtils.putString(mContext, PrefConstants.Token.TOKEN,"");
        PrefUtils.putLong(mContext, PrefConstants.Token.CREATE_TIME,0);
        PrefUtils.putLong(mContext, PrefConstants.Token.EXPIRE_TIME,0);
    }

    public void verifyToken(){
        String token = PrefUtils.getString(mContext, PrefConstants.Token.TOKEN);
        if(!TextUtils.isEmpty(token)){
            String loginType = PrefUtils.getString(mContext, PrefConstants.UserInfo.BIND_TYPE);
            if(HttpProtocol.UserType.ANONYMOUS.equals(loginType)){
                return;
            }
            String uid = getUid();
            long tokenCreateTime = PrefUtils.getLong(mContext,PrefConstants.Token.CREATE_TIME);
            long tokenExpireTime = PrefUtils.getLong(mContext,PrefConstants.Token.EXPIRE_TIME);
            if (System.currentTimeMillis()< tokenCreateTime+tokenExpireTime){
                verifyTokenByServer(token,uid);
            } else {
                handleTokenExpire(uid);
            }
        }
    }

    //token验证流程
    private void verifyTokenByServer(String token, final String uid) {
        Map<String,Object> param = new HashMap<>();
        param.put("token", token);
        param.put("uid", uid);

        String loginUrl = HttpProtocol.URLS.TOKEN_VERIFY_URL;
        Network.get(GlobalContext.get()).asyncPost(loginUrl, param, new Network.JsonCallBack<PojoVerfyToken>() {
            @Override
            public void onSuccess(PojoVerfyToken result) {
                HBLog.d(TAG, "verifyToken onSuccess " + result);
                handleTokenChanged(uid,result);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG,"verifyToken onFailed "+code+" "+e);
                if(code!=HttpProtocol.CODE.NET_ERROR){
                    handleTokenExpire(uid);
                }
            }

            @Override
            public Class<PojoVerfyToken> getObjectClass() {
                return PojoVerfyToken.class;
            }
        });
    }

    private void handleTokenChanged(String uid,PojoVerfyToken result){
        saveToken(uid,result.new_token,result.expire_time*1000);
        mTokenValid = true;
    }

    private void handleTokenExpire(String uid){
        clearToken();
        clearUserInfo();
        mTokenValid = false;
        mLogined = false;
    }

    public void saveUserInfo(PojoUserInfo userInfo){
        PrefUtils.putString(PrefConstants.UserInfo.USER_NAME, userInfo.nick);
        PrefUtils.putString(PrefConstants.UserInfo.USER_ICON_URL, userInfo.avatar);
        PrefUtils.putString(PrefConstants.UserInfo.GEO, userInfo.geo);
        PrefUtils.putString(PrefConstants.UserInfo.LEVEL, userInfo.level);
        PrefUtils.putString(PrefConstants.UserInfo.TYPE, userInfo.type);
        PrefUtils.putString(PrefConstants.UserInfo.BIND_TYPE, userInfo.bind_type);
        PrefUtils.putInt(PrefConstants.UserInfo.COIN, userInfo.coin);
        PrefUtils.putInt(PrefConstants.UserInfo.COUPON_COUNT, userInfo.coupon_count);
    }

    private void clearUserInfo(){
        PrefUtils.putString(PrefConstants.UserInfo.USER_NAME, "");
        PrefUtils.putString(PrefConstants.UserInfo.USER_ICON_URL, "");
        PrefUtils.putString(PrefConstants.UserInfo.GEO, "");
        PrefUtils.putString(PrefConstants.UserInfo.LEVEL, "");
        PrefUtils.putString(PrefConstants.UserInfo.TYPE, "");
        PrefUtils.putString(PrefConstants.UserInfo.BIND_TYPE, "");
        PrefUtils.putInt(PrefConstants.UserInfo.COIN, 0);
        PrefUtils.putInt(PrefConstants.UserInfo.COUPON_COUNT, 0);
    }

}
