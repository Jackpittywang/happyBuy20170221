package dotc.android.happybuy.modules.active;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.update.util.StringUtil;

import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.modules.schema.SchemeProcessor;
import dotc.android.happybuy.modules.splash.SplashActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by wangzhiyuan on 2017/1/4.
 */

public class NoTopUpActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!SchemeProcessor.handle(this,readClickAction())||StringUtil.isEmpty(readClickAction())){
            startActivity(new Intent(this, SplashActivity.class));
        }
        Analytics.sendUIEvent(AnalyticsEvents.Notification.Click_NativeNoti, "no_topup", null);
        finish();
    }

    public  String readClickAction(){
        return AbConfigManager.getInstance(this).getConfig().notify.long_topup.click_action;
    }
}
