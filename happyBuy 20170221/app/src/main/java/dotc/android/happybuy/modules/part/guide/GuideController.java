package dotc.android.happybuy.modules.part.guide;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.FirstBuyGuide;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/28.
 */

public class GuideController {

    private final String TAG = this.getClass().getSimpleName();

    private PartFragment mFragment;

    private FrameLayout mContainer;
    private View mFingerView;
    private View mPressedView;
    private View mCloseView;

    private Animator mAnimator;
    private boolean mBlockEvents;
    private boolean mShowing;
    private boolean mAllowClose;

    public GuideController(PartFragment fragment) {
        this.mFragment = fragment;
        mContainer = (FrameLayout) mFragment.getView().findViewById(R.id.layout_guide);

        mContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                showIfNeeded();
                return true;
            }
        });
        FirstBuyGuide firstBuyGuide = AbConfigManager.getInstance(mFragment.getContext()).getConfig().first_buy_guide;
        mAllowClose = firstBuyGuide.allow_close;
    }

    public void onDestroy() {
        if(mShowing){
            cancelAnimator();
            dismiss();
        }
    }

    private void setBlockMainEvent(boolean blockEvent) {
//        mMain.setEnabled(!blockEvent);
        mBlockEvents = blockEvent;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mBlockEvents;
    }

    public boolean handleBackPressed(){
//        if(isNeedGuide()){
//            if(mAllowClose){
//                ignoreAndQuit();
//                mFragment.onBackPressed();
//                return true;
//            } else {
//                return true;
//            }
//        }
        return false;
    }

    private void ignoreAndQuit(){
        PrefUtils.putBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, true);
        cancelAnimator();
        dismiss();
    }

    private void inflateAndInit() {
        HBLog.d(TAG, "inflateAndInit");
        View v = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.layout_guide_part, mContainer);
        mFingerView = v.findViewById(R.id.imageview_finger);
        mPressedView = v.findViewById(R.id.view_pressed);
        mCloseView = v.findViewById(R.id.imageview_close);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreAndQuit();
//                mFragment.onBackPressed();
            }
        });
        if(mAllowClose){
            Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Close_Guide, "part", null);
            mCloseView.setVisibility(View.VISIBLE);
        } else {
            mCloseView.setVisibility(View.INVISIBLE);
        }
    }

    public void showIfNeeded() {
        if (isNeedShowGuide() && mFragment.isLogin()) {
            mShowing = true;
            setBlockMainEvent(true);
            inflateAndInit();
            showWithAnim();
        }
    }

    private void dismiss() {
        mContainer.removeAllViews();
        setBlockMainEvent(false);
        mShowing = false;
    }

    public boolean isNeedShowGuide(){
        return !PrefUtils.getBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, false)
                &&!PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true);
    }

    private void cancelAnimator(){
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    private void showWithAnim() {
        HBLog.d(TAG, "showWithAnim");
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(fingerAppearAnimator(300, 0),fingerPushAnimator(600,0),
                fingerDisAppearAnimator(300,0));
        animatorSet.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFingerView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mFingerView.setVisibility(View.INVISIBLE);
                dismiss();
            }
        });
        mAnimator = animatorSet;
        animatorSet.start();
    }

    private Animator fingerAppearAnimator(long duration, long delay) {
        float value = -mContainer.getWidth() * 1.0f / 4;
        Animator animator = ObjectAnimator.ofFloat(mFingerView, "translationX", 0, value)
                .setDuration(duration);
        animator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                mFingerView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator fingerPushAnimator(long duration,long delay) {
        AnimatorSet animatorSet = new AnimatorSet();
        final float pushValue = AppUtil.dp2px(mFragment.getContext(), 3);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f, 0f);
        animator.setDuration(duration/2);//1000
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean firstFrame = true;
            float startTransY;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (firstFrame) {
                    startTransY = mFingerView.getTranslationY();
                    firstFrame = false;
                }
                float animatedValue = (float) animation.getAnimatedValue();
                mFingerView.setTranslationY(startTransY + pushValue * animatedValue);
//
            }
        });

        Animator pressAnimator = ObjectAnimator.ofFloat(mPressedView,"alpha",0.f,1f,0f).setDuration(duration);
        pressAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mPressedView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPressedView.setVisibility(View.INVISIBLE);
            }
        });
        animatorSet.playTogether(animator,pressAnimator);

        return animatorSet;
    }

    private Animator fingerDisAppearAnimator(long duration, long delay) {
        float value = -mContainer.getWidth() * 1.0f / 4;
        Animator animator = ObjectAnimator.ofFloat(mFingerView, "translationX", value, 0)
                .setDuration(duration);
        animator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        return animator;
    }

}