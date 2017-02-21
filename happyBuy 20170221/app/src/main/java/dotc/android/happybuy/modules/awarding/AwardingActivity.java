package dotc.android.happybuy.modules.awarding;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoEventStatus;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.awarding.widget.AnimationLayout;
import dotc.android.happybuy.modules.awarding.widget.BottomShadowView;
import dotc.android.happybuy.modules.awarding.widget.CircleCornerLinearLayout;
import dotc.android.happybuy.modules.awarding.widget.LightFocusLayout;
import dotc.android.happybuy.modules.awarding.widget.LoadingAnimView;
import dotc.android.happybuy.modules.awarding.widget.TimeCountView;
import dotc.android.happybuy.modules.awarding.widget.TopShadowView;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.me.MeFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.uibase.anim.Duration;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 开奖提醒对话框
 *
 */
public class AwardingActivity extends Activity implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName();
    public static final int TYPE_EXTRA_AWARDING = 0x00;
    public static final int TYPE_EXTRA_WIN = 0x01;
    public static final int SOURCE_EXTRA_PUSH = 0x00;
    public static final int SOURCE_EXTRA_NOTI= 0x01;

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_NAME = "extra_product_name";
    public static final String EXTRA_PRODUCT_PERIOD = "extra_product_period";
    public static final String EXTRA_PRODUCT_ITEM_ID = "extra_product_item_id";
    public static final String EXTRA_PRODUCT_IMAGE = "extra_product_image";
    public static final String EXTRA_SERVER_TIME = "extra_server_time";
    public static final String EXTRA_AWARD_TIME = "extra_award_time";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_SOURCE = "extra_source";

    private String mExtraProductId;
    private String mExtraProductItemId;
    private String mExtraProductImage;
    private String mExtraProductName;
    private String mExtraProductPeriod;
//    private Bitmap mExtraBitmap;
    private long mExtraServerTime;
    private long mExtraAwardTime;
    private long mExtraType;
    private long mExtraSource;//页面来源

    private AnimationLayout mBallAnimLayout;
    private CircleCornerLinearLayout mSetsLayout;
    private View mTimerCountLayout;
    private TimeCountView mTimeCountView;
    private View mAwardingLayout;
    private View mQueryLayout;
    private View mWinLayout;
    private View mNoWinLayout;

    private View mRoundView;
    private ImageView mTimerDownDiskImageView;
    private TextView mTimerDownProductNameTextView;
    private TextView mCountDownTextView;
    private TopShadowView mTopShadowView;
    private BottomShadowView mBottomShadowView;

    private LoadingAnimView mLoadingAnimView;

    private TextView mWinCongratulationTextView;
    private View mWinnerHintView;
    private TextView mWinProductNameTextView;
    private LightFocusLayout mWinLightFocusLayout;
    private ImageView mWinSunshineImageView;

    private View mNoWinTopHintView;
    private View mNoWinBottomHintView;
    private TextView mNoWinWinnerNickTextView;
    private LightFocusLayout mNoWinLightFocusLayout;
    private ImageView mNoWinSunshineImageView;

    private ImageView mProductImageView;
    private ImageView mMaskImageView;
    private ImageView mEmotionHandyImageView;
    private TextView mProductPeriodTextView;
    private CountDownTimer mCountDownTimer;
    private String timer;

    private Animator mEnterTimerDownViewAnimtor;
    private Animator mSwitchToQueryViewAnimtor;
    private Animator mSwitchToWinViewAnimtor;
    private Animator mSwitchToNoWinViewAnimtor;
    private Animator mDirectDislayWinAnimator;
    private float mTimerDownUptranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        HBLog.d(TAG + " onCreate");
        setContentView(R.layout.activity_awarding);
        readExtraFromIntent();
        initUI();
        mTimerCountLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mTimerCountLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.d("yun","-----------"+mTimerCountLayout.getWidth()+" "+mTimerCountLayout.getHeight());
                if(mExtraType == TYPE_EXTRA_AWARDING){
                    initCountDown();
                    enterTimerDownViewWithAnim();
                } else if(mExtraType == TYPE_EXTRA_WIN){
                    directDislayWinViewWithAnim();
                }
                return true;
            }
        });
        mTimerDownUptranslationY = getResources().getDimensionPixelSize(R.dimen.awarding_anim_translate_y);
        if(mExtraSource == SOURCE_EXTRA_NOTI){
            Analytics.sendUIEvent(AnalyticsEvents.Awarding.CountDown_Noti, null, null);
        }
        Analytics.sendUIEvent(AnalyticsEvents.WinDialog.Show_Win_Dialog, null, null);
    }

    private void readExtraFromIntent() {
        mExtraProductId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        mExtraProductName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        mExtraProductItemId = getIntent().getStringExtra(EXTRA_PRODUCT_ITEM_ID);
        mExtraProductPeriod = getIntent().getStringExtra(EXTRA_PRODUCT_PERIOD);
        mExtraProductImage = getIntent().getStringExtra(EXTRA_PRODUCT_IMAGE);
        mExtraServerTime = getIntent().getLongExtra(EXTRA_SERVER_TIME,0);
        mExtraAwardTime = getIntent().getLongExtra(EXTRA_AWARD_TIME,0);
        mExtraType = getIntent().getIntExtra(EXTRA_TYPE,TYPE_EXTRA_AWARDING);
        mExtraSource = getIntent().getIntExtra(EXTRA_SOURCE,0);
//        mExtraBitmap = getIntent().getParcelableExtra(EXTRA_BITMAP);
    }

    private void initUI() {
        mBallAnimLayout = (AnimationLayout) findViewById(R.id.layout_ball_anim);
        mSetsLayout = (CircleCornerLinearLayout) findViewById(R.id.layout_sets);
        mAwardingLayout = findViewById(R.id.layout_awarding_timerdown);
        mQueryLayout = findViewById(R.id.layout_awarding_query);
        mWinLayout = findViewById(R.id.layout_awarding_win);
        mNoWinLayout = findViewById(R.id.layout_awarding_nowin);

        mTopShadowView = (TopShadowView) findViewById(R.id.view_top_shadow);
        mBottomShadowView = (BottomShadowView) findViewById(R.id.view_bottom_shadow);
        mRoundView = findViewById(R.id.layout_round);
        mTimerCountLayout = findViewById(R.id.layout_timecount);
        mTimeCountView = (TimeCountView) findViewById(R.id.view_timecount);
        mProductImageView = (ImageView) findViewById(R.id.imageview_product_image);
        mProductPeriodTextView = (TextView) findViewById(R.id.textview_product_period);
        mMaskImageView = (ImageView) findViewById(R.id.imageview_image_mask);
        mEmotionHandyImageView = (ImageView) findViewById(R.id.imageview_emotion_handy);

        mWinCongratulationTextView = (TextView) findViewById(R.id.textview_congratulations);
        mWinnerHintView = findViewById(R.id.layout_winner_hint);
        mTimerDownDiskImageView = (ImageView) findViewById(R.id.imageview_disk);
        mTimerDownProductNameTextView = (TextView) findViewById(R.id.timerdown_product_name);

        mLoadingAnimView = (LoadingAnimView) findViewById(R.id.view_loading_anim);

        mWinProductNameTextView = (TextView) findViewById(R.id.textview_win_product_name);
        mWinLightFocusLayout = (LightFocusLayout) findViewById(R.id.layout_win_light_focus);
        mWinSunshineImageView = (ImageView) findViewById(R.id.imageview_win_sunshine);

        mNoWinTopHintView = findViewById(R.id.layout_nowin_top_hint);
        mNoWinLightFocusLayout = (LightFocusLayout) findViewById(R.id.layout_nowin_light_focus);
        mNoWinBottomHintView = findViewById(R.id.layout_nowin_bottom_hint);
        mNoWinWinnerNickTextView = (TextView) findViewById(R.id.textview_nowin_nickname);
        mNoWinSunshineImageView = (ImageView) findViewById(R.id.imageview_nowin_sunshine);

        mTimerDownProductNameTextView.setText(mExtraProductName);
        mWinProductNameTextView.setText(mExtraProductName);
        mProductPeriodTextView.setText(mExtraProductPeriod);

        Glide.with(this).load(mExtraProductImage).dontAnimate().into(mProductImageView);
        Glide.with(this).load(mExtraProductImage).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mProductImageView.setImageBitmap(resource);
                drawMaskBitmap(resource);
                calcBitmapFitOffset(resource);
            }
        });

        mWinLightFocusLayout.setOnClickListener(this);
        mNoWinLightFocusLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_win_light_focus:
                Analytics.sendUIEvent(AnalyticsEvents.WinDialog.Click_Win_Yes, null, null);
//                mWinLightFocusLayout.getHighLightAnim(Duration.ofTime(52),0,mWinSunshineImageView).start();
                Intent intent = new Intent(AwardingActivity.this,MainTabActivity.class);
                intent.putExtra(MainTabActivity.EXTRA_TAB_INDEX,MainTabActivity.TAB_INDEX_3);
                Bundle args = new Bundle();
                args.putInt(MeFragment.EXTRA_INDEX,MeFragment.TAB_INDEX_1);
                intent.putExtra(MainTabActivity.EXTRA_TAB_ARGS,args);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.layout_nowin_light_focus:
//                mNoWinLightFocusLayout.getHighLightAnim(Duration.ofTime(52),0,mNoWinSunshineImageView).start();
                Intent goHomeIntent = new Intent(AwardingActivity.this,MainTabActivity.class);
                goHomeIntent.putExtra(MainTabActivity.EXTRA_TAB_INDEX,MainTabActivity.TAB_INDEX_3);
                Bundle noargs = new Bundle();
                noargs.putInt(MeFragment.EXTRA_INDEX,MeFragment.TAB_INDEX_0);
                goHomeIntent.putExtra(MainTabActivity.EXTRA_TAB_ARGS,noargs);
                goHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goHomeIntent);
                finish();
                break;
        }
    }

    private void initCountDown(){
        long diffTime = 0L;
        long time = 0L;
        TimerDownManager.TimerObj timerObj = TimerDownManager.getInstance(this).getTimer(mExtraProductItemId);
        if(timerObj!=null){
            if(timerObj.isValid()){
                diffTime = timerObj.diffTime;
                time = timerObj.awardTime*1000 - (System.currentTimeMillis()-diffTime);
            }
        } else {
            diffTime = System.currentTimeMillis() - mExtraServerTime*1000;
            time = mExtraAwardTime*1000 - (System.currentTimeMillis()-diffTime);
        }
        HBLog.d(TAG+" initCountDown time:"+time);
//        time = 5*1000;

        if(time < 3*1000*Duration.FACTOR){
            time = 3*1000*Duration.FACTOR;
        }
        startCountDownAnim(time);
    }

    private void startCountDownAnim(long time ){
        mCountDownTimer = new CountDownTimer(time,10) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeCountView.setTimerDown(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                HBLog.d(TAG+" onFinish ");
                mTimeCountView.setTimerDown(0);
                switchToQueryViewWithAnim();
            }
        };
        mCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
        if(mEnterTimerDownViewAnimtor !=null&& mEnterTimerDownViewAnimtor.isStarted()){
            mEnterTimerDownViewAnimtor.cancel();
        }
        if(mSwitchToQueryViewAnimtor !=null&& mSwitchToQueryViewAnimtor.isStarted()){
            mSwitchToQueryViewAnimtor.cancel();
        }
        if(mSwitchToWinViewAnimtor !=null&& mSwitchToWinViewAnimtor.isStarted()){
            mSwitchToWinViewAnimtor.cancel();
        }
        if(mSwitchToNoWinViewAnimtor !=null&& mSwitchToNoWinViewAnimtor.isStarted()){
            mSwitchToNoWinViewAnimtor.cancel();
        }
        if(mDirectDislayWinAnimator !=null&& mDirectDislayWinAnimator.isStarted()){
            mDirectDislayWinAnimator.cancel();
        }
    }

    private void queryAwardResult(){
        String url = HttpProtocol.URLS.EVENT_STATUS;
        Map<String,Object> params = new HashMap<>();
        params.put("productItemId",mExtraProductItemId);
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoEventStatus>() {
            @Override
            public void onSuccess(PojoEventStatus eventStatus) {
                HBLog.d(TAG + " queryAwardResult onSuccess " + eventStatus);
                if(eventStatus.status ==HttpProtocol.PRODUCT_STATE.AWARD&&eventStatus.prizeUser!=null){
                    if(eventStatus.prizeUser.userId.equals(PrefUtils.getString(PrefConstants.UserInfo.UID, ""))){
                        switchToWinViewWithAnim();
                    } else {
                        mNoWinWinnerNickTextView.setText(eventStatus.prizeUser.nickname);
                        switchToNoWinViewWithAnim();
                    }
                    /*if(System.currentTimeMillis()%2 == 0){
                        switchToWinViewWithAnim();
                    } else {
                        mNoWinWinnerNickTextView.setText(eventStatus.prizeUser.nickname);
                        switchToNoWinViewWithAnim();
                    }*/
                } else {
                    ToastUtils.showLongToast(GlobalContext.get(),R.string.net_request_error);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " queryAwardResult onFailed " + code + " " + message + " " + e);
                ToastUtils.showLongToast(GlobalContext.get(),R.string.net_request_error);
//                switchToWinViewWithAnim();
            }

            @Override
            public Class<PojoEventStatus> getObjectClass() {
                return PojoEventStatus.class;
            }
        });
    }

    private void drawMaskBitmap(Bitmap bitmap){
        Bitmap maskBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(maskBitmap);
        canvas.drawBitmap(bitmap,0,0,null);
        canvas.drawColor(0x4c000000, PorterDuff.Mode.SRC_IN);
        mMaskImageView.setImageBitmap(maskBitmap);
    }

    private float mPhotoInnerOffset;

    private void calcBitmapFitOffset(Bitmap bitmap){
        int width = getResources().getDimensionPixelOffset(R.dimen.awarding_image_width);
        int height = getResources().getDimensionPixelOffset(R.dimen.awarding_image_height);
        float scale = Math.min((float) width / (float) bitmap.getWidth(),
                (float) height / (float) bitmap.getHeight());
//        mPhotoInnerOffset = (mProductImageView.getHeight() - bitmap.getHeight()*scale)/2 + offset*scale;
        float gapPxOffset = getGapPxOffset(bitmap);
        mPhotoInnerOffset = (height - bitmap.getHeight()*scale)/2 + gapPxOffset*scale;
        HBLog.d(TAG+" --------- calc image width:"+width+" height:"+height+" bitmap w:"+bitmap.getWidth()+" h:"+bitmap.getHeight()+" scale:"+scale);
        HBLog.d(TAG+" --------- calc mPhotoInnerOffset:"+mPhotoInnerOffset+" gapPxOffset:"+gapPxOffset);
    }

    private float getGapPxOffset(Bitmap bitmap){
        int[] srcBuffer = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(srcBuffer,
                0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int lastTransparentRow = bitmap.getHeight();
        for (int i = srcBuffer.length-1; i >= 0; i--) {
            final int alpha = srcBuffer[i] >>> 24;
            if (alpha > 188) {
                int row = i / bitmap.getWidth();
                lastTransparentRow = row;
                break;
            }
        }
        return bitmap.getHeight() - lastTransparentRow;
    }

    private void directDislayWinViewWithAnim(){
        mTimerCountLayout.setVisibility(View.GONE);
        mAwardingLayout.setVisibility(View.GONE);
        mProductImageView.setVisibility(View.INVISIBLE);

        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet aSet = new AnimatorSet();
        aSet.playTogether(backgroundStretchAnimator(Duration.ofTime(10),0),roundScaleAnimator(Duration.ofTime(10),0));

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(winCongratulationScaleAnimator(Duration.ofTime(12),0),
                winnerHintAnimator(Duration.ofTime(17),Duration.ofTime(7)),
                winProductImageScaleAnimator(Duration.ofTime(15),Duration.ofTime(12)),
                winProductNameAppearAnimator(Duration.ofTime(8),Duration.ofTime(20)),
                mBallAnimLayout.getCoinAnimator(Duration.ofTime(20),Duration.ofTime(12))
        );
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mWinLayout.setVisibility(View.VISIBLE);
                mSetsLayout.setShadowVisiable(false);
            }
        });
        animatorSet.playSequentially(aSet,animator,
                winLightFoucsAnimator(Duration.ofTime(12),0),
                mWinLightFocusLayout.getHighLightAnim(Duration.ofTime(52),0,mWinSunshineImageView)
        );
        mDirectDislayWinAnimator = animatorSet;
        animatorSet.start();
    }

    private void enterTimerDownViewWithAnim(){
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet aSet = new AnimatorSet();
        aSet.playTogether(backgroundStretchAnimator(Duration.ofTime(10),0),roundScaleAnimator(Duration.ofTime(10),0));

        AnimatorSet viewVisiabeAnimatorSet = new AnimatorSet();
        viewVisiabeAnimatorSet.playTogether(
                shadowVisableAnimator(Duration.ofTime(10),0),
                productImageScaleAnimator(Duration.ofTime(16),0),
                productNameScaleAnimator(Duration.ofTime(10),Duration.ofTime(6)),
                mBallAnimLayout.getExplodeAnimator(Duration.ofTime(13),Duration.ofTime(3)));

        AnimatorSet looperAnimator = new AnimatorSet();
        looperAnimator.playTogether(mBallAnimLayout.getShakeAnimator(Duration.ofTime(100)),
                productImageShakeAnimator(Duration.ofTime(100)));
        animatorSet.playSequentially(timedownScaleAnimator(Duration.ofTime(16)),
                stayAnimator(Duration.ofTime(8)),
                timerdownUpMoveAnimator(Duration.ofTime(20)),
                aSet,viewVisiabeAnimatorSet,looperAnimator);
        mEnterTimerDownViewAnimtor = animatorSet;
        animatorSet.start();
    }

    private void switchToQueryViewWithAnim(){
        mEnterTimerDownViewAnimtor.cancel();
        AnimatorSet firstAnimators = new AnimatorSet();
        firstAnimators.playTogether(
                productNameDownDisappearAnimator(Duration.ofTime(12),0),
                mBallAnimLayout.getTwoDropAnimator(Duration.ofTime(12),Duration.ofTime(5)),
                productImageDownDisappearAnimator(Duration.ofTime(12),Duration.ofTime(5)),
                timerdownDownDisappearAnimator(Duration.ofTime(12),Duration.ofTime(5))
                );

        Animator secondAnimators = queryViewAppearAnimator(Duration.ofTime(10),0);
        secondAnimators.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                queryAwardResult();
            }
        });

        AnimatorSet looperAnimator = new AnimatorSet();
        looperAnimator.playTogether(mLoadingAnimView.getLoadingAnimator(Duration.value(2)),mBallAnimLayout.getShakeAnimator(Duration.value(4)));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(firstAnimators,secondAnimators,looperAnimator);
        mSwitchToQueryViewAnimtor = animatorSet;
        animatorSet.start();
    }

    private void switchToWinViewWithAnim(){
        mSwitchToQueryViewAnimtor.cancel();
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet firstAnimator = new AnimatorSet();
        firstAnimator.playTogether(shadowInvisableAnimator(Duration.ofTime(10),0),
                queryViewDisAppearAnimator(Duration.ofTime(13),0),
                mBallAnimLayout.getDropDisappearAnimator(Duration.ofTime(11),Duration.ofTime(2)));

        AnimatorSet secondAnimator = new AnimatorSet();
        secondAnimator.playTogether(winCongratulationScaleAnimator(Duration.ofTime(12),0),
                winnerHintAnimator(Duration.ofTime(17),Duration.ofTime(7)),
                winProductImageScaleAnimator(Duration.ofTime(15),Duration.ofTime(12)),
                winProductNameAppearAnimator(Duration.ofTime(8),Duration.ofTime(20)),
                mBallAnimLayout.getCoinAnimator(Duration.ofTime(20),Duration.ofTime(12))
                );
        secondAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mWinLayout.setVisibility(View.VISIBLE);
                mQueryLayout.setVisibility(View.INVISIBLE);
                mSetsLayout.setShadowVisiable(false);
            }
        });
        animatorSet.playSequentially(firstAnimator,secondAnimator,
                winLightFoucsAnimator(Duration.ofTime(12),0),
                mWinLightFocusLayout.getHighLightAnim(Duration.ofTime(52),0,mWinSunshineImageView)
                );
        mSwitchToWinViewAnimtor = animatorSet;
        animatorSet.start();
    }

    private void switchToNoWinViewWithAnim(){
        mSwitchToQueryViewAnimtor.cancel();
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet firstAnimator = new AnimatorSet();
        firstAnimator.playTogether(shadowInvisableAnimator(Duration.ofTime(10),0),
                queryViewDisAppearAnimator(Duration.ofTime(13),0),
                mBallAnimLayout.getDropDisappearAnimator(Duration.ofTime(10),Duration.ofTime(3)));

        AnimatorSet secondAnimator = new AnimatorSet();
        secondAnimator.playTogether(nowinnerTopHintAnimator(Duration.ofTime(11),0),
                nowinnerBottomHintAnimator(Duration.ofTime(10),Duration.ofTime(5)),
                noWinProductImageAnimator(Duration.ofTime(10),Duration.ofTime(5))
                );
        secondAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mNoWinLayout.setVisibility(View.VISIBLE);
                mQueryLayout.setVisibility(View.INVISIBLE);
                mSetsLayout.setShadowVisiable(false);
            }
        });

        animatorSet.playSequentially(
                firstAnimator,secondAnimator,stayAnimator(Duration.ofTime(6)),
                emotionHandyAnimator(Duration.ofTime(18),0),
                nowinLightFoucsAnimator(Duration.ofTime(16),0),
                mNoWinLightFocusLayout.getHighLightAnim(Duration.ofTime(52),0,mNoWinSunshineImageView)
                );

        mSwitchToNoWinViewAnimtor = animatorSet;
        animatorSet.start();
    }

//    private void switchToNoWinViewWithAnim(){
//        mSwitchToQueryViewAnimtor.cancel();
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        AnimatorSet disappearAnimatorSet = new AnimatorSet();
//        disappearAnimatorSet.playTogether(mBallAnimLayout.getDropDisappearAnimator(Duration.value(.5f),0)
//                ,queryViewDisAppearAnimator(Duration.value(.5f),0)
//                ,nowinnerTopHintAnimator(Duration.value(.2f),Duration.value(.3f))
//                ,nowinnerBottomHintAnimator(Duration.value(.2f),Duration.value(.5f))
//                ,winProductImageScaleAnimator(Duration.value(.6f),Duration.value(.5f)));
//
//        animatorSet.playSequentially(disappearAnimatorSet,emotionHandyAnimator(Duration.value(.6f),0)
//                ,nowinLightFoucsAnimator(Duration.value(.6f),0)
//                ,mNoWinLightFocusLayout.getHighLightAnim(Duration.value(2f),0,mNoWinSunshineImageView));
//        mSwitchToNoWinViewAnimtor = animatorSet;
//        animatorSet.start();
//    }

    private Animator timedownScaleAnimator(long duration){
        ValueAnimator animator = ValueAnimator.ofFloat(0.9f,1.2f,0.9f,1f);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTimerCountLayout.setScaleX(value);
                mTimerCountLayout.setScaleY(value);

            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mTimerCountLayout.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator stayAnimator(long duration){
        ValueAnimator animator = ValueAnimator.ofFloat(0,1f);
        animator.setDuration(duration);
        return animator;
    }

    private Animator timerdownUpMoveAnimator(long duration){
        Animator animator = ObjectAnimator.ofFloat(mTimerCountLayout, "translationY", -mTimerDownUptranslationY)
                .setDuration(duration);
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTimerDownView.getLayoutParams();
//                layoutParams.topMargin = 0;
            }
        });
        return animator;
    }

    private Animator backgroundStretchAnimator(long duration,long delay){
        final int height = mSetsLayout.getHeight();
        HBLog.d(TAG+" backgroundStretchAnimator height:"+height);
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1);
        animator.setDuration(duration);//1000
        animator.setStartDelay(delay);
//        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mSetsLayout.setScaleY(value);
                mSetsLayout.setTranslationY(height*(value - 1)/2);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mSetsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSetsLayout.setShadowVisiable(true);
            }
        });
        return animator;
    }

    private Animator shadowVisableAnimator(long duration,long delay){
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0,1f);
        scaleAnimator.setStartDelay(delay);
        scaleAnimator.setDuration(duration);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTopShadowView.setAlpha(value);
                mBottomShadowView.setAlpha(value);
            }
        });

        scaleAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mTopShadowView.setVisibility(View.VISIBLE);
                mBottomShadowView.setVisibility(View.VISIBLE);
            }
        });
        return scaleAnimator;
    }

    private Animator shadowInvisableAnimator(long duration,long delay){
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f,0);
        scaleAnimator.setStartDelay(delay);
        scaleAnimator.setDuration(duration);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTopShadowView.setAlpha(value);
                mBottomShadowView.setAlpha(value);
            }
        });

        scaleAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                mTopShadowView.setVisibility(View.INVISIBLE);
                mBottomShadowView.setVisibility(View.INVISIBLE);
            }
        });
        return scaleAnimator;
    }

    private Animator roundScaleAnimator(long duration,long delay){
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0.9f,1.2f,0.9f,1f);
        scaleAnimator.setStartDelay(delay);
        scaleAnimator.setDuration(duration);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mRoundView.setScaleX(value);
                mRoundView.setScaleY(value);
            }
        });

        scaleAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mRoundView.setVisibility(View.VISIBLE);
            }
        });
        return scaleAnimator;
    }

    private Animator noWinProductImageAnimator(long duration, long delay){
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.f,1f);
        alphaAnimator.setStartDelay(delay);
        alphaAnimator.setDuration(duration);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mProductImageView.setAlpha(value);
                mProductImageView.setTranslationY(0);
            }
        });
        alphaAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mProductImageView.setVisibility(View.VISIBLE);
            }
        });
        return alphaAnimator;
    }

    private Animator productImageScaleAnimator(long duration,long delay){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(delay);
        animatorSet.setDuration(duration);
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0.9f,1.2f,0.9f,1f);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mProductImageView.setScaleX(value);
                mProductImageView.setScaleY(value);
            }
        });
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.f,1f);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mProductImageView.setAlpha(value);
                mTimerDownDiskImageView.setAlpha(value);
            }
        });
        animatorSet.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mProductImageView.setVisibility(View.VISIBLE);
                mTimerDownDiskImageView.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.playTogether(alphaAnimator,scaleAnimator);
        return animatorSet;
    }

    private Animator productNameScaleAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1.5f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTimerDownProductNameTextView.setScaleY(value);
                mTimerDownProductNameTextView.setScaleY(value);
                mTimerDownProductNameTextView.setAlpha(Math.min(1,value));
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mTimerDownProductNameTextView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }
    private float mImageShakeHeight;
    private Animator productImageShakeAnimator(long duration){
        ValueAnimator animator = ValueAnimator.ofFloat(0.f,1f,0f);
        animator.setDuration(duration);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setInterpolator(new SpringInterpolator());
//        final float height = mPhotoInnerOffset+AppUtil.dp2px(this,16);//mProductImageView.getHeight()*0.1f;
//        HBLog.d(TAG+" productImageShakeAnimator height:"+height);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mProductImageView.setTranslationY(mImageShakeHeight*value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mImageShakeHeight = mPhotoInnerOffset+AppUtil.dp2px(GlobalContext.get(),16);
                HBLog.d(TAG+" onAnimationStart mImageShakeHeight:"+mImageShakeHeight);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mImageShakeHeight = mPhotoInnerOffset+AppUtil.dp2px(GlobalContext.get(),16);
                HBLog.d(TAG+" onAnimationRepeat mImageShakeHeight:"+mImageShakeHeight);
            }
        });
        return animator;
    }

    private Animator productImageDownDisappearAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
//        animator.setInterpolator(new AccelerateInterpolator());
        final int translate = mSetsLayout.getHeight() - mTimerDownDiskImageView.getBottom();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mProductImageView.setTranslationY(translate*value);
                mTimerDownDiskImageView.setTranslationY(translate*value);
                mProductImageView.setAlpha(1 - value);
                mTimerDownDiskImageView.setAlpha(1 - value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                mProductImageView.setVisibility(View.INVISIBLE);
            }
        });
        return animator;
    }

    private Animator productNameDownDisappearAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
//        animator.setInterpolator(new AccelerateInterpolator());
        final int translate = mSetsLayout.getHeight() - mTimerDownProductNameTextView.getTop();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTimerDownProductNameTextView.setTranslationY(translate*value);
                mTimerDownProductNameTextView.setAlpha(1 - value);
            }
        });
        return animator;
    }

    private Animator winProductNameAppearAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mWinProductNameTextView.setAlpha(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mWinProductNameTextView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator timerdownDownDisappearAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTimerCountLayout.setTranslationY(-mTimerDownUptranslationY*(1-value));
                mTimerCountLayout.setAlpha(1 - value);
            }
        });
        return animator;
    }

    private Animator queryViewAppearAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
//        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mQueryLayout.setTranslationY((value-1)*mTimerDownUptranslationY);
                mQueryLayout.setAlpha(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mQueryLayout.setVisibility(View.VISIBLE);
                mAwardingLayout.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
//                mLoadingAnimView.getLoadingAnimator()
            }
        });
        return animator;
    }

    private Animator queryViewDisAppearAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
//        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mQueryLayout.setTranslationY(value*mTimerDownUptranslationY);
                mQueryLayout.setAlpha(1-value);
            }
        });
//        animator.addListener(new WrapAnimatorListener(){
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mQueryLayout.setVisibility(View.GONE);
//            }
//        });
        return animator;
    }

    private Animator winCongratulationScaleAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(3.0f,0.8f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mWinCongratulationTextView.setScaleX(value);
                mWinCongratulationTextView.setScaleY(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mWinCongratulationTextView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator winnerHintAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mWinnerHintView.setAlpha(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mWinnerHintView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator winProductImageScaleAnimator(long duration,long deley){
        AnimatorSet animatorSet = new AnimatorSet();
        Animator scaleXAnimator = ObjectAnimator.ofFloat(mProductImageView,"scaleX",0.9f,1.2f,0.9f,1f);
        Animator scaleYAnimator = ObjectAnimator.ofFloat(mProductImageView,"scaleY",0.9f,1.2f,0.9f,1f);
        Animator alphaAnimator = ObjectAnimator.ofFloat(mProductImageView,"alpha",0.f,1f);
        alphaAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mProductImageView.setTranslationY(0);
                mProductImageView.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.setStartDelay(deley);
        animatorSet.setDuration(duration);
        animatorSet.playTogether(scaleXAnimator,scaleYAnimator,alphaAnimator);
        return animatorSet;
    }

    private Animator winLightFoucsAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0.8f,1f,1.2f,0.9f,1.1f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mWinLightFocusLayout.setAlpha(Math.min(1,value));
                mWinLightFocusLayout.setScaleX(value);
                mWinLightFocusLayout.setScaleY(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mWinLightFocusLayout.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator nowinnerTopHintAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        final float translateValue = mWinnerHintView.getHeight()/3;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mNoWinTopHintView.setTranslationY(value*translateValue);
                mNoWinTopHintView.setAlpha(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mNoWinTopHintView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }


    private Animator nowinnerBottomHintAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mNoWinBottomHintView.setAlpha(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mNoWinBottomHintView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator emotionHandyAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        final float height = mEmotionHandyImageView.getHeight();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mMaskImageView.setAlpha(value);
                mEmotionHandyImageView.setScaleX(value);
                mEmotionHandyImageView.setScaleY(value);
                mEmotionHandyImageView.setTranslationY(height*value/2);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mEmotionHandyImageView.setVisibility(View.VISIBLE);
                mMaskImageView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator nowinLightFoucsAnimator(long duration,long delay){
        ValueAnimator animator = ValueAnimator.ofFloat(0.8f,1f,1.2f,0.9f,1.1f,1f);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mNoWinLightFocusLayout.setAlpha(Math.min(1,value));
                mNoWinLightFocusLayout.setScaleX(value);
                mNoWinLightFocusLayout.setScaleY(value);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mNoWinLightFocusLayout.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }


//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.button_view:
//                Intent intent = new Intent(AwardingActivity.this,UserCenterActivity.class);
//                intent.putExtra(UserCenterActivity.EXTRA_TAB,UserCenterActivity.TAB_INDEX_1);
//                startActivity(intent);
//                finish();
//                break;
//        }
//    }
//
}
