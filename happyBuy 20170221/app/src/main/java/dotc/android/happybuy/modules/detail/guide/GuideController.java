package dotc.android.happybuy.modules.detail.guide;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.FirstBuyGuide;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoProductDetail;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/21.
 */

public class GuideController implements View.OnClickListener,PartCallBack {

    private final String TAG = GuideController.class.getSimpleName();
    private GoodsDetailActivity mActivity;
    private FrameLayout mContainer;
//    private ViewGroup mMain;

    private View mFingerView;
    private View mBackgroundMaskView;
    private View mPartView;
    private View mPressedView;
    private View mCloseView;

    private PojoProductDetail mProductDetail;
    private PartFragment mPartFragment;
    private boolean mBlockEvents;
    private boolean mShowing;
    private boolean mAllowClose;

    private Animator mAnimator;

    public GuideController(GoodsDetailActivity activity) {
        this.mActivity = activity;
        mContainer = (FrameLayout) activity.findViewById(R.id.first_open);
        FirstBuyGuide firstBuyGuide = AbConfigManager.getInstance(mActivity).getConfig().first_buy_guide;
        mAllowClose = firstBuyGuide.allow_close;
//        mMain = (ViewGroup) activity.findViewById(R.id.layout_main);
    }

    public void onDestroy() {
        if(mShowing){
            cancelAnimator();
            dismiss();
        }
    }

    private void dismiss() {
        mContainer.removeAllViews();
        setBlockMainEvent(false);
        mShowing = false;
    }

    private void setBlockMainEvent(boolean blockEvent) {
//        mMain.setEnabled(!blockEvent);
        mBlockEvents = blockEvent;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return mBlockEvents;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mBlockEvents;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HBLog.d(TAG,"onKeyDown keyCode:"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isNeedGuide()){
                if(mAllowClose){
                    PrefUtils.putBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, true);
                    if (mPartFragment!=null&&mPartFragment.onBackPressed()) {
                        return true;
                    }
//                    if(mShowing){
//                        ignoreAndQuit();
//                        return true;
//                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.background_mask:
                break;
            case R.id.textview_part:
                handleButtonClick();
                break;
            case R.id.imageview_close:
                Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Close_Guide, "detail", null);
                ignoreAndQuit();
                break;
        }
    }

    private void ignoreAndQuit(){
        PrefUtils.putBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, true);
        cancelAnimator();
        dismiss();
    }

    private void inflateAndInit() {
        HBLog.d(TAG, "inflateAndInit");
        View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_guide_detail, mContainer);
        mFingerView = v.findViewById(R.id.imageview_finger);
        mBackgroundMaskView = v.findViewById(R.id.background_mask);
        mPartView = v.findViewById(R.id.textview_part);
        mPressedView = v.findViewById(R.id.view_pressed);
        mCloseView = v.findViewById(R.id.imageview_close);

        mCloseView.setOnClickListener(this);
        mBackgroundMaskView.setOnClickListener(this);
        mPartView.setOnClickListener(this);

        if(mAllowClose){
            mCloseView.setVisibility(View.VISIBLE);
        } else {
            mCloseView.setVisibility(View.INVISIBLE);
        }
        mFingerView.setTranslationY(AppUtil.dp2px(mActivity,5));
    }

    public void showIfNeeded(PojoProductDetail goodsDetail){
        if(isNeedGuide()){
            mProductDetail = goodsDetail;
            mShowing = true;
            setBlockMainEvent(true);
            inflateAndInit();
            showWithAnim();
        }
    }

    private boolean isNeedGuide(){
        boolean flag = !PrefUtils.getBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, false)
                &&!PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true);
        return flag&&mActivity.isLogin();
    }


    private void cancelAnimator(){
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    private void showWithAnim(){
        HBLog.d(TAG,"showWithAnim");
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(backgroundMaskAnimator(500,0),fingerAppearAnimator(600,0),
                fingerViewPressedAnimator(300,0));
        animatorSet.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!AppUtil.isActivityDestroyed(mActivity)){
                    handleButtonClick();
                }
            }
        });
        mAnimator = animatorSet;
        animatorSet.start();
    }

    private void handleButtonClick(){
        setBlockMainEvent(false);
        Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Detail_Guide, null, null);
        mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_USER_TIPS, mProductDetail.product, this);
        mPartFragment.show(mActivity.getSupportFragmentManager());
        dismiss();
    }

    private Animator backgroundMaskAnimator(long duration, long delay){
        Animator animator = ObjectAnimator.ofFloat(mBackgroundMaskView,"alpha",0.f,1f).setDuration(duration);
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mBackgroundMaskView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator fingerAppearAnimator(long duration, long delay) {
        float value = -mContainer.getWidth() / 2;
        Animator animator = ObjectAnimator.ofFloat(mFingerView, "translationX", 0, value)
                .setDuration(duration);
        animator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFingerView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator fingerViewPressedAnimator(long duration, long delay){
        AnimatorSet animatorSet = new AnimatorSet();

        final float pushValue = AppUtil.dp2px(mActivity,5);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f,0.7f,1f);
        animator.setDuration(duration);//1000
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean firstFrame = true;
            float startTransY;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(firstFrame){
                    startTransY = mFingerView.getTranslationY();
                    firstFrame = false;
                }
                float animatedValue = (float) animation.getAnimatedValue();
                mFingerView.setTranslationY(startTransY+ pushValue* animatedValue);
            }
        });

        Animator pressAnimator = ObjectAnimator.ofFloat(mPressedView,"alpha",0.f,1f).setDuration(duration);
        pressAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mPressedView.setVisibility(View.VISIBLE);
            }
        });

        animatorSet.playTogether(animator,pressAnimator);
        return animatorSet;
    }


    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject){
        HBLog.d(TAG,"onFragmentDismiss paySuceess:"+paySuceess);
        if(paySuceess){
            PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true);
            ignoreAndQuit();
            setServerUserFinishNewbieGuide();
            mActivity.refreshingAndScrollToFirst();
            Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Finish_Guide, "", null);
        }
    }

    private void setServerUserFinishNewbieGuide(){
        String uid = PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, "");
        Map<String,Object> params = new HashMap<>();
        params.put("from_uid", uid);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.FINISHNEWBIEGUIDE, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone list) {
                HBLog.d(TAG + " onSuccess " + list);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
    }

}
