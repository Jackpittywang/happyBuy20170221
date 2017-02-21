package dotc.android.happybuy.uibase.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.login.func.UserTokenManager;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.modules.HBApplication;
import dotc.android.happybuy.modules.login.LoginActivity;

/**
 * Created by wangjun on 16/3/25.
 */
public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printDebug("onCreate");
        getMyApplication().addTaskStack(this);
//        initLanguage();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        printDebug("onNewIntent");
    }

    @Override
    protected void onResume() {
        printDebug("onResume");
        super.onResume();
//        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        printDebug("onPause");
        super.onPause();
//        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMyApplication().removeTaskStack(this);
        printDebug("onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        printDebug("onStart");
        Analytics.onActivityStart(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        printDebug("onStop");
        Analytics.onActivityStop(this);
    }

//    private void initLanguage(){
//        String userChoiceLanguage = Languages.getInstance(this).getUserChoiceLanguage();
//        String appLanguage = Languages.getInstance(this).getLocalLanguage();
//        printDebug("switchLanguage userChoiceLanguage:"+userChoiceLanguage+" appLanguage:"+appLanguage);
//        if (!TextUtils.isEmpty(userChoiceLanguage)&&!userChoiceLanguage.equals(appLanguage)){
//            switchLanguage(userChoiceLanguage);
//        }
//    }
//
//    public void switchLanguage(String language){
//        printDebug("switchLanguage language:"+language);
//        Resources resources = getResources();
//        Configuration config = resources.getConfiguration();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        if (language.equals("ไทย")){
//            config.locale = new Locale("th", "");
//        } else{
//            config.locale = Locale.ENGLISH;
//        }
//        resources.updateConfiguration(config,dm);
//
//        Languages.getInstance(this).switchLanguage(language);
//
//    }

    public void startActivityIfLogined(Intent intent){
        if (isLogin()) {
            startActivity(intent);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public boolean isLogin(){
        return UserTokenManager.getInstance(this).isTokenValid();
    }

    public String getUid(){
        return PrefUtils.getString(PrefConstants.Network.uid, "");
    }

    public HBApplication getMyApplication() {
        return (HBApplication) getApplication();
    }

    private void printDebug(String message){
        if(true){
            HBLog.v(TAG + " ----- " + message);
        }
    }


}
