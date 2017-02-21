package dotc.android.happybuy.modules.login.func;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoLogin;
import dotc.android.happybuy.http.result.PojoVerfyToken;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.PushHelper;
import dotc.android.happybuy.social.facebook.FacebookSignInAccount;
import dotc.android.happybuy.social.google.GoogleSignInAccount;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.RandomStringUtils;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by zhanqiang.mei on 2016/3/30.
 */
public class AccountHelper {
    private final static  String TAG = AccountHelper.class.getSimpleName();
    private static AccountHelper instance;

    public static AccountHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (AccountHelper.class) {
                if (instance == null) {
                    instance = new AccountHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public static JSONObject extraFromFacebook(FacebookSignInAccount account){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nick", account.name);
            jsonObject.put("avatar", URLEncoder.encode(account.picture, "UTF-8"));
            jsonObject.put("device_id", AppUtil.getDeviceId(GlobalContext.get()));
            jsonObject.put("email", account.email);
            jsonObject.put("gender", account.gender);
            jsonObject.put("location", account.location);
            jsonObject.put("link_url", URLEncoder.encode(account.linkurl, "UTF-8"));
            jsonObject.put("birthday", account.birthday);

        } catch (Exception e){}
        return jsonObject;
    }

    public static JSONObject extraFromGoogle(GoogleSignInAccount account){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nick", account.name);
            jsonObject.put("avatar", URLEncoder.encode(account.picture, "UTF-8"));
            jsonObject.put("device_id", AppUtil.getDeviceId(GlobalContext.get()));
            jsonObject.put("email",account.email);
            jsonObject.put("gender",account.gender);
            jsonObject.put("location",account.location);
//            jsonObject.put("link_url", URLEncoder.encode(account.l, "UTF-8"));
            jsonObject.put("birthday", account.birthday);
        } catch (Exception e){}
        return jsonObject;
    }


    private Context mContext;

    private AccountHelper(Context context) {
        mContext = context;
    }

    @Deprecated
    public boolean isLogin() {
        return isTokenValid();
    }

    @Deprecated
    /*  instead of UserTokenManager method isTokenValid */
    public boolean isTokenValid() {
        return UserTokenManager.getInstance(GlobalContext.get()).isTokenValid();
    }

    public void loginWithFacebok(FacebookSignInAccount account,OnLoginResult callback){
        JSONObject extra = extraFromFacebook(account);
        login(account.uid,account.token,Integer.parseInt(HttpProtocol.UserType.FACEBOOK),extra,callback);
    }

    public void loginWithGoogle(GoogleSignInAccount account,OnLoginResult callback){
        JSONObject extra = extraFromGoogle(account);
        login(account.uid,account.token,Integer.parseInt(HttpProtocol.UserType.GOOGLE),extra,callback);
    }



    public void loginPhone(String phoneNumber, String password,OnLoginResult callback){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", AppUtil.getDeviceId(GlobalContext.get()));
            jsonObject.put("mark", "1");
        } catch (Exception e){}
        login(phoneNumber,password,Integer.parseInt(HttpProtocol.UserType.PHONE),jsonObject,callback);
    }
    public void anonymousLogin(String uid, String password,OnLoginResult callback){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", AppUtil.getDeviceId(GlobalContext.get()));
            jsonObject.put("mark", "1");
        } catch (Exception e){}
        login(uid,password,Integer.parseInt(HttpProtocol.UserType.ANONYMOUS),jsonObject,callback);
    }

    private void login(String account, String password,final int type,JSONObject jsonObject,final OnLoginResult callback){
        Map<String,Object> param = new HashMap<>();


        param.put("account", account);
//        param.put("account", account.substring(0,13)+ RandomStringUtils.getRandomDigitals(8));
        param.put("password", password);
        param.put("type", ""+type);
        param.put("app_id", ""+HttpProtocol.AppId.APP_ID);
        param.put("device_id", ""+AppUtil.getDeviceId(mContext));
        param.put("prd_id", ""+HttpProtocol.ProducteId.PRD_ID);
        param.put("extra", jsonObject);
        //todo
        String loginUrl = HttpProtocol.URLS.LOGIN_URL+"?packageName="+GlobalContext.get().getPackageName()+"&packageVer="+String.valueOf(AppUtil.getVersionCode(GlobalContext.get()));
        Network.get(GlobalContext.get()).asyncPost(loginUrl, param, new Network.JsonCallBack<PojoLogin>() {
            @Override
            public void onSuccess(PojoLogin result) {
                HBLog.d(TAG + " login onSuccess " + result);
                UserTokenManager.getInstance(GlobalContext.get()).setToken(result.uid,result.token,result.expire_time * 1000);
                callback.onLoginSucceed(type);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                if(code==HttpProtocol.CODE.VERIFICATION_ERROR){
                    ToastUtils.showShortToast(GlobalContext.get(), R.string.verification_error);
                }
                callback.onLoginFailed(type,message,code);
            }

            @Override
            public Class<PojoLogin> getObjectClass() {
                return PojoLogin.class;
            }
        });
    }

    public void anonymousRegister(String devicedId, String password,OnLoginResult callback){
        final String model = android.os.Build.MODEL.toLowerCase();
//        final String brand = Build.BRAND.toLowerCase();
//        HBLog.d( " anonymousRegister " + model+"----"+brand);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("platform", "android");
            jsonObject.put("device_name", model);
            jsonObject.put("device_id", AppUtil.getDeviceId(GlobalContext.get()));
            jsonObject.put("mark","1");
            jsonObject.put("gcm_token", PushHelper.getGCMToken(GlobalContext.get()));

        } catch (Exception e){}
        register(devicedId,password,Integer.parseInt(HttpProtocol.UserType.ANONYMOUS),jsonObject,callback);
    }
    private void register(String account, String password,final int type,JSONObject jsonObject,final OnLoginResult callback){
        Map<String,Object> param = new HashMap<>();

        param.put("account", account);
//        param.put("account", account.substring(0,13)+ RandomStringUtils.getRandomDigitals(8));
        param.put("password", password);
        param.put("type", ""+type);
        param.put("app_id", ""+HttpProtocol.AppId.APP_ID);
        param.put("device_id", ""+AppUtil.getDeviceId(mContext));
        param.put("prd_id", ""+HttpProtocol.ProducteId.PRD_ID);
        param.put("extra", jsonObject);
        //todo
        String loginUrl = HttpProtocol.URLS.REGISTER_URL+"?packageName="+GlobalContext.get().getPackageName()+"&packageVer="+String.valueOf(AppUtil.getVersionCode(GlobalContext.get()));
        Network.get(GlobalContext.get()).asyncPost(loginUrl, param, new Network.JsonCallBack<PojoLogin>() {
            @Override
            public void onSuccess(PojoLogin result) {
                HBLog.d(TAG + " login onSuccess " + result);
                UserTokenManager.getInstance(GlobalContext.get()).setToken(result.uid,result.token,result.expire_time * 1000);
                try {
                    PrefUtils.putString(PrefConstants.ANONYMOUS.ANONYMOUS_TOKEN, URLEncoder.encode(result.token,"UTF-8"));
                } catch (Exception e){}
                PrefUtils.putString(PrefConstants.ANONYMOUS.ANONYMOUS_UID, result.uid);

                callback.onLoginSucceed(type);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                callback.onLoginFailed(type,message,code);
            }

            @Override
            public Class<PojoLogin> getObjectClass() {
                return PojoLogin.class;
            }
        });
    }

    public interface OnLoginResult {
        void onLoginSucceed(int type);
        void onLoginFailed(int type,String message,int code);
    }
}
