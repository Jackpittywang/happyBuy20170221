package dotc.android.happybuy.modules.main.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by LiShen
 * on 2016/12/9.
 */

public class UpdateActivity extends BaseActivity {
    public static final String INTENT_IS_GP_INSTALLED = "intent_is_gp_installed";
    public static final String INTENT_GP_LINK = "intent_gp_link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.sendUIEvent(AnalyticsEvents.Notification.Click_NativeNoti,
                "app_update_notify_click", null);
        boolean isGpInstalled = getIntent().getBooleanExtra(INTENT_IS_GP_INSTALLED, false);
        String gpLink = getIntent().getStringExtra(INTENT_GP_LINK);
        Intent intent;
        if (isGpInstalled) {
            // go to GooglePlay
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=go.android.gogobuy"));
        } else {
            // go to browser
            Uri linkUri;
            try {
                linkUri = Uri.parse(gpLink);
            } catch (Exception e) {
                linkUri = Uri.parse("market://details?id=go.android.gogobuy");
            }
            intent = new Intent(Intent.ACTION_VIEW, linkUri);
        }
        try {
            startActivity(intent);
        } catch (Exception ignore) {
        }
        finish();
    }
}
