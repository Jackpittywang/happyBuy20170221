package dotc.android.happybuy.modules.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.update.util.StringUtil;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoVerification;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.setting.SettingActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.TokenManager;
import dotc.android.happybuy.social.ProfileInfo;
import dotc.android.happybuy.social.SocialBaseActivity;
import dotc.android.happybuy.social.SocialType;
import dotc.android.happybuy.social.facebook.FacebookSignInAccount;
import dotc.android.happybuy.social.facebook.FbLoginHelper;
import dotc.android.happybuy.social.google.GoogleLoginHelper;
import dotc.android.happybuy.social.google.GoogleSignInAccount;
import dotc.android.happybuy.social.impl.OnProfileLoadListener;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.FormatVerifyUtil;
import dotc.android.happybuy.util.ToastUtils;

public class BindActivity extends AbstractLoginActivity implements View.OnClickListener{
    private ImageView mBtnFacebook;
    private ImageView mBtnTwitter;
    private ImageView mBtnGoogleplus;
    private HBToolbar mToolbar;

    private EditText inputPhoneNumber;
    private EditText mVerificationNumber;
    private TextView getVerification;
    private Button mLoginForPhone;
    private Button mLoginForTourist;
    private TextView mAgreement;
    private ImageView mCleanNumber;
    private CountDownTimer countDownTimer;

    private boolean mVerificationBottonClickable=false;//验证码按钮是否满足获取条件
    private boolean mTimeFinished=true;//倒计时是否结束
    private boolean mVerificationEmpty=true;//验证码输入是否为空
//    private boolean mGetVerificationClickble=true;//验证码请求是否完成

    private static final int REQUEST_CODE_READ_PHONE_STATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        initActionbar();
        initViews();
        requestPermissionIfNeeded();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.bind);
        mToolbar.setDisplayHomeAsUpEnabled(true);
        /*mToolbar.setRightItem(R.drawable.icon_setting, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                startActivity(new Intent(BindActivity.this, SettingActivity.class));
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Settings, null,null);
            }
        });*/
    }

    void initViews() {
        mBtnFacebook = (ImageView) findViewById(R.id.img_facebook);
        mBtnTwitter = (ImageView) findViewById(R.id.img_twitter);
        mBtnGoogleplus = (ImageView) findViewById(R.id.img_googleplus);

        mLoginForTourist = (Button) findViewById(R.id.tv_login_for_tourist);
        mLoginForPhone = (Button) findViewById(R.id.login_for_phone);
        getVerification = (TextView) findViewById(R.id.tv_get_verification);
        mAgreement=  (TextView) findViewById(R.id.tv_login_for_phonr_tips);
        mCleanNumber=  (ImageView) findViewById(R.id.iv_clean_phonenumber);

        mVerificationNumber = (EditText) findViewById(R.id.et_verification);
        mVerificationNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mVerificationNumber.isFocused()){
                    Analytics.sendUIEvent(AnalyticsEvents.PhoneLogin.Click_GetCode, null, null);
                }
            }
        });
        mVerificationNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = mVerificationNumber.getText().toString().trim();
                if(!StringUtil.isEmpty(name)){
                    mVerificationEmpty=false;
                    if(mVerificationBottonClickable){
                        mLoginForPhone.setEnabled(true);
                    }else {
                        mLoginForPhone.setEnabled(false);
                    }
                }else {
                    mVerificationEmpty=true;
                    mLoginForPhone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        inputPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(inputPhoneNumber.isFocused()){
                    Analytics.sendUIEvent(AnalyticsEvents.PhoneLogin.Click_PhoneNumber, null, null);
                }
            }
        });

        inputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = inputPhoneNumber.getText().toString().trim();
                if(FormatVerifyUtil.isMobileNO(getMyApplication(),name)&&mTimeFinished){
                    getVerification.setClickable(true);
                    getVerification.setBackgroundResource(R.drawable.bg_red_color);
                    getVerification.setTextColor(getResources().getColor(R.color.standard_red_normal));
                    mVerificationBottonClickable=true;
                }else {
                    getVerification.setClickable(false);
                    mVerificationBottonClickable=false;
                    getVerification.setBackgroundResource(R.drawable.bg_gray_color);
                    getVerification.setTextColor(getResources().getColor(R.color.standard_font_light));
                    mLoginForPhone.setEnabled(false);
                }
                if(!mVerificationEmpty&&FormatVerifyUtil.isMobileNO(getMyApplication(),name)){
                    mLoginForPhone.setEnabled(true);
                }else {
                    mLoginForPhone.setEnabled(false);
                }
                if(!StringUtil.isEmpty(name)){
                    mCleanNumber.setVisibility(View.VISIBLE);
                }else {
                    mCleanNumber.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String tipsHead=getString(R.string.login_tips_head);
        String tipsEnd=getString(R.string.login_tips_end);
        SpannableStringBuilder style =new SpannableStringBuilder(tipsHead+tipsEnd);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#4990E2")), tipsHead.length(), style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAgreement.setText(style);

        getVerification.setClickable(false);
        getVerification.setOnClickListener(this);
        mLoginForPhone.setOnClickListener(this);
        mLoginForTourist.setOnClickListener(this);
        mCleanNumber.setOnClickListener(this);
        mBtnFacebook.setOnClickListener(this);
        mBtnGoogleplus.setOnClickListener(this);
        mBtnTwitter.setOnClickListener(this);
        mAgreement.setOnClickListener(this);

    }

    private ProgressDialog logindialog;

    private void dismissLoginingDialog(){
        if (logindialog != null){
            logindialog.dismiss();
        }
    }

    private void showLoginingDialog(){
        if (logindialog == null){
            logindialog = new ProgressDialog(this);
        }
        logindialog.setMessage(getString(R.string.logining));
        logindialog.show();
    }

    private  void bindWithPhone(String number,String verification){
        if(isFinishing()){
            return;
        }
        showLoginingDialog();
        bind(HttpProtocol.UserType.PHONE,number,verification,new JSONObject());
    }

    private void bind(String type,String account,String password,JSONObject extra){
        Map<String,Object> params = new HashMap<>();
        params.put("uid",PrefUtils.getString(PrefConstants.ANONYMOUS.ANONYMOUS_UID, ""));
        params.put("token", URLDecoder.decode(PrefUtils.getString(PrefConstants.ANONYMOUS.ANONYMOUS_TOKEN, "")));
        params.put("password",password);
        params.put("account",account);
        params.put("type",type);
        params.put("extra", extra);
        String bindUrl =HttpProtocol.URLS.BIND_URL+"?packageName="+GlobalContext.get().getPackageName()+"&packageVer="+String.valueOf(AppUtil.getVersionCode(GlobalContext.get()));
        Network.get(GlobalContext.get()).asyncPost(bindUrl, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone pojoNone) {
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_success, "phone", null);
                PrefUtils.putString(PrefConstants.ANONYMOUS.ANONYMOUS_UID, "");
                PrefUtils.putString(PrefConstants.ANONYMOUS.ANONYMOUS_TOKEN, "");
                dismissLoginingDialog();
                gotoUserCenter();
                Toast.makeText(GlobalContext.get(), "success" , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                if(code==HttpProtocol.CODE.CODE_REPEAT_BIND){
                    Toast.makeText(GlobalContext.get(), R.string.have_band, Toast.LENGTH_LONG).show();
                }else if(code==HttpProtocol.CODE.CODE_ALREADY_BIND){
                    Toast.makeText(GlobalContext.get(), R.string.bind_other_user, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
                }
                dismissLoginingDialog();

            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
    }

    private void anonymousRegister(String account, String password){
        if(isFinishing()){
            return;
        }
        showLoginingDialog();
        AccountHelper.getInstance(getApplicationContext()).anonymousRegister(account, password,listner);
    }

    private void anonymousLogin(String anonymousUid, String password){
        if(isFinishing()){
            return;
        }
        showLoginingDialog();
        AccountHelper.getInstance(getApplicationContext()).anonymousLogin(anonymousUid, password,listner);
    }

    AccountHelper.OnLoginResult listner = new AccountHelper.OnLoginResult() {
        @Override
        public void onLoginSucceed(int type) {
            dismissLoginingDialog();
            gotoUserCenter();
            TokenManager.get(GlobalContext.get()).uploadTokenToServerWhenLoginIfNeeded();
        }

        @Override
        public void onLoginFailed(int type,String message,int errorcode) {
            dismissLoginingDialog();
            ToastUtils.showShortToast(getApplicationContext(), getString(R.string.failed_connected_server));
        }
    };


    private void gotoUserCenter() {
        Intent intent=new Intent(this, MainTabActivity.class);
        intent.putExtra(MainTabActivity.EXTRA_TAB_INDEX,MainTabActivity.TAB_INDEX_3);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void onFacebookBindClick(View view){
        mFbLoginHelper.login(new FbLoginHelper.LoginCallBack() {
            @Override
            public void onLoginUiComplement() {
                showLoginingDialog();
            }

            @Override
            public void onLoginSuccess(FacebookSignInAccount account) {
                HBLog.d(TAG,"onLoginSuccess "+Thread.currentThread().getName());
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_success, "fb", null);
                if(AppUtil.isActivityDestroyed(BindActivity.this)){
                    return;
                }
                bind(HttpProtocol.UserType.FACEBOOK,account.uid,account.token,AccountHelper.extraFromFacebook(account));
            }

            @Override
            public void onLoginFailed(int code, String message, Exception e) {
                HBLog.d(TAG,"onLoginFailed "+Thread.currentThread().getName());
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_fail, message, (long) code);
                dismissLoginingDialog();
            }

            @Override
            public void onLoginCancel() {
                HBLog.d(TAG,"onLoginCancel "+Thread.currentThread().getName());
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_fail, "login cancel", -1l);
                dismissLoginingDialog();
            }
        });
    }

    private void onGoogleBindClick(View view){
        showLoginingDialog();
        mGoogleLoginHelper.login(new GoogleLoginHelper.LoginCallBack() {
            @Override
            public void onLoginSuccess(GoogleSignInAccount account) {
                HBLog.d(TAG,"onLoginSuccess "+Thread.currentThread().getName());
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_success, "g", null);
                if(AppUtil.isActivityDestroyed(BindActivity.this)){
                    return;
                }
                bind(HttpProtocol.UserType.GOOGLE,account.uid,account.token,AccountHelper.extraFromGoogle(account));
            }

            @Override
            public void onLoginFailed(int code, String message, Exception e) {
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_fail, message, (long) code);
                HBLog.d(TAG,"onLoginFailed "+Thread.currentThread().getName());
                dismissLoginingDialog();
                if(code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
                    Toast.makeText(GlobalContext.get(), R.string.error_login_google_service_update_tips, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GlobalContext.get(), R.string.error_authorize, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLoginCancel() {
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Bound_fail, "login cancel", -1l);
                HBLog.d(TAG,"onLoginCancel "+Thread.currentThread().getName());
                dismissLoginingDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_facebook:
                Analytics.sendUIEvent(AnalyticsEvents.Login.Login_FB, "bind", null);
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Click_bound, "fb", null);
                onFacebookBindClick(view);
                break;
            case R.id.img_googleplus:
                Analytics.sendUIEvent(AnalyticsEvents.Login.Login_G, "bind", null);
                Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Click_bound, "g", null);
                onGoogleBindClick(view);
                break;
            case R.id.iv_clean_phonenumber: {
                inputPhoneNumber.setText("");
            }
            break;
            case R.id.tv_login_for_phonr_tips: {
                String url = ConfigManager.get(BindActivity.this).getH5Config().agreement;
                Intent intent = new Intent(BindActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getString(R.string.activity_agreement));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(url));
                startActivity(intent);
            }
            break;
            case R.id.tv_get_verification: {
                    Analytics.sendUIEvent(AnalyticsEvents.PhoneLogin.Click_Code, null, null);
                    mTimeFinished=false;
                    getVerification.setClickable(false);
                    getVerification.setBackgroundResource(R.drawable.bg_gray_color);
                    getVerification.setTextColor(getResources().getColor(R.color.standard_font_light));
                    String number=inputPhoneNumber.getText().toString().trim();
                    getVerificationCode(FormatVerifyUtil.checkMobileNumber(this,number));
            }
            break;
            case R.id.tv_login_for_tourist: {

            }
            break;
            case R.id.login_for_phone: {

                String number=inputPhoneNumber.getText().toString().trim();
                String verification=mVerificationNumber.getText().toString().trim();
                if(!StringUtil.isEmpty(number)){
                    if(!StringUtil.isEmpty(verification)){
                        Analytics.sendUIEvent(AnalyticsEvents.VisitorLogin.Click_bound, "mobile", null);
                        Analytics.sendUIEvent(AnalyticsEvents.PhoneLogin.Login_mobile, "bind", null);
                        bindWithPhone(FormatVerifyUtil.checkMobileNumber(this,number),verification);
                    }else {
                        Toast.makeText(getMyApplication(),R.string.verification_number_empty,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getMyApplication(),R.string.phone_number_empty,Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }




    private void  getVerificationCode(String number){
        Map<String,Object> params = new HashMap<>();
        params.put("account",number);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.VERIFICATION, params, new Network.JsonCallBack<PojoVerification>() {
            @Override
            public void onSuccess(PojoVerification pojoVerification) {
//                getVerification.setBackgroundResource(R.drawable.bg_red_color);
//                getVerification.setTextColor(getResources().getColor(R.color.verification_red));
                sendMassege(Integer.parseInt(pojoVerification.valid_time));
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                getVerification.setBackgroundResource(R.drawable.bg_red_color);
                getVerification.setTextColor(getResources().getColor(R.color.standard_red_normal));
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), R.string.failed_connected_server, Toast.LENGTH_LONG).show();
                mTimeFinished=true;
                if(mVerificationBottonClickable){
                    getVerification.setClickable(true);
                }else {
                    getVerification.setClickable(false);
                }
            }

            @Override
            public Class<PojoVerification> getObjectClass() {
                return PojoVerification.class;
            }
        });

    }


    private void sendMassege(int time){
        countDownTimer= new CountDownTimer(time*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                String text=getString(R.string.send_again)+" "+millisUntilFinished/1000+"s";
                getVerification.setText(text);
            }

            @Override
            public void onFinish() {
                mTimeFinished=true;
                if(mVerificationBottonClickable){
                    getVerification.setClickable(true);
                }else {
                    getVerification.setClickable(false);
                }
                String name = inputPhoneNumber.getText().toString().trim();
                if(FormatVerifyUtil.isMobileNO(getMyApplication(),name)){
                    mVerificationBottonClickable=true;
                    getVerification.setBackgroundResource(R.drawable.bg_red_color);
                    getVerification.setTextColor(getResources().getColor(R.color.standard_red_normal));
                }
                getVerification.setText(R.string.send_again);
            }
        };
        countDownTimer.start();
    }

    private void requestPermissionIfNeeded(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        HBLog.d(TAG + " onRequestPermissionsResult requestCode:" + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_READ_PHONE_STATE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
//                    finish();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


}
