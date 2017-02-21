package mobi.dotc.socialnetworks.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import mobi.dotc.socialnetworks.SocialHelper;
import mobi.dotc.socialnetworks.SocialType;

/**
 * 该类用于获取google plus用户的个人信息
 * 流程说明:
 * 开发文档参考https://developers.google.com/+/mobile/android/people#retrieve_profile_information_for_a_signed_in_user
 * 1,实例化GoogleApiClient对象
 * 2,GoogleApiClient建立连接,回调onConnectionFailed(),然后弹出账号选择Activity
 * 3,账号选择Activity返回result中判断是否选择了账号，如果选择了，再次GoogleApiClient建立连接
 * 4,回调onConnected()，这时候可以湖区person个人资料
 */

public class GoogleHelper extends SocialHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final int REQUEST_CODE_CHIOCE_ACCOUT = 0;

    public interface LoadingGoogleSocialStateListener {
        void loadinggooglesocial(boolean isloading);
    }

    private LoadingGoogleSocialStateListener mloadingstateListener;

    @Override
    public SocialType getSocialType() {
        return SocialType.GooglePlus;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        buildGoogleApiClient();
    }

    @Override
    public void onDestory() {
        unbuildGoogleApiClient();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHIOCE_ACCOUT){
            //resultCode -1表示选择了账号,0表示没有选择账号
            if (resultCode == -1){
                mGoogleApiClient.connect();
                if (mloadingstateListener != null){
                    mloadingstateListener.loadinggooglesocial(true);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
    private static final String TAG = "GoogleHelper";
    private GoogleApiClient mGoogleApiClient;
    private Activity myActivity;

    public GoogleHelper(Activity activity,LoadingGoogleSocialStateListener loadingGoogleSocialStateListener) {
        mloadingstateListener = loadingGoogleSocialStateListener;
        myActivity = activity;
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(myActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);
        mGoogleApiClient = builder.build();
        return mGoogleApiClient;
    }

    private void unbuildGoogleApiClient(){
        if (mGoogleApiClient != null){
            signout();
        }
    }

    public boolean isConnected() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            return true;
        else
            return false;
    }

    public Person getPerson() {
        if (!isConnected()){
            return null;
        }
        // Retrieve some profile information to personalize our app for the user.
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        return currentUser;
    }

    public void signin() {
        unbuildGoogleApiClient();
        mGoogleApiClient.connect();
        if (mloadingstateListener != null){
            mloadingstateListener.loadinggooglesocial(true);
        }
    }

    public void signout() {
        if (!mGoogleApiClient.isConnecting()) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    /* onConnected is called when our Activity successfully connects to Google
     * Play services.  onConnected indicates that an account was selected on the
     * device, that the selected account has granted any requested permissions to
     * our app and that we were able to establish a service connection to Google
     * Play services.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Reaching onConnected means we consider the user signed in.
        if (mloadingstateListener != null){
            mloadingstateListener.loadinggooglesocial(false);
        }
        Log.i(TAG, "onConnected()");
        Log.i("GOOGLE","连接成功 onConnected()");
        if (getOnSigninListener() != null) {
            getOnSigninListener().onSuccess(getSocialType());
        }

        if (getOnLoadProfileListener() != null) {
            getOnLoadProfileListener().onSuccess(SocialType.GooglePlus);
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("GOOGLE","连接延期 onConnectionSuspended()");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mloadingstateListener != null){
            mloadingstateListener.loadinggooglesocial(false);
        }
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.i("GOOGLE","onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.toString());
        if (getOnSigninListener() != null ) {
            getOnSigninListener().onError(getSocialType(), "errorCode" + result.getErrorCode(),result.getErrorCode());
        }
        if (result.hasResolution()) {
            Log.d("GOOGLE", "onConnectionFailed 1");
            try {
                Log.d("GOOGLE", "onConnectionFailed 2");
                result.startResolutionForResult(myActivity, REQUEST_CODE_CHIOCE_ACCOUT);
            } catch (IntentSender.SendIntentException e) {
                Log.d("GOOGLE", "onConnectionFailed 3");
//                mGoogleApiClient.connect();
            }
        }
        Log.d("GOOGLE", "onConnectionFailed 4");
    }

}
