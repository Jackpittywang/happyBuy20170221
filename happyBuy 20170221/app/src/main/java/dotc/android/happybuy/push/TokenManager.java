package dotc.android.happybuy.push;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/4/13.
 */
public class TokenManager {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private static TokenManager mInstance;

    private TokenManager(Context context) {
        mContext = context;
    }

    public static TokenManager get(Context context) {
        if (mInstance == null) {
            synchronized (TokenManager.class) {
                if (mInstance == null) {
                    mInstance = new TokenManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public String getFirebaseToken(){
        try {
            return FirebaseInstanceId.getInstance().getToken();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
        return null;
    }

    public void uploadTokenToServerWhenLoginIfNeeded(){
        String gcmToken = PushHelper.getGCMToken(GlobalContext.get());
        PushLog.logD(TAG, "uploadTokenToServerWhenLoginIfNeeded token:" + gcmToken);
        if(!TextUtils.isEmpty(gcmToken)){
            uploadTokenToServer(gcmToken);
        }
    }

    public void uploadTokenToServerIfNeeded(final String newToken){
        String uid = PrefUtils.getString(PrefConstants.Network.uid, "");
        if(!TextUtils.isEmpty(uid)){
            uploadTokenToServer(newToken);
        }
    }

    private void uploadTokenToServer(final String newToken){
        PushLog.logD(TAG, "sendRegistrationToServer token:" + newToken);
        if(TextUtils.isEmpty(newToken)||newToken.equals(PushHelper.getLastUploadGCMToken(GlobalContext.get()))){
            return;
        }
        PushLog.logD(TAG, "sendRegistrationToServer to do");
        String url = HttpProtocol.URLS.GCM_TOKEN;
        Map<String,Object> params = new HashMap<>();
        params.put("gcm_token", newToken);
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone pojoGcm) {
                PushLog.logD(TAG, "sendRegistrationToServer onSuccess " + pojoGcm);
                PushHelper.setLastUploadGCMToken(GlobalContext.get(), newToken);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                PushLog.logD(TAG, "sendRegistrationToServer onFailed " + code+" "+message+" "+e);
            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
    }




}
