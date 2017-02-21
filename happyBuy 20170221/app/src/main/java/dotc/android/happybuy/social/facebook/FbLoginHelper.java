package dotc.android.happybuy.social.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/11/21.
 */

public class FbLoginHelper implements FacebookCallback<LoginResult> {

    private final String TAG = this.getClass().getSimpleName();
    private Activity mActivity;
    private CallbackManager mCallbackManager;
    private boolean mDestroy;
    private LoginCallBack mLoginCallBack;

    public FbLoginHelper(Activity activity){
        mActivity = activity;
        mCallbackManager = CallbackManager.Factory.create();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void destroy(){
        mDestroy = true;
        mLoginCallBack = null;
//        LoginManager.getInstance().
        LoginManager.getInstance().logOut();
    }

    public void login(LoginCallBack loginCallBack){
        mLoginCallBack = loginCallBack;
        LoginManager.getInstance().registerCallback(mCallbackManager,this);
        List<String> permissions = Arrays.asList("email", "public_profile");
        LoginManager.getInstance().logInWithReadPermissions(mActivity,permissions);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        HBLog.d(TAG,"onSuccess "+loginResult);
        if(!mDestroy){
            handleLoginSuccess(loginResult);
        }
    }

    @Override
    public void onCancel() {
        HBLog.d(TAG,"onCancel ");
        if(!mDestroy){
            notifyLoginCancel();
        }
    }

    @Override
    public void onError(FacebookException error) {
        HBLog.d(TAG,"onError "+error);
        if(!mDestroy){
            notifyLoginFailed(-1,"",error);
        }
    }

    private void handleLoginSuccess(LoginResult loginResult){
        AccessToken accessToken = loginResult.getAccessToken();
        notifyLoginUiComplement();
        fetchPublishProfile(accessToken);

        String userId = accessToken.getUserId();
//        accessToken.getDeclinedPermissions();
//        accessToken.getExpires();
//        accessToken.getToken();
//        loginResult.getAccessToken().
        HBLog.d(TAG,"handleLoginSuccess "+userId+" ");
    }

    private void fetchPublishProfile(final AccessToken accessToken){
        HBLog.d(TAG,"fetchEmail ");
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        HBLog.d(TAG,"fetchEmail onCompleted:"+object+" "+response);
                        FacebookRequestError error = response.getError();
                        if(error!=null){
                            notifyLoginFailed(error.getErrorCode(),error.getErrorMessage(),error.getException());
                        } else {
                            String uid = object.optString("id");
                            FacebookSignInAccount account = new FacebookSignInAccount();
                            account.token = accessToken.getToken();
                            account.uid = uid;
                            account.name = object.optString("name");
                            account.email = object.optString("email");
                            account.gender = object.optString("gender");
                            account.birthday = object.optString("birthday");
                            account.picture = ImageRequest.getProfilePictureUri(uid,200,200).toString();
                            account.linkurl = object.optString("link");
                            notifyLoginSuccess(account);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,middle_name,last_name,link,email,gender,birthday,picture");//"public_profile"

        request.setParameters(parameters);
        request.executeAsync();
    }

    private void notifyLoginUiComplement(){
        HBLog.d(TAG,"notifyLoginUiComplement ");
        if(mLoginCallBack !=null){
            mLoginCallBack.onLoginUiComplement();
        }
    }

    private void notifyLoginSuccess(FacebookSignInAccount account){
        HBLog.d(TAG,"notifyLoginSuccess "+account);
        if(mLoginCallBack !=null){
            mLoginCallBack.onLoginSuccess(account);
        }
    }

    private void notifyLoginFailed(int code,String message,Exception e){
        HBLog.d(TAG,"notifyLoginFailed code:"+code+" message:"+message+" "+e);
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
        void onLoginUiComplement();
        void onLoginSuccess(FacebookSignInAccount account);
        void onLoginFailed(int code, String message, Exception e);
        void onLoginCancel();
    }

}
