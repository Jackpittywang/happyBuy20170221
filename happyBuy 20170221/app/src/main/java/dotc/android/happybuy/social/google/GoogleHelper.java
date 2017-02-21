package dotc.android.happybuy.social.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import dotc.android.happybuy.social.ProfileInfo;
import dotc.android.happybuy.social.SocialHelper;
import dotc.android.happybuy.social.SocialType;
import dotc.android.happybuy.social.impl.OnProfileLoadListener;

/**
 * Created by huangli on 16/5/10.
 * 该类用于获取google plus用户的个人信息
 * 流程说明:
 * 开发文档参考https://developers.google.com/+/mobile/android/people#retrieve_profile_information_for_a_signed_in_user
 * 1,实例化GoogleApiClient对象
 * 2,GoogleApiClient建立连接,回调onConnectionFailed(),然后弹出账号选择Activity
 * 3,账号选择Activity返回result中判断是否选择了账号，如果选择了，再次GoogleApiClient建立连接
 * 4,回调onConnected()，这时候可以获取person个人资料
 *
 * note:之前遇到过换包名后导致登录不上的问题,解决方法：
 * 1,登录https://developers.google.com/mobile/add?platform=android&cntapi=signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fsign-in%3Fconfigured%3Dtrue&cntlbl=Continue%20Adding%20Sign-In
 * 2,登录开发者帐号,重新注册新包名，然后再生成sha1填入，生成google-services.json
 * */
@Deprecated
public class GoogleHelper extends SocialHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_CODE_CHIOCE_ACCOUT = 0;
    private static final String TAG = "GoogleHelper";

    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private GoogleStateListener mGoogleStateListener;

    public interface GoogleStateListener{
        void isGoogleLoading(boolean isLoading);
    }

    public GoogleHelper(Activity activity,GoogleStateListener googleStateListener){
        mActivity = activity;
        mGoogleStateListener = googleStateListener;
    }

    private GoogleApiClient buildGoogleApiClient() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);
        mGoogleApiClient = builder.build();
        return mGoogleApiClient;
    }

    private void unbuildGoogleApiClient(){
        if (mGoogleApiClient != null){
            if (!mGoogleApiClient.isConnecting()) {
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
            }
        }
    }

    private void connectToGoogleService(){
        if (mGoogleApiClient != null){
            mGoogleApiClient.connect();
            if (mGoogleStateListener != null){
                mGoogleStateListener.isGoogleLoading(true);
            }
        }
    }

    public void getProfile(OnProfileLoadListener onProfileLoadListener) {
        unbuildGoogleApiClient();
        connectToGoogleService();
        setOnProfileLoadListener(onProfileLoadListener);
    }

    private boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    private Person getPerson() {
        if (!isConnected()){
            return null;
        }
        // Retrieve some profile information to personalize our app for the user.
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        return currentUser;
    }

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
            switch (resultCode){
                case Activity.RESULT_OK:
                    if (resultCode == -1){
                        connectToGoogleService();   //try again
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    if(getOnProfileLoadListener()!=null){
                        getOnProfileLoadListener().onProfileLoadedError(getSocialType(),"login cancel",0);
                    }
                    break;
                default:
                    if(getOnProfileLoadListener()!=null){
                        getOnProfileLoadListener().onProfileLoadedError(getSocialType(),"",0);
                    }
                    break;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleStateListener != null){
            mGoogleStateListener.isGoogleLoading(false);
        }
        if (getOnProfileLoadListener() != null) {
            Person person = getPerson();
            if (person != null){
                Person.Image avatar = person.getImage();
                String headUrl = "";
                if (avatar != null) {
                    headUrl = avatar.getUrl();
                }
                String id = person.getId();
                String name = person.getDisplayName();
                getOnProfileLoadListener().onProfileLoadedSuccess(getSocialType(),new ProfileInfo("GooglePlus",id,"",name,headUrl));
            }else{
                getOnProfileLoadListener().onProfileLoadedError(getSocialType(),"",0);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        if (mGoogleStateListener != null){
            mGoogleStateListener.isGoogleLoading(true);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("GOOGLE", "onConnectionFailed");
        if (mGoogleStateListener != null){
            mGoogleStateListener.isGoogleLoading(false);
        }
        if (result.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            Log.d("GOOGLE", "SERVICE_VERSION_UPDATE_REQUIRED");
            if (getOnProfileLoadListener() != null){
                getOnProfileLoadListener().onProfileLoadedError(getSocialType(),"",result.getErrorCode());
            }
        }else if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED){
            Log.d("GOOGLE", "SIGN_IN_REQUIRED 1");
            try {
                Log.d("GOOGLE", "SIGN_IN_REQUIRED 2");
                result.startResolutionForResult(mActivity, REQUEST_CODE_CHIOCE_ACCOUT);
            } catch (IntentSender.SendIntentException e) {
                Log.d("GOOGLE", "SIGN_IN_REQUIRED 3");
//                mGoogleApiClient.connect();
            }
        }else{
            Log.d("GOOGLE", "OTHER");
            if (getOnProfileLoadListener() != null){
                getOnProfileLoadListener().onProfileLoadedError(getSocialType(),"",result.getErrorCode());
            }
        }
    }
}
