package dotc.android.happybuy.modules.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.AbtestConfigBean;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.login.func.UserTokenManager;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.setting.about.AboutActivity;
import dotc.android.happybuy.modules.setting.invite.EnterInviteCodeActivity;
import dotc.android.happybuy.modules.setting.invite.InviteWinCoinsActivity;
import dotc.android.happybuy.modules.setting.language.LanguageActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.FileUtils;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private HBToolbar mToolbar;
    private View mPassMgrView;
    private View mAgreementView;
    private View mAboutView;
    private View mLanguageView;
    private Button mButton;
    private View mGroupGeneralView;
    private LinearLayout mLlInviteCode;

    private LinearLayout llSettingsInviteFriend;
    private LinearLayout llSettingsEnterCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initActionbar();
        initUI();
        initBackDoor();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_setting);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mLlInviteCode = (LinearLayout) findViewById(R.id.ll_invite_code);
        mPassMgrView = findViewById(R.id.layout_pass_mgr);
        mAgreementView = findViewById(R.id.layout_agreement);
        mAboutView = findViewById(R.id.layout_about);
        mLanguageView = findViewById(R.id.layout_language);
        mButton = (Button) findViewById(R.id.button_exit);
        mGroupGeneralView = findViewById(R.id.text_general);
        llSettingsInviteFriend = (LinearLayout) findViewById(R.id.llSettingsInviteFriend);
        llSettingsEnterCode = (LinearLayout) findViewById(R.id.llSettingsEnterCode);
        llSettingsInviteFriend.setOnClickListener(this);
        llSettingsEnterCode.setOnClickListener(this);
        mPassMgrView.setOnClickListener(this);
        mAgreementView.setOnClickListener(this);
        mAboutView.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mLanguageView.setOnClickListener(this);
        if (isLogin()) {
            mButton.setVisibility(View.VISIBLE);
            String type = PrefUtils.getString(PrefConstants.UserInfo.BIND_TYPE, "");
            if(HttpProtocol.UserType.ANONYMOUS.equals(type)){
                mButton.setText(R.string.switch_account);
            } else {
                mButton.setText(R.string.user_exit);
            }
        } else {
            mButton.setVisibility(View.GONE);
        }
        if(!isLogin()){
            mLlInviteCode.setVisibility(View.GONE);
        }
    }

    private void loginOut() {
        UserTokenManager.getInstance(this).clear();
//        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        Intent intent = new Intent(SettingActivity.this, MainTabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_pass_mgr:
//                startActivity(new Intent(this,PassMgrActivity.class));
                break;
            case R.id.layout_agreement: {
                String url = ConfigManager.get(this).getH5Config().agreement;
                Intent intent = new Intent(this, WebActivity.class);
//                intent.putExtra(WebActivity.EXTRA_NO_TITLE, true);
                intent.putExtra(WebActivity.EXTRA_TITLE, getString(R.string.activity_agreement));
                //"http://caipiao.163.com/nfop/fwxy/index.htm?"
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(url));
                startActivity(intent);
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_ServiceAgreement, "Settings", null);
                break;
            }
            case R.id.layout_about:
//                Intent intent = new Intent(this, WebActivity.class);
//                intent.putExtra(WebActivity.EXTRA_TITLE, getString(R.string.activity_about));
                //"http://hk.api.wifimaster.mobi/v/t"
//                intent.putExtra(WebActivity.EXTRA_URL, HttpProtocol.H5Config.);
//                startActivity(intent);
                startActivity(new Intent(this, AboutActivity.class));
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_About, null, null);
                break;
            case R.id.layout_language:
                startActivity(new Intent(this, LanguageActivity.class));
                break;
            case R.id.button_exit:
                String type = PrefUtils.getString(PrefConstants.UserInfo.BIND_TYPE, "");
                if (HttpProtocol.UserType.ANONYMOUS.equals(type)) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton(R.string.signout_dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loginOut();
                            Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_LogOut, null, null);
                        }
                    });
                    builder.setNegativeButton(R.string.signout_dialog_cancel, null);
                    builder.setMessage(R.string.signout_dialog_tips);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                break;
            case R.id.llSettingsEnterCode:
                Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickSettingsCode, null, null);
                startActivity(new Intent(this, EnterInviteCodeActivity.class));
                break;
            case R.id.llSettingsInviteFriend:
                Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickSettingsShare, null, null);
                startActivity(new Intent(this, InviteWinCoinsActivity.class));
                break;
        }
    }

    private void initBackDoor(){
        mGroupGeneralView.setOnTouchListener(new View.OnTouchListener() {
            long actionDownTimestamp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    AbtestConfigBean bean = AbConfigManager.getInstance(SettingActivity.this).getConfig();
                    HBLog.d(TAG,"backDoor ACTION_DOWN "+bean);
                    actionDownTimestamp = System.currentTimeMillis();
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    HBLog.d(TAG,"backDoor ACTION_UP");
                    if(System.currentTimeMillis() - actionDownTimestamp >=10*1000){
                        createAbtestFileToSDCard();
                    }
                }
                return true;
            }
        });
    }

    private void createAbtestFileToSDCard(){
        HBLog.d(TAG,"createAbtestFileToSDCard "+Environment.getExternalStorageState());
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File configFile = getFileStreamPath("app_config.json");
            File file = new File(Environment.getExternalStorageDirectory(),"app_config_"+System.currentTimeMillis()+".json");
            HBLog.d(TAG,"ready to copy file: "+file.getAbsolutePath());
            if(FileUtils.copyFile(configFile,file)){
                ToastUtils.showLongToast(this,"copy success to "+file.getAbsolutePath());
            } else {
                ToastUtils.showLongToast(this,"copy failed ");
            }
            logFileContent(configFile);
        }
    }

    private void logFileContent(File file){
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                HBLog.d(TAG,lineTxt);
            }
        } catch (Exception e) {
            HBLog.e(TAG + " parse error:"+e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
