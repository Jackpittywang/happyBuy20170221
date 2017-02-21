package dotc.android.happybuy.modules.active;

import android.content.Intent;
import android.os.Bundle;


import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by wangzhiyuan on 2017/1/18.
 */

public class UnAvaliablePushActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (AccountHelper.getInstance(GlobalContext.get()).isLogin()) {
            intent = new Intent(GlobalContext.get(), RedPacketActivity.class);
        } else {
            intent = new Intent(GlobalContext.get(), LoginActivity.class);
        }
        startActivity(intent);
        Analytics.sendUIEvent(AnalyticsEvents.Notification.Click_CloudNoti, null, null);
        finish();
    }

}
