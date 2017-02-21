package dotc.android.happybuy.modules.login;

import android.content.Intent;
import android.os.Bundle;

import dotc.android.happybuy.social.facebook.FbLoginHelper;
import dotc.android.happybuy.social.google.GoogleLoginHelper;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by wangjun on 16/12/5.
 */

public class AbstractLoginActivity extends BaseActivity {

    protected FbLoginHelper mFbLoginHelper;
    protected GoogleLoginHelper mGoogleLoginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFbLoginHelper = new FbLoginHelper(this);
        mGoogleLoginHelper = new GoogleLoginHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFbLoginHelper.destroy();
        mGoogleLoginHelper.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbLoginHelper.onActivityResult(requestCode,resultCode,data);
        mGoogleLoginHelper.onActivityResult(requestCode,resultCode,data);
    }


}
