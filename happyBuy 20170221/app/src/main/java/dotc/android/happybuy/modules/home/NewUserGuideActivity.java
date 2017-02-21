package dotc.android.happybuy.modules.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.update.util.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoUserInfo;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.HBApplication;
import dotc.android.happybuy.modules.awarding.widget.CircleCornerLinearLayout;
import dotc.android.happybuy.modules.awarding.widget.LightFocusLayout;
import dotc.android.happybuy.modules.login.func.RegistManager;
import dotc.android.happybuy.modules.login.func.RegistManager.RegistStatusCallBack;
import dotc.android.happybuy.modules.login.func.UserTokenManager;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

public class NewUserGuideActivity extends Activity implements View.OnClickListener,RegistStatusCallBack {

    private final String TAG = this.getClass().getSimpleName();

    public static final int TYPE_0 = 0x00;
    public static final int TYPE_1 = 0x01;
    public static final int TYPE_2 = 0x02;
    public static final String START_TYPE = "start_type";
    private int mCurrentType;


    private TextView mQuit;
    private TextView mProductPeriodTextView;
    private TextView mProductNameTextView;

    private LightFocusLayout mBuyNow;
    private LightFocusLayout mQuitBut;
    private RelativeLayout mRlExit;
    private RelativeLayout mRlJoinAction;


    private RelativeLayout rlBgAnimation;
    private RelativeLayout rlBgExitAnimation;
    private ImageView ivBox;
    private ImageView ivRedbag;
    private CircleCornerLinearLayout ivBgShow;

    private ImageView mLightPoint;
    private RelativeLayout relativeLayout;
    private RelativeLayout relativeWelcomeLayout;
    private TextView mRegist;
    private TextView mWelcomeText;
    private TextView mSuccessful;
    private TextView mTextGogobuy;

    private ImageView mImageViewCionOne;
    private ImageView mImageViewCionTwo;
    private ImageView mImageViewCionThree;
    private ImageView mImageViewCionFour;
    private ImageView mImageViewCionFive;
    private ImageView mImageViewCionSix;
    private ImageView mImageViewCionSeven;
    private ImageView mImageViewCionEight;


    private ImageView mImageViewCionWelcomeOne;
    private ImageView mImageViewCionWelcomeTwo;
    private ImageView mImageViewCionWelcomeThree;
    private ImageView mImageViewCionWelcomeFour;
    private ImageView mImageViewCionWelcomeFive;
    private ImageView mImageViewCionWelcomeSix;
    private ImageView mImageViewCionWelcomeSeven;
    private ImageView mImageViewCionWelcomeEight;

    private EditText etEnterInviteCode;
    private TextView imageViewOne;
    private TextView imageViewTwo;
    private TextView imageViewThree;
    private TextView imageViewFour;
    private TextView imageViewFive;
    private static int mDurationTime = 500;
    private static int mDuration700Time = 700;
    private static int mLongDurationTime = 800;
    private static int mLongLongDurationTime = 1000;
    private static int mDurationTime2min = 2000;
    private static int mDurationTime3min = 3000;
    private float lastnumber = 5;
    private boolean submitCooldown;

    private Animator mAnimator;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        obtainArgsFromIntent(getIntent());
        HBLog.d(TAG + " onCreate");
        setContentView(R.layout.activity_new_user_guide);
        initUI();
        mRlExit.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRlExit.getViewTreeObserver().removeOnPreDrawListener(this);
                showView();
                return true;
            }
        });
        this.setFinishOnTouchOutside(false);
    }


    private void initUI() {


        etEnterInviteCode = (EditText) findViewById(R.id.et_input_invitation);
        etEnterInviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.LoginGuide.ClickWindowPromotion, null, null);
            }
        });

        mRlExit = (RelativeLayout) findViewById(R.id.rl_exit);
        mRlJoinAction = (RelativeLayout) findViewById(R.id.rl_join_action);

        mQuit = (TextView) findViewById(R.id.tv_number_five);



        mProductPeriodTextView = (TextView) findViewById(R.id.textview_product_period);
        mProductNameTextView = (TextView) findViewById(R.id.textview_product_name);

        mBuyNow = (LightFocusLayout) findViewById(R.id.textview_buy_now);
        mQuitBut = (LightFocusLayout) findViewById(R.id.tv_no);

        mQuitBut.setOnClickListener(this);
        mBuyNow.setOnClickListener(this);

        findViewById(R.id.no_thanks).setOnClickListener(this);
        findViewById(R.id.tv_no).setOnClickListener(this);

        //注册成功id
        rlBgAnimation = (RelativeLayout) findViewById(R.id.rl_bg_animation);
        rlBgExitAnimation = (RelativeLayout) findViewById(R.id.rl_exit);

        mRegist = (TextView) findViewById(R.id.tv_regist);
        mWelcomeText = (TextView) findViewById(R.id.text_welcome);
        if (Languages.getInstance().getLanguage().equals("vn")) {
            mWelcomeText.setTextSize(28);
        }

        mSuccessful = (TextView) findViewById(R.id.tv_successful);
        mTextGogobuy = (TextView) findViewById(R.id.text_gogobuy);

        ivBox = (ImageView) findViewById(R.id.iv_box);
        ivRedbag = (ImageView) findViewById(R.id.iv_redbag);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeWelcomeLayout = (RelativeLayout) findViewById(R.id.rl_text_five_coins_free);


//        ivBgShow = (ImageView) findViewById(R.id.iv_bg_show);
        ivBgShow = (CircleCornerLinearLayout) findViewById(R.id.iv_bg_show);
        mLightPoint = (ImageView) findViewById(R.id.iv_show_light_point);

        mImageViewCionOne = (ImageView) findViewById(R.id.iv_cion_one);
        mImageViewCionTwo = (ImageView) findViewById(R.id.iv_coin_two);
        mImageViewCionThree = (ImageView) findViewById(R.id.iv_coin_three);
        mImageViewCionFour = (ImageView) findViewById(R.id.iv_coin_four);
        mImageViewCionFive = (ImageView) findViewById(R.id.iv_coin_five);
        mImageViewCionSix = (ImageView) findViewById(R.id.iv_coin_six);
        mImageViewCionSeven = (ImageView) findViewById(R.id.iv_coin_seven);
        mImageViewCionEight = (ImageView) findViewById(R.id.iv_coin_eight);

        imageViewOne = (TextView) findViewById(R.id.imageView_one);
        imageViewTwo = (TextView) findViewById(R.id.imageView_two);
        imageViewThree = (TextView) findViewById(R.id.imageView_three);
        imageViewFour = (TextView) findViewById(R.id.imageView_four);
        imageViewFive = (TextView) findViewById(R.id.imageView_five);


        mImageViewCionWelcomeOne = (ImageView) findViewById(R.id.iv_coin_welcome_one);
        mImageViewCionWelcomeTwo = (ImageView) findViewById(R.id.iv_coin_welcome_two);
        mImageViewCionWelcomeThree = (ImageView) findViewById(R.id.iv_coin_welcome_three);
        mImageViewCionWelcomeFour = (ImageView) findViewById(R.id.iv_coin_welcome_four);
        mImageViewCionWelcomeFive = (ImageView) findViewById(R.id.iv_coin_welcome_five);
        mImageViewCionWelcomeSix = (ImageView) findViewById(R.id.iv_coin_welcome_six);
        mImageViewCionWelcomeSeven = (ImageView) findViewById(R.id.iv_coin_welcome_seven);
        mImageViewCionWelcomeEight = (ImageView) findViewById(R.id.iv_coin_welcome_eight);


        mQuit.setText(AbConfigManager.getInstance(this).getConfig().coins+"");

//        showView();
    }

    private void showView() {
        switch (mCurrentType) {
            case 1:
                mRlExit.setVisibility(View.GONE);
                mRlJoinAction.setVisibility(View.VISIBLE);
                startAnimator();
                break;
            case 2:
                mRlExit.setVisibility(View.VISIBLE);
                mRlJoinAction.setVisibility(View.GONE);
                startQuitAnimator();
                break;
        }
    }

    private void obtainArgsFromIntent(Intent intent) {
        if (intent != null) {
            mCurrentType = intent.getIntExtra(START_TYPE, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_buy_now:
//                mBuyNow.getHighLightAnim(2080,0,findViewById(R.id.imageview_sunshine)).start();
                finish();
                ((HBApplication) getApplication()).finishAllActivity();
                Intent buyNow = new Intent(NewUserGuideActivity.this, MainTabActivity.class);
                buyNow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(buyNow);
                break;
            case R.id.no_thanks:
                Analytics.sendUIEvent(AnalyticsEvents.LoginGuide.Click_Newuser_No, null, null);
                Analytics.sendUIEvent(AnalyticsEvents.LoginGuide.ClickWindowClose, null, null);
                if(UserTokenManager.getInstance(this).isTokenValid()){
                    showLoginingDialog();
                    getUserStatus();
                }else {
                    finish();
                }
                break;
            case R.id.tv_no:
                showLoginingDialog();
                Analytics.sendUIEvent(AnalyticsEvents.LoginGuide.Click_Newuser_Yes, null, null);
                Analytics.sendUIEvent(AnalyticsEvents.LoginGuide.ClickWindowOK, null, null);
                    String invite_code = etEnterInviteCode.getEditableText().toString().trim();
                    if(!StringUtil.isEmpty(invite_code)){
                        if(UserTokenManager.getInstance(this).isTokenValid()){
                            submitInviteCode();
                        }else {
                            if(RegistManager.get(this).getRegistStatus()){
                                RegistManager.get(this).addCallBack(this);
                            }else {
                                dismissLoginingDialog();
                                ToastUtils.showShortToast(NewUserGuideActivity.this,
                                        R.string.invite_code_enter_wrong_in_regist);
                            }
                        }
                    }
                if(UserTokenManager.getInstance(this).isTokenValid()){
                    getUserStatus();
                }else {
                    dismissLoginingDialog();
                    finish();
                }
                break;

        }
    }

    public void getUserStatus(){
        String uid = PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, "");
        Map<String,Object> param = new HashMap<>();
        param.put("from_uid", uid);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.USER_INFO, param, new Network.JsonCallBack<PojoUserInfo>() {
            @Override
            public void onSuccess(PojoUserInfo userInfo) {
                dismissLoginingDialog();
                if(userInfo.coin>0&&!userInfo.is_finish_newbieguide){
                    PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,userInfo.is_finish_newbieguide);
                    finish();
                    ((HBApplication) getApplication()).finishAllActivity();
                    Intent buyNow = new Intent(NewUserGuideActivity.this, MainTabActivity.class);
                    buyNow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(buyNow);
                }else {
                    finish();
                }

            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                dismissLoginingDialog();
                finish();
            }

            @Override
            public Class<PojoUserInfo> getObjectClass() {
                return PojoUserInfo.class;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        RegistManager.get(this).removeCallBack(this);
    }


    @Override
    public void onBackPressed() {

    }

    private Dialog logindialog;

    private void dismissLoginingDialog() {
        if (logindialog != null) {
            logindialog.dismiss();
            finish();
        }
    }

    private void showLoginingDialog() {
        if (logindialog == null) {
            logindialog = new Dialog(this,R.style.RechargeDialog);
            logindialog.setContentView(R.layout.regist_dialog);
        }
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.regist_progress_rotate);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        logindialog.findViewById(R.id.rl_progress).startAnimation(animation);
        logindialog.show();
        logindialog.setCancelable(false);
    }


    private void submitInviteCode() {
        String invite_code = etEnterInviteCode.getEditableText().toString().trim();
        submitCooldown = true;
        Map<String, Object> param = new HashMap<>();
        param.put("uid", PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, ""));
        param.put("device_id", AppUtil.getDeviceId(this));
        param.put("invite_code", invite_code);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.USE_INVITE_CODE, param,
                new Network.JsonCallBack<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        if (!AppUtil.isActivityDestroyed(NewUserGuideActivity.this)) {
                            submitCooldown = false;
                            dismissLoginingDialog();
                            ToastUtils.showShortToast(NewUserGuideActivity.this,
                                    R.string.invite_code_enter_success);
                        }
                    }
                    @Override
                    public void onFailed(int code, String message, Exception e) {
                        if (!AppUtil.isActivityDestroyed(NewUserGuideActivity.this)) {
                            submitCooldown = false;
                            dismissLoginingDialog();
                            ToastUtils.showShortToast(NewUserGuideActivity.this,
                                    R.string.invite_code_enter_wrong_in_regist);


                        }
                    }

                    @Override
                    public Class<JSONObject> getObjectClass() {
                        return JSONObject.class;
                    }
                });
    }


    private void startQuitAnimator() {
        Animator quit = mQuitBut.getHighLightAnim(2080, 800, findViewById(R.id.imageview_sunshine_three));
        ObjectAnimator objectAnimatorBackgrand = ObjectAnimator.ofFloat(rlBgExitAnimation, "scaleX", 0, 1.2f, 0.9f, 1.0f).setDuration(300);


        //welcome文字
        AnimatorSet animatorSetTextWelcome = new AnimatorSet();
        ObjectAnimator animatorSetTextWelcomeTranslationX = ObjectAnimator.ofFloat(mWelcomeText, "translationX", 300f, -200f, 100f, -50f, 10f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator animatorSetTextWelcomeScaleX = ObjectAnimator.ofFloat(mWelcomeText, "scaleX", 0, 2.0f, 0.8f, 1.1f, 0.95f, 1.0f).setDuration(mLongLongDurationTime);
        ObjectAnimator animatorSetTextWelcomeScaleY = ObjectAnimator.ofFloat(mWelcomeText, "scaleY", 0, 2.0f, 0.8f, 1.1f, 0.95f, 1.0f).setDuration(mLongLongDurationTime);
        ObjectAnimator animatorSetTextWelcomeAlpha = ObjectAnimator.ofFloat(mWelcomeText, "alpha", 0, 1.0f).setDuration(mLongLongDurationTime);
        animatorSetTextWelcome.setStartDelay(100);
        animatorSetTextWelcome.playTogether(animatorSetTextWelcomeTranslationX, animatorSetTextWelcomeScaleX, animatorSetTextWelcomeScaleY, animatorSetTextWelcomeAlpha);

        //gogobuy文字
        AnimatorSet animatorSetTextGogobuy = new AnimatorSet();
        ObjectAnimator animatorSetTextGogobuyRotation = ObjectAnimator.ofFloat(mTextGogobuy, "rotation", -180, 0).setDuration(mDurationTime);
        ObjectAnimator animatorSetTextGogobuyScaleX = ObjectAnimator.ofFloat(mTextGogobuy, "scaleX", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator animatorSetTextGogobuyScaleY = ObjectAnimator.ofFloat(mTextGogobuy, "scaleY", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator animatorSetTextGogobuyAlpha = ObjectAnimator.ofFloat(mTextGogobuy, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetTextGogobuy.setStartDelay(100);
        animatorSetTextGogobuy.playTogether(animatorSetTextGogobuyRotation, animatorSetTextGogobuyScaleX, animatorSetTextGogobuyScaleY, animatorSetTextGogobuyAlpha);

        //红包
        AnimatorSet animatorSetRedbag = new AnimatorSet();
        ObjectAnimator objectAnimatorRedbagTranslationX = ObjectAnimator.ofFloat(ivRedbag, "translationX", 400f, -50f, 50f, 0f).setDuration(mDuration700Time);
        ObjectAnimator objectAnimatorRedbagScaleX = ObjectAnimator.ofFloat(ivRedbag, "scaleX", 0, 1.5f, 0.9f, 1f).setDuration(mDuration700Time);
        ObjectAnimator objectAnimatorRedbagScaleY = ObjectAnimator.ofFloat(ivRedbag, "scaleY", 0, 1.5f, 0.9f, 1f).setDuration(mDuration700Time);
        ObjectAnimator objectAnimatorRedbagAlpha = ObjectAnimator.ofFloat(ivRedbag, "alpha", 0, 1.0f).setDuration(mDuration700Time);
        animatorSetRedbag.setStartDelay(150);
        animatorSetRedbag.playTogether(objectAnimatorRedbagTranslationX, objectAnimatorRedbagScaleX, objectAnimatorRedbagScaleY, objectAnimatorRedbagAlpha);

        //coin文字
        AnimatorSet animatorSetText = new AnimatorSet();
        ObjectAnimator objectAnimatorTextScaleX = ObjectAnimator.ofFloat(relativeWelcomeLayout, "scaleX", 0, 1.2f, 0.95f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorTextScaleY = ObjectAnimator.ofFloat(relativeWelcomeLayout, "scaleY", 0, 1.2f, 0.95f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorTextAlpha = ObjectAnimator.ofFloat(relativeWelcomeLayout, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetText.setStartDelay(200);
        animatorSetText.playTogether(objectAnimatorTextScaleX, objectAnimatorTextAlpha, objectAnimatorTextScaleY);

        //按钮
        AnimatorSet animatorSetButton = new AnimatorSet();
        ObjectAnimator objectAnimatorButtonScaleX = ObjectAnimator.ofFloat(mQuitBut, "scaleX", 0, 1.3f, 0.9f, 1.0f).setDuration(mLongDurationTime);
        ObjectAnimator objectAnimatorButtonScaleY = ObjectAnimator.ofFloat(mQuitBut, "scaleY", 0, 1.3f, 0.9f, 1.0f).setDuration(mLongDurationTime);
        ObjectAnimator objectAnimatorButtonAlpha = ObjectAnimator.ofFloat(mQuitBut, "alpha", 0, 1.0f).setDuration(mLongDurationTime);
        animatorSetButton.setStartDelay(300);
        animatorSetButton.playTogether(objectAnimatorButtonScaleX, objectAnimatorButtonScaleY, objectAnimatorButtonAlpha);
        animatorSetButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.no_thanks).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_input_invitation).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        //welcome金币发射
        AnimatorSet animatorSetCionOne = new AnimatorSet();
        ObjectAnimator objectAnimatorCionOneRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "rotation", 250f, -30f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "rotationX", -200f, 0f, 180, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "rotationY", 169f, -60f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "translationY", 150f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "translationX", 200f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "scaleX", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "scaleY", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeOne, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionOne.setStartDelay(600);
        animatorSetCionOne.playTogether(objectAnimatorCionOneTranslationY, objectAnimatorCionOneTranslationX, objectAnimatorCionOneScaleX,
                objectAnimatorCionOneRotation, objectAnimatorCionOneRotationX, objectAnimatorCionOneRotationY, objectAnimatorCionOneScaleY, objectAnimatorCionOneAlpha);

        AnimatorSet animatorSetCionTwo = new AnimatorSet();
        ObjectAnimator objectAnimatorCionTwoRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "rotation", 250f, -30f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "rotationX", -200f, 0f, 180, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "rotationY", 169f, -60f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "translationY", 460f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "translationX", -230f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "scaleX", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "scaleY", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeTwo, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionTwo.setStartDelay(600);
        animatorSetCionTwo.playTogether(objectAnimatorCionTwoTranslationY, objectAnimatorCionTwoTranslationX, objectAnimatorCionTwoScaleX,
                objectAnimatorCionTwoRotation, objectAnimatorCionTwoRotationX, objectAnimatorCionTwoRotationY, objectAnimatorCionTwoScaleY, objectAnimatorCionTwoAlpha);


        AnimatorSet animatorSetCionThree = new AnimatorSet();
        ObjectAnimator objectAnimatorCionThreeRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "rotation", 250f, -30f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "rotationX", -200f, 0f, 180, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "rotationY", 169f, -60f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "translationY", 430f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "translationX", -180f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "scaleX", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "scaleY", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeThree, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionThree.setStartDelay(600);
        animatorSetCionThree.playTogether(objectAnimatorCionThreeTranslationY, objectAnimatorCionThreeTranslationX, objectAnimatorCionThreeScaleX,
                objectAnimatorCionThreeRotation, objectAnimatorCionThreeRotationX, objectAnimatorCionThreeRotationY, objectAnimatorCionThreeScaleY, objectAnimatorCionThreeAlpha);

        AnimatorSet animatorSetCionFour = new AnimatorSet();
        ObjectAnimator objectAnimatorCionFourRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "rotation", 250f, -30f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "rotationX", -200f, 0f, 90, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "rotationY", 169f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "translationY", -300f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "translationX", -180f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "scaleX", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionFourScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "scaleY", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionFourAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeFour, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionFour.setStartDelay(800);
        animatorSetCionFour.playTogether(objectAnimatorCionFourTranslationY, objectAnimatorCionFourTranslationX, objectAnimatorCionFourScaleX,
                objectAnimatorCionFourRotation, objectAnimatorCionFourRotationX, objectAnimatorCionFourRotationY, objectAnimatorCionFourScaleY, objectAnimatorCionFourAlpha);

        AnimatorSet animatorSetCionFive = new AnimatorSet();
        ObjectAnimator objectAnimatorCionFiveRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "rotation", -50f, 150f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "translationY", 50f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "translationX", 400f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "scaleX", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "scaleY", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeFive, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionFive.setStartDelay(600);
        animatorSetCionFive.playTogether(objectAnimatorCionFiveTranslationY, objectAnimatorCionFiveTranslationX, objectAnimatorCionFiveScaleX,
                objectAnimatorCionFiveRotation, objectAnimatorCionFiveRotationX, objectAnimatorCionFiveRotationY, objectAnimatorCionFiveScaleY, objectAnimatorCionFiveAlpha);

        AnimatorSet animatorSetCionSix = new AnimatorSet();
        ObjectAnimator objectAnimatorCionSixRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "rotation", -50f, 150f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "translationY", -400f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "translationX", 300f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "scaleX", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "scaleY", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeSix, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionSix.setStartDelay(800);
        animatorSetCionSix.playTogether(objectAnimatorCionSixTranslationY, objectAnimatorCionSixTranslationX, objectAnimatorCionSixScaleX,
                objectAnimatorCionSixRotation, objectAnimatorCionSixRotationX, objectAnimatorCionSixRotationY, objectAnimatorCionSixScaleY, objectAnimatorCionSixAlpha);

        AnimatorSet animatorSetCionSeven = new AnimatorSet();
        ObjectAnimator objectAnimatorCionSevenRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "rotation", -50f, 150f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "translationY", 400f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "translationX", 410f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "scaleX", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "scaleY", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeSeven, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionSeven.setStartDelay(600);
        animatorSetCionSeven.playTogether(objectAnimatorCionSevenTranslationY, objectAnimatorCionSevenTranslationX, objectAnimatorCionSevenScaleX,
                objectAnimatorCionSevenRotation, objectAnimatorCionSevenRotationX, objectAnimatorCionSevenRotationY, objectAnimatorCionSevenScaleY, objectAnimatorCionSevenAlpha);

        AnimatorSet animatorSetCionEight = new AnimatorSet();
        ObjectAnimator objectAnimatorCionEightRotation = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "rotation", -50f, 150f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightRotationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightRotationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightTranslationY = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "translationY", 800f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightTranslationX = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "translationX", 300f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightScaleX = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "scaleX", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightScaleY = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "scaleY", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightAlpha = ObjectAnimator.ofFloat(mImageViewCionWelcomeEight, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionEight.setStartDelay(600);
        animatorSetCionEight.playTogether(objectAnimatorCionEightTranslationY, objectAnimatorCionEightTranslationX, objectAnimatorCionEightScaleX,
                objectAnimatorCionEightRotation, objectAnimatorCionEightRotationX, objectAnimatorCionEightRotationY, objectAnimatorCionEightScaleY, objectAnimatorCionEightAlpha);

        //所有金币汇总
        AnimatorSet animatorSetAllCions = new AnimatorSet();
        animatorSetAllCions.playTogether(animatorSetCionOne, animatorSetCionTwo, animatorSetCionThree,
                animatorSetCionFour, animatorSetCionFive, animatorSetCionSix, animatorSetCionSeven, animatorSetCionEight);


        AnimatorSet animatorSetAllOfAnimator = new AnimatorSet();
        animatorSetAllOfAnimator.playTogether(quit, objectAnimatorBackgrand, animatorSetTextWelcome, animatorSetTextGogobuy, animatorSetRedbag, animatorSetText,
                animatorSetButton, animatorSetAllCions);
        animatorSetAllOfAnimator.start();
    }

    private void startAnimator() {
        Animator sunshine = mBuyNow.getHighLightAnim(2080, 1100, findViewById(R.id.imageview_sunshine));
//        sunshine.setStartDelay(1100);
        // 背景
        ObjectAnimator objectAnimatorBackgrand = ObjectAnimator.ofFloat(rlBgAnimation, "scaleX", 0, 1.2f, 0.9f, 1.0f).setDuration(300);
        //regist文字
        AnimatorSet animatorSetTextRegist = new AnimatorSet();
        ObjectAnimator animatorSetTextRegistTranslationX = ObjectAnimator.ofFloat(mRegist, "translationX", 300f, -200f, 100f, -50f, 10f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator animatorSetTextRegistScaleX = ObjectAnimator.ofFloat(mRegist, "scaleX", 0, 2.0f, 0.8f, 1.1f, 0.95f, 1.0f).setDuration(mLongLongDurationTime);
        ObjectAnimator animatorSetTextRegistScaleY = ObjectAnimator.ofFloat(mRegist, "scaleY", 0, 2.0f, 0.8f, 1.1f, 0.95f, 1.0f).setDuration(mLongLongDurationTime);
        ObjectAnimator animatorSetTextRegistAlpha = ObjectAnimator.ofFloat(mRegist, "alpha", 0, 1.0f).setDuration(mLongLongDurationTime);
        animatorSetTextRegist.setStartDelay(100);
        animatorSetTextRegist.playTogether(animatorSetTextRegistTranslationX, animatorSetTextRegistScaleX, animatorSetTextRegistScaleY, animatorSetTextRegistAlpha);

        //successfully文字
        AnimatorSet animatorSetTextSuccessfully = new AnimatorSet();
        ObjectAnimator animatorSetTextSuccessfullyRotation = ObjectAnimator.ofFloat(mSuccessful, "rotation", -180, 0).setDuration(mDurationTime);
        ObjectAnimator animatorSetTextSuccessfullyScaleX = ObjectAnimator.ofFloat(mSuccessful, "scaleX", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator animatorSetTextSuccessfullyScaleY = ObjectAnimator.ofFloat(mSuccessful, "scaleY", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator animatorSetTextSuccessfullyAlpha = ObjectAnimator.ofFloat(mSuccessful, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetTextSuccessfully.setStartDelay(100);
        animatorSetTextSuccessfully.playTogether(animatorSetTextSuccessfullyRotation, animatorSetTextSuccessfullyScaleX, animatorSetTextSuccessfullyScaleY, animatorSetTextSuccessfullyAlpha);

        //宝箱
        AnimatorSet animatorSetBox = new AnimatorSet();
        ObjectAnimator objectAnimatorBoxTranslationX = ObjectAnimator.ofFloat(ivBox, "translationX", 400f, -50f, 50f, 0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorBoxScaleX = ObjectAnimator.ofFloat(ivBox, "scaleX", 0, 1.5f, 0.9f, 1f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorBoxScaleY = ObjectAnimator.ofFloat(ivBox, "scaleY", 0, 1.5f, 0.9f, 1f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorBoxAlpha = ObjectAnimator.ofFloat(ivBox, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetBox.setStartDelay(100);
        animatorSetBox.playTogether(objectAnimatorBoxTranslationX, objectAnimatorBoxScaleX, objectAnimatorBoxScaleY, objectAnimatorBoxAlpha);
        animatorSetBox.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.iv_shadow).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        //金币发射

        AnimatorSet animatorSetCionOne = new AnimatorSet();
        ObjectAnimator objectAnimatorCionOneRotation = ObjectAnimator.ofFloat(mImageViewCionOne, "rotation", 250f, -30f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneRotationX = ObjectAnimator.ofFloat(mImageViewCionOne, "rotationX", -200f, 0f, 180, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneRotationY = ObjectAnimator.ofFloat(mImageViewCionOne, "rotationY", 169f, -60f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneTranslationY = ObjectAnimator.ofFloat(mImageViewCionOne, "translationY", 150f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneTranslationX = ObjectAnimator.ofFloat(mImageViewCionOne, "translationX", 200f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneScaleX = ObjectAnimator.ofFloat(mImageViewCionOne, "scaleX", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneScaleY = ObjectAnimator.ofFloat(mImageViewCionOne, "scaleY", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionOneAlpha = ObjectAnimator.ofFloat(mImageViewCionOne, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionOne.setStartDelay(600);
        animatorSetCionOne.playTogether(objectAnimatorCionOneTranslationY, objectAnimatorCionOneTranslationX, objectAnimatorCionOneScaleX,
                objectAnimatorCionOneRotation, objectAnimatorCionOneRotationX, objectAnimatorCionOneRotationY, objectAnimatorCionOneScaleY, objectAnimatorCionOneAlpha);

        AnimatorSet animatorSetCionTwo = new AnimatorSet();
        ObjectAnimator objectAnimatorCionTwoRotation = ObjectAnimator.ofFloat(mImageViewCionTwo, "rotation", 250f, -30f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoRotationX = ObjectAnimator.ofFloat(mImageViewCionTwo, "rotationX", -200f, 0f, 180, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoRotationY = ObjectAnimator.ofFloat(mImageViewCionTwo, "rotationY", 169f, -60f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoTranslationY = ObjectAnimator.ofFloat(mImageViewCionTwo, "translationY", 460f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoTranslationX = ObjectAnimator.ofFloat(mImageViewCionTwo, "translationX", -230f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoScaleX = ObjectAnimator.ofFloat(mImageViewCionTwo, "scaleX", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoScaleY = ObjectAnimator.ofFloat(mImageViewCionTwo, "scaleY", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionTwoAlpha = ObjectAnimator.ofFloat(mImageViewCionTwo, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionTwo.setStartDelay(600);
        animatorSetCionTwo.playTogether(objectAnimatorCionTwoTranslationY, objectAnimatorCionTwoTranslationX, objectAnimatorCionTwoScaleX,
                objectAnimatorCionTwoRotation, objectAnimatorCionTwoRotationX, objectAnimatorCionTwoRotationY, objectAnimatorCionTwoScaleY, objectAnimatorCionTwoAlpha);


        AnimatorSet animatorSetCionThree = new AnimatorSet();
        ObjectAnimator objectAnimatorCionThreeRotation = ObjectAnimator.ofFloat(mImageViewCionThree, "rotation", 250f, -30f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeRotationX = ObjectAnimator.ofFloat(mImageViewCionThree, "rotationX", -200f, 0f, 180, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeRotationY = ObjectAnimator.ofFloat(mImageViewCionThree, "rotationY", 169f, -60f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeTranslationY = ObjectAnimator.ofFloat(mImageViewCionThree, "translationY", 430f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeTranslationX = ObjectAnimator.ofFloat(mImageViewCionThree, "translationX", -180f, 0f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeScaleX = ObjectAnimator.ofFloat(mImageViewCionThree, "scaleX", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeScaleY = ObjectAnimator.ofFloat(mImageViewCionThree, "scaleY", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionThreeAlpha = ObjectAnimator.ofFloat(mImageViewCionThree, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionThree.setStartDelay(600);
        animatorSetCionThree.playTogether(objectAnimatorCionThreeTranslationY, objectAnimatorCionThreeTranslationX, objectAnimatorCionThreeScaleX,
                objectAnimatorCionThreeRotation, objectAnimatorCionThreeRotationX, objectAnimatorCionThreeRotationY, objectAnimatorCionThreeScaleY, objectAnimatorCionThreeAlpha);

        AnimatorSet animatorSetCionFour = new AnimatorSet();
        ObjectAnimator objectAnimatorCionFourRotation = ObjectAnimator.ofFloat(mImageViewCionFour, "rotation", 250f, -30f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourRotationX = ObjectAnimator.ofFloat(mImageViewCionFour, "rotationX", -200f, 0f, 90, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourRotationY = ObjectAnimator.ofFloat(mImageViewCionFour, "rotationY", 169f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourTranslationY = ObjectAnimator.ofFloat(mImageViewCionFour, "translationY", -300f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourTranslationX = ObjectAnimator.ofFloat(mImageViewCionFour, "translationX", -180f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFourScaleX = ObjectAnimator.ofFloat(mImageViewCionFour, "scaleX", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionFourScaleY = ObjectAnimator.ofFloat(mImageViewCionFour, "scaleY", 0, 1f).setDuration(mLongLongDurationTime);
        ObjectAnimator objectAnimatorCionFourAlpha = ObjectAnimator.ofFloat(mImageViewCionFour, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionFour.setStartDelay(800);
        animatorSetCionFour.playTogether(objectAnimatorCionFourTranslationY, objectAnimatorCionFourTranslationX, objectAnimatorCionFourScaleX,
                objectAnimatorCionFourRotation, objectAnimatorCionFourRotationX, objectAnimatorCionFourRotationY, objectAnimatorCionFourScaleY, objectAnimatorCionFourAlpha);

        AnimatorSet animatorSetCionFive = new AnimatorSet();
        ObjectAnimator objectAnimatorCionFiveRotation = ObjectAnimator.ofFloat(mImageViewCionFive, "rotation", -50f, 150f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveRotationX = ObjectAnimator.ofFloat(mImageViewCionFive, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveRotationY = ObjectAnimator.ofFloat(mImageViewCionFive, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveTranslationY = ObjectAnimator.ofFloat(mImageViewCionFive, "translationY", 50f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveTranslationX = ObjectAnimator.ofFloat(mImageViewCionFive, "translationX", 400f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveScaleX = ObjectAnimator.ofFloat(mImageViewCionFive, "scaleX", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveScaleY = ObjectAnimator.ofFloat(mImageViewCionFive, "scaleY", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionFiveAlpha = ObjectAnimator.ofFloat(mImageViewCionFive, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionFive.setStartDelay(600);
        animatorSetCionFive.playTogether(objectAnimatorCionFiveTranslationY, objectAnimatorCionFiveTranslationX, objectAnimatorCionFiveScaleX,
                objectAnimatorCionFiveRotation, objectAnimatorCionFiveRotationX, objectAnimatorCionFiveRotationY, objectAnimatorCionFiveScaleY, objectAnimatorCionFiveAlpha);

        AnimatorSet animatorSetCionSix = new AnimatorSet();
        ObjectAnimator objectAnimatorCionSixRotation = ObjectAnimator.ofFloat(mImageViewCionSix, "rotation", -50f, 150f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixRotationX = ObjectAnimator.ofFloat(mImageViewCionSix, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixRotationY = ObjectAnimator.ofFloat(mImageViewCionSix, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixTranslationY = ObjectAnimator.ofFloat(mImageViewCionSix, "translationY", -400f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixTranslationX = ObjectAnimator.ofFloat(mImageViewCionSix, "translationX", 300f, 0f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixScaleX = ObjectAnimator.ofFloat(mImageViewCionSix, "scaleX", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixScaleY = ObjectAnimator.ofFloat(mImageViewCionSix, "scaleY", 0, 1f).setDuration(mDurationTime3min);
        ObjectAnimator objectAnimatorCionSixAlpha = ObjectAnimator.ofFloat(mImageViewCionSix, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionSix.setStartDelay(800);
        animatorSetCionSix.playTogether(objectAnimatorCionSixTranslationY, objectAnimatorCionSixTranslationX, objectAnimatorCionSixScaleX,
                objectAnimatorCionSixRotation, objectAnimatorCionSixRotationX, objectAnimatorCionSixRotationY, objectAnimatorCionSixScaleY, objectAnimatorCionSixAlpha);

        AnimatorSet animatorSetCionSeven = new AnimatorSet();
        ObjectAnimator objectAnimatorCionSevenRotation = ObjectAnimator.ofFloat(mImageViewCionSeven, "rotation", -50f, 150f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenRotationX = ObjectAnimator.ofFloat(mImageViewCionSeven, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenRotationY = ObjectAnimator.ofFloat(mImageViewCionSeven, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenTranslationY = ObjectAnimator.ofFloat(mImageViewCionSeven, "translationY", 400f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenTranslationX = ObjectAnimator.ofFloat(mImageViewCionSeven, "translationX", 410f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenScaleX = ObjectAnimator.ofFloat(mImageViewCionSeven, "scaleX", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenScaleY = ObjectAnimator.ofFloat(mImageViewCionSeven, "scaleY", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionSevenAlpha = ObjectAnimator.ofFloat(mImageViewCionSeven, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionSeven.setStartDelay(600);
        animatorSetCionSeven.playTogether(objectAnimatorCionSevenTranslationY, objectAnimatorCionSevenTranslationX, objectAnimatorCionSevenScaleX,
                objectAnimatorCionSevenRotation, objectAnimatorCionSevenRotationX, objectAnimatorCionSevenRotationY, objectAnimatorCionSevenScaleY, objectAnimatorCionSevenAlpha);

        AnimatorSet animatorSetCionEight = new AnimatorSet();
        ObjectAnimator objectAnimatorCionEightRotation = ObjectAnimator.ofFloat(mImageViewCionEight, "rotation", -50f, 150f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightRotationX = ObjectAnimator.ofFloat(mImageViewCionEight, "rotationX", -120f, 0f, 60, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightRotationY = ObjectAnimator.ofFloat(mImageViewCionEight, "rotationY", -69f, 60f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightTranslationY = ObjectAnimator.ofFloat(mImageViewCionEight, "translationY", 800f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightTranslationX = ObjectAnimator.ofFloat(mImageViewCionEight, "translationX", 300f, 0f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightScaleX = ObjectAnimator.ofFloat(mImageViewCionEight, "scaleX", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightScaleY = ObjectAnimator.ofFloat(mImageViewCionEight, "scaleY", 0, 1f).setDuration(mDurationTime2min);
        ObjectAnimator objectAnimatorCionEightAlpha = ObjectAnimator.ofFloat(mImageViewCionEight, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetCionEight.setStartDelay(600);
        animatorSetCionEight.playTogether(objectAnimatorCionEightTranslationY, objectAnimatorCionEightTranslationX, objectAnimatorCionEightScaleX,
                objectAnimatorCionEightRotation, objectAnimatorCionEightRotationX, objectAnimatorCionEightRotationY, objectAnimatorCionEightScaleY, objectAnimatorCionEightAlpha);

        //所有金币汇总
        AnimatorSet animatorSetAllCions = new AnimatorSet();
        animatorSetAllCions.playTogether(animatorSetCionOne, animatorSetCionTwo, animatorSetCionThree,
                animatorSetCionFour, animatorSetCionFive, animatorSetCionSix, animatorSetCionSeven, animatorSetCionEight);


        //coin文字
        AnimatorSet animatorSetText = new AnimatorSet();
        ObjectAnimator objectAnimatorTextScaleX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", 0, 1.2f, 0.95f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorTextScaleY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", 0, 1.2f, 0.95f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorTextAlpha = ObjectAnimator.ofFloat(relativeLayout, "alpha", 0, 1.0f).setDuration(mDurationTime);
        animatorSetText.setStartDelay(200);
        animatorSetText.playTogether(objectAnimatorTextScaleX, objectAnimatorTextAlpha, objectAnimatorTextScaleY);

        //按钮
        AnimatorSet animatorSetButton = new AnimatorSet();
        ObjectAnimator objectAnimatorButtonScaleX = ObjectAnimator.ofFloat(mBuyNow, "scaleX", 0, 1.3f, 0.9f, 1.0f).setDuration(mLongDurationTime);
        ObjectAnimator objectAnimatorButtonScaleY = ObjectAnimator.ofFloat(mBuyNow, "scaleY", 0, 1.3f, 0.9f, 1.0f).setDuration(mLongDurationTime);
        ObjectAnimator objectAnimatorButtonAlpha = ObjectAnimator.ofFloat(mBuyNow, "alpha", 0, 1.0f).setDuration(mLongDurationTime);
        animatorSetButton.setStartDelay(300);
        animatorSetButton.playTogether(objectAnimatorButtonScaleX, objectAnimatorButtonScaleY, objectAnimatorButtonAlpha);


        //数字
        float parameter = lastnumber / 5f;
        AnimatorSet animatorSetNumberOne = new AnimatorSet();
        int number = Math.round(parameter * 1);
        imageViewOne.setText(number + "");
        ObjectAnimator objectAnimatorNumberOne = ObjectAnimator.ofFloat(imageViewOne, "alpha", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberOneScaleX = ObjectAnimator.ofFloat(imageViewOne, "scaleX", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberOneScaleY = ObjectAnimator.ofFloat(imageViewOne, "scaleY", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        animatorSetNumberOne.playTogether(objectAnimatorNumberOne, objectAnimatorNumberOneScaleX, objectAnimatorNumberOneScaleY);

        AnimatorSet animatorSetNumberTwo = new AnimatorSet();
        int number2 = Math.round(parameter * 2);
        imageViewTwo.setText(number2 + "");
        ObjectAnimator objectAnimatorNumberTwo = ObjectAnimator.ofFloat(imageViewTwo, "alpha", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberTwoScaleX = ObjectAnimator.ofFloat(imageViewTwo, "scaleX", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberTwoScaleY = ObjectAnimator.ofFloat(imageViewTwo, "scaleY", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberOneRemove = ObjectAnimator.ofFloat(imageViewOne, "alpha", 1.0f, 0).setDuration(mDurationTime);
        animatorSetNumberTwo.playTogether(objectAnimatorNumberTwo, objectAnimatorNumberTwoScaleX, objectAnimatorNumberTwoScaleY, objectAnimatorNumberOneRemove);

        AnimatorSet animatorSetNumberThree = new AnimatorSet();
        int number3 = Math.round(parameter * 3);
        imageViewThree.setText(number3 + "");
        ObjectAnimator objectAnimatorNumberThree = ObjectAnimator.ofFloat(imageViewThree, "alpha", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberThreeScaleX = ObjectAnimator.ofFloat(imageViewThree, "scaleX", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberThreeScaleY = ObjectAnimator.ofFloat(imageViewThree, "scaleY", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberTwoRemove = ObjectAnimator.ofFloat(imageViewTwo, "alpha", 1.0f, 0).setDuration(mDurationTime);
        animatorSetNumberThree.playTogether(objectAnimatorNumberThree, objectAnimatorNumberThreeScaleX, objectAnimatorNumberThreeScaleY, objectAnimatorNumberTwoRemove);

        AnimatorSet animatorSetNumberFour = new AnimatorSet();
        int number4 = Math.round(parameter * 4);
        imageViewFour.setText(number4 + "");
        ObjectAnimator objectAnimatorNumberFour = ObjectAnimator.ofFloat(imageViewFour, "alpha", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberFourScaleX = ObjectAnimator.ofFloat(imageViewFour, "scaleX", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberFourScaleY = ObjectAnimator.ofFloat(imageViewFour, "scaleY", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberThreeRemove = ObjectAnimator.ofFloat(imageViewThree, "alpha", 1.0f, 0).setDuration(mDurationTime);
        animatorSetNumberFour.playTogether(objectAnimatorNumberFour, objectAnimatorNumberFourScaleX, objectAnimatorNumberFourScaleY, objectAnimatorNumberThreeRemove);

        AnimatorSet animatorSetNumberFive = new AnimatorSet();
        int number5 = Math.round(parameter * 5);
        imageViewFive.setText(number5 + "");
        ObjectAnimator objectAnimatorNumberFive = ObjectAnimator.ofFloat(imageViewFive, "alpha", 0, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberFiveScaleX = ObjectAnimator.ofFloat(imageViewFive, "scaleX", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberFiveScaleY = ObjectAnimator.ofFloat(imageViewFive, "scaleY", 1.0f, 1.1f, 0.9f, 1.0f).setDuration(mDurationTime);
        ObjectAnimator objectAnimatorNumberFourRemove = ObjectAnimator.ofFloat(imageViewFour, "alpha", 1.0f, 0).setDuration(mDurationTime);
        animatorSetNumberFive.playTogether(objectAnimatorNumberFive, objectAnimatorNumberFiveScaleX, objectAnimatorNumberFiveScaleY, objectAnimatorNumberFourRemove);

        //数字变化集合，依次播放
        AnimatorSet animatorSetNumberList = new AnimatorSet();
        animatorSetNumberList.setStartDelay(700);
        animatorSetNumberList.playSequentially(animatorSetNumberOne, animatorSetNumberTwo, animatorSetNumberThree, animatorSetNumberFour, animatorSetNumberFive);

        AnimatorSet animatorSetAllOfAnimator = new AnimatorSet();
        animatorSetAllOfAnimator.playTogether(animatorSetButton,
                animatorSetAllCions, objectAnimatorBackgrand, animatorSetTextRegist, animatorSetTextSuccessfully,
                animatorSetBox, animatorSetText, animatorSetNumberList, sunshine);
        mAnimator = animatorSetAllOfAnimator;
        animatorSetAllOfAnimator.start();
    }

    @Override
    public void registSucceed() {
        RegistManager.get(this).removeCallBack(this);
        submitInviteCode();
    }

    @Override
    public void registFailed() {
        RegistManager.get(this).removeCallBack(this);
        dismissLoginingDialog();
        ToastUtils.showShortToast(NewUserGuideActivity.this,
                R.string.invite_code_enter_wrong_in_regist);
    }
}
