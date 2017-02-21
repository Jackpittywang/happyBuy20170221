package dotc.android.happybuy.social.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/12/3.
 */

public class GoogleLoginHelper implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = this.getClass().getSimpleName();
    private static final int REQUEST_CODE_CHIOCE_ACCOUT = 0;
    private boolean mDestroy;
    private LoginCallBack mLoginCallBack;
    private FragmentActivity mActivity;
    private GoogleApiClient mGoogleApiClient;

    public GoogleLoginHelper(FragmentActivity activity){
        mActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    public void login(LoginCallBack loginCallBack) {
        mLoginCallBack = loginCallBack;
        unbuildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE_CHIOCE_ACCOUT){
            if(resultCode == Activity.RESULT_OK){
                if(!mDestroy){
                    mGoogleApiClient.connect();
                }
            } else if(resultCode == Activity.RESULT_CANCELED){
                notifyLoginCancel();
            } else {
                notifyLoginFailed(requestCode,"",null);
            }
        }
    }

    public void destroy() {
        mDestroy = true;
        mLoginCallBack = null;
        unbuildGoogleApiClient();
    }

    private void unbuildGoogleApiClient(){
        if (!mGoogleApiClient.isConnecting()) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        if(person!=null){
            GoogleSignInAccount account = new GoogleSignInAccount();
            account.uid = person.getId();
            account.name = person.getDisplayName();
            account.picture = person.getImage()!=null?person.getImage().getUrl():"";
            account.birthday = person.getBirthday();
//            account.email = person.ge
            account.gender = String.valueOf(person.getGender());
            account.location = person.getCurrentLocation();
//            account.linkurl = person.getUrl();
            notifyLoginSuccess(account);
        } else {
            notifyLoginFailed(-1,"can't find person",null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED){
            try {
                Log.d("GOOGLE", "SIGN_IN_REQUIRED 2");
                result.startResolutionForResult(mActivity, REQUEST_CODE_CHIOCE_ACCOUT);
            } catch (IntentSender.SendIntentException e) {
                Log.d("GOOGLE", "SIGN_IN_REQUIRED 3");
            }
        } else {
            notifyLoginFailed(result.getErrorCode(),result.getErrorMessage(),null);
        }
    }

    private void notifyLoginSuccess(GoogleSignInAccount account){
        HBLog.d(TAG+" notifyLoginSuccess "+account);
        if(mLoginCallBack !=null){
            mLoginCallBack.onLoginSuccess(account);
        }
    }

    private void notifyLoginFailed(int code,String message,Exception e){
        HBLog.d(TAG+"notifyLoginFailed code:"+code+" message:"+message+" e:"+e);
        if(mLoginCallBack !=null){
            mLoginCallBack.onLoginFailed(code,message,e);
        }
    }

    private void notifyLoginCancel(){
        if(mLoginCallBack !=null){
            mLoginCallBack.onLoginCancel();
        }
    }

    public interface LoginCallBack {
        void onLoginSuccess(GoogleSignInAccount account);
        void onLoginFailed(int code,String message,Exception e);
        void onLoginCancel();
    }

}
