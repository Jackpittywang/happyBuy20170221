package dotc.android.happybuy.modules.main.guide;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.FirstBuyGuide;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.widget.ColorProgressBar;
import dotc.android.happybuy.uibase.widget.WaveView;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by wangjun on 16/12/20.
 */

public class GuideController implements View.OnClickListener {
    private final String TAG = GuideController.class.getSimpleName();
    private MainTabActivity mActivity;
    private FrameLayout mContainer;
    private ViewGroup mMain;

    private RefreshLayout mRefreshLayout;
    private ListView mListView;
    private View mFingerView;
    private View mBackgroundMaskView;
    private View mBackgroundDimMaskView;
    private View mProductRowLayout;
    private View mProductItemLayout;
    //    private View mPressedView;
    private View mTipsView;
    private WaveView mWaveView;
    private View mCloseView;

    private PojoProduct mTargetProduct;
//    private int[] mTargetLocation = new int[2];
//    private int mTopAndTabHeight;

    private int mTargetPosition;
    private boolean mBlockEvents;
    private boolean mShowing;
    //    private float mFingerTranslateY;
    private boolean mAllowClose;

    private Animator mAnimator;

    public GuideController(MainTabActivity activity) {
        this.mActivity = activity;
        mContainer = (FrameLayout) activity.findViewById(R.id.first_open);
        mMain = (ViewGroup) activity.findViewById(R.id.layout_main);

        FirstBuyGuide firstBuyGuide = AbConfigManager.getInstance(mActivity).getConfig().first_buy_guide;
        mAllowClose = firstBuyGuide.allow_close;
    }

    public void onDestroy() {
        if (mShowing) {
            cancelAnimator();
            dismiss();
        }
    }

    public void showIfNeeded(RefreshLayout refreshLayout, ListView listView, List<PojoProduct> products) {
        mRefreshLayout = refreshLayout;
        mListView = listView;
        mTargetPosition = 0;
        if (isNeedGuide()) {
            setBlockMainEvent(true);
//            final View view = listView.getChildAt(mTargetPosition);
//            view.getLocationOnScreen(mTargetLocation);
//            mTopAndTabHeight = refreshLayout.getTopAndTabHeight();
            mShowing = true;
            mTargetProduct = products.get(mTargetPosition);
            inflateAndInit(mTargetProduct);
            Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Start_Guide, "", null);
        }
    }

    private void inflateAndInit(PojoProduct product) {
        HBLog.d(TAG, "inflateAndInit");
        View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_guide_main, mContainer);
        mFingerView = v.findViewById(R.id.imageview_finger);
        mBackgroundMaskView = v.findViewById(R.id.background_mask);
        mBackgroundDimMaskView = v.findViewById(R.id.background_dim_mask);
        mWaveView = (WaveView) v.findViewById(R.id.wave_view);
        mProductRowLayout = v.findViewById(R.id.layout_row);
        mProductItemLayout = v.findViewById(R.id.layout_item);
//        mPressedView = v.findViewById(R.id.view_press);
        mTipsView = v.findViewById(R.id.textview_tip);
        mCloseView = v.findViewById(R.id.imageview_close);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Close_Guide, "main", null);
                ignoreAndQuit();
            }
        });
        if (mAllowClose) {
            mCloseView.setVisibility(View.VISIBLE);
        } else {
            mCloseView.setVisibility(View.INVISIBLE);
        }
        HBLog.d(TAG, "show " + mProductRowLayout.getHeight());
        setItemData(v, product);
        mProductRowLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mProductRowLayout.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        int height = mProductRowLayout.getHeight();
                        showWithAnim(height);
                    }
                });
        mBackgroundMaskView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect outRect = new Rect();
                mProductItemLayout.getGlobalVisibleRect(outRect);
                if (!outRect.contains(x, y)) {
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Home_Gray, "main", null);
                }
                return false;
            }
        });
//        mBackgroundMaskView.setOnClickListener(this);
        mProductItemLayout.setOnClickListener(this);
    }

    private void dismiss() {
        mShowing = false;
        mContainer.removeAllViews();
        setBlockMainEvent(false);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return mBlockEvents;
    }

    private void setBlockMainEvent(boolean blockEvent) {
//        mMain.setEnabled(!blockEvent);
        mBlockEvents = blockEvent;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mBlockEvents;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isNeedGuide()) {
                if (mAllowClose) {
                    ignoreAndQuit();
                }
                return true;
            }
        }
        return false;
    }

    private void ignoreAndQuit() {
        PrefUtils.putBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, true);
        cancelAnimator();
        dismiss();
    }

    private void setItemData(View v, PojoProduct product) {
        ImageView iconImageView = (ImageView) v.findViewById(R.id.imageview_icon);
        ImageView tenFlagImageView = (ImageView) v.findViewById(R.id.imageview_ten_flag);
        TextView nameTextView = (TextView) v.findViewById(R.id.textview_name);
        ColorProgressBar progressBar = (ColorProgressBar) v.findViewById(R.id.progressbar);
        TextView totalTimesTextView = (TextView) v.findViewById(R.id.textview_total_times);
        TextView remainTimesTextView = (TextView) v.findViewById(R.id.textview_retain_times);

        nameTextView.setText(product.productName);
        progressBar.setProgress(product.totalTimes - product.remainTimes, product.totalTimes);
        totalTimesTextView.setText(mActivity.getString(R.string.times_total) + " " + product.totalTimes + "");
        remainTimesTextView.setText(product.remainTimes + "");
        if (product.coins_unit == 10) {
            String country = AppUtil.getMetaData(mActivity, "country");
            if (country.equals("th")) {
                tenFlagImageView.setImageResource(R.drawable.ic_ten_flag);
            } else if (country.equals("vn")) {
                tenFlagImageView.setImageResource(R.drawable.ic_ten_flagyunan);
            }
            tenFlagImageView.setVisibility(View.VISIBLE);
        } else if(product.coins_unit== 100){
            String country= AppUtil.getMetaData(mActivity,"country");
            if(country.equals("th")){
                tenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }else if(country.equals("vn")){
                tenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }
            tenFlagImageView.setVisibility(View.VISIBLE);
        }
        else {
            tenFlagImageView.setVisibility(View.INVISIBLE);
        }
        Glide.with(mActivity).load(product.productUrl).into(iconImageView);
    }

    @Override
    public void onClick(View v) {
        HBLog.d(TAG, "onClick--");
        switch (v.getId()) {
            case R.id.background_mask:
                break;
            case R.id.layout_item:
                Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Home_Guide, null, null);
                Intent intent = new Intent(mActivity, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, mTargetProduct.productId);
                intent.putExtra(GoodsDetailActivity.USER_GUIDE, true);
                mActivity.startActivity(intent);
//                mActivity.finish();
                dismiss();
                break;
        }

    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    private void showWithAnim(int height) {
        initializeFinger(height);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(
                emptyAnimator(300, 0),
                fingerAppearAnimator(300, 0),
                fingerSmoothAnimator(700, 0),
                backgroundMaskAnimator(500, 0), fingerOverAnimator(500, 0),
                fingerPushAnimator());
        animatorSet.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBlockMainEvent(false);
            }
        });
        mAnimator = animatorSet;
        animatorSet.start();
    }

    private boolean isNeedGuide() {
        boolean flag = !PrefUtils.getBoolean(PrefConstants.Guide.IGNORE_NEWBIEGUIDE, false)
                && !PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true);
        return flag && mActivity.isLogin();
    }

    private void initializeFinger(int height) {
        RelativeLayout.LayoutParams dimLp = (RelativeLayout.LayoutParams) mBackgroundDimMaskView.getLayoutParams();
        dimLp.width = mProductRowLayout.getMeasuredWidth() / 2 + AppUtil.dp2px(mActivity, 80);
        mBackgroundDimMaskView.setLayoutParams(dimLp);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mWaveView.getLayoutParams();
        lp.leftMargin = mContainer.getWidth() / 4 - mWaveView.getWidth() / 2 + AppUtil.dp2px(mActivity, 8);
        mWaveView.setLayoutParams(lp);
    }

    private Animator backgroundMaskAnimator(long duration, long delay) {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1).setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                mBackgroundMaskView.setAlpha(animatedValue);
                mBackgroundDimMaskView.setAlpha(animatedValue);
            }
        });
        animator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBackgroundDimMaskView.setVisibility(View.VISIBLE);
                mBackgroundMaskView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mProductRowLayout.setVisibility(View.VISIBLE);
            }
        });

        ValueAnimator tipAnimator = ValueAnimator.ofFloat(0f, 1);
        tipAnimator.setDuration(duration);//1000
        tipAnimator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTipsView.setVisibility(View.VISIBLE);
            }
        });
        tipAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
//                mTipsView.setAlpha(animatedValue);
                mTipsView.setScaleX(animatedValue);
                mTipsView.setScaleY(animatedValue);
            }
        });

        animatorSet.playTogether(animator, tipAnimator);

        return animatorSet;
    }

    private Animator emptyAnimator(long duration, long delay) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1);
        animator.setDuration(duration);//1000
        return animator;
    }

    private Animator fingerAppearAnimator(long duration, long delay) {
        float value = -mContainer.getWidth() * 3 / 4;
        Animator animator = ObjectAnimator.ofFloat(mFingerView, "translationX", 0, value)
                .setDuration(duration);
        animator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFingerView.setTranslationY(mRefreshLayout.getTopAndTabHeight() - mContainer.getHeight());
                mFingerView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator fingerSmoothAnimator(long duration, long delay) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1);
        animator.setDuration(duration);//1000
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            boolean firstFrame = true;

            float startFingerY = 0;
//            float fingerOffsetY = 0;

            float smoothDistance = 0;
            float smoothStartY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (firstFrame) {
                    firstFrame = false;
                    smoothStartY = mRefreshLayout.getScrollY();
                    smoothDistance = caclSmoothDistance();
                    startFingerY = mFingerView.getTranslationY();
//                    fingerOffsetY = startFingerY + mContainer.getHeight()/2;
                }
                float animatedValue = (float) animation.getAnimatedValue();
                mFingerView.setTranslationY(startFingerY - smoothDistance * animatedValue);
                mRefreshLayout.scrollTo(mRefreshLayout.getScrollX(), (int) (animatedValue * smoothDistance + smoothStartY));
//                ViewCompat.postInvalidateOnAnimation(mRefreshLayout);
            }
        });
        return animator;
    }

    private float caclSmoothDistance() {
        int[] destLocation = new int[2];
        mProductRowLayout.getLocationOnScreen(destLocation);

        int[] srcLocation = new int[2];
        mListView.getLocationOnScreen(srcLocation);

        return srcLocation[1] - destLocation[1];
    }

    private Animator fingerOverAnimator(long duration, long delay) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1);
        animator.setDuration(duration);//1000
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean firstFrame = true;
            float startTransY;
            float distance;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (firstFrame) {
                    startTransY = mFingerView.getTranslationY();
                    distance = (mFingerView.getHeight() - mContainer.getHeight()) / 2 - startTransY;
                    firstFrame = false;
                }
                float animatedValue = (float) animation.getAnimatedValue();
                mFingerView.setTranslationY(startTransY + distance * animatedValue);
            }
        });
        return animator;
    }

    private Animator fingerPushAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();

        final float pushValue = AppUtil.dp2px(mActivity, 3);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f, 0f);
        animator.setDuration(300);//1000
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
//                ViewCompat.postInvalidateOnAnimation(mRefreshLayout);
            }
        });

//        Animator pressAnimator = ObjectAnimator.ofFloat(mPressedView, "alpha", 0.f, 1f, 0f).setDuration(1000);
//        pressAnimator.setStartDelay(200);
//        pressAnimator.addListener(new WrapAnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mPressedView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mPressedView.setVisibility(View.INVISIBLE);
//            }
//        });
        //pressAnimator
        animatorSet.playTogether(animator, mWaveView.getAnimator(2000, 100));//
        return animatorSet;
    }


}
