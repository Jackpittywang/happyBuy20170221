package dotc.android.happybuy.push.firebase;

import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import dotc.android.happybuy.push.PushHelper;
import dotc.android.happybuy.push.PushLog;
import dotc.android.happybuy.push.TokenManager;


/**
 * Created by wangjun on 16/7/22.
 * instead with autolog
 */
@Deprecated
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        PushLog.logD(TAG,"onTokenRefresh refreshedToken:"+refreshedToken);
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        // TODO: Implement this method to send any registration to your app's servers.
        if(!TextUtils.isEmpty(refreshedToken)){
            PushHelper.setGCMToken(this,refreshedToken);
            sendRegistrationToServer(refreshedToken);
        }
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        TokenManager.get(this).uploadTokenToServerIfNeeded(token);
    }

//    private void sub(){
//        FirebaseMessaging.getInstance().subscribeToTopic("mytopic");
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("mytopic");
//    }


}
