package dotc.android.happybuy.social;

import android.content.Intent;
import android.os.Bundle;

import dotc.android.happybuy.social.facebook.FaceBookHelper;
import dotc.android.happybuy.social.google.GoogleHelper;
import dotc.android.happybuy.social.impl.OnProfileLoadListener;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by huangli on 16/5/10.
 */
@Deprecated
public abstract class SocialBaseActivity extends BaseActivity implements GoogleHelper.GoogleStateListener,FaceBookHelper.FaceBookStateListener {
    private GoogleHelper googleHelper;
    private FaceBookHelper faceBookHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleHelper = new GoogleHelper(this,this);
        faceBookHelper = new FaceBookHelper(this,this);
        googleHelper.onCreate(savedInstanceState);
        faceBookHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleHelper.onDestory();
        faceBookHelper.onDestory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleHelper.onActivityResult(requestCode, resultCode, data);
        faceBookHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void isGoogleLoading(boolean isLoading) {
        isShowLoadingDialog(isLoading);
    }

    @Override
    public void isFaceBookLoading(boolean isLoading) {
        isShowLoadingDialog(isLoading);
    }

    public void getProfile(SocialType socialType, OnProfileLoadListener onProfileLoadListener){
        if (socialType == SocialType.Facebook){
            faceBookHelper.getProfile(onProfileLoadListener);
        }else if(socialType == SocialType.GooglePlus){
            googleHelper.getProfile(onProfileLoadListener);
        }else{

        }
    }

    public abstract void isShowLoadingDialog(boolean isLoading);

}
