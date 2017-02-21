package dotc.android.happybuy.uibase.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;


/**
 * Created by wangjun on 16/12/21.
 */

public class WaveView extends View {

    private final String TAG = this.getClass().getSimpleName();
    private Paint mPaint;
    private RadialGradient mRadialGradient;
    private Animator mAnimator;
    private Interpolator mInterpolator = new LinearInterpolator();
    private float mDuration;
    private float mAnimatorValue;

    private int mWaveCount;
    private int mCenterX;
    private int mCenterY;
    private int mRadius;

    private List<Circle> mCircleList = new ArrayList<>();
    private long mLastCreateTime;

    private boolean mBoost = false;


    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffFFBFBF);
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(50f);
        mWaveCount = 3;
    }

    public Animator getAnimator(long duration, long delay) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);//1000
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                ViewCompat.postInvalidateOnAnimation(WaveView.this);
            }
        });
        animator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                mCircleList.clear();
            }
        });
        this.mDuration = duration;
        mAnimator = animator;
        return mAnimator;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mBoost && getWidth() > 0) {
            mBoost = false;
            initOnBoost();
        }
        if (mAnimator != null&& mAnimator.isStarted()) {
            if(System.currentTimeMillis() - mLastCreateTime >500&&mCircleList.size()<mWaveCount){
                mLastCreateTime = System.currentTimeMillis();
                Circle circle = new Circle();
                mCircleList.add(circle);
            }
            for (Circle circle:mCircleList) {
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(mCenterX, mCenterY, circle.getCurrentRadius(), mPaint);
            }
        }
    }

    private void initOnBoost() {
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mRadius = getWidth() / 2;
    }

    class Circle {
        private long createTime;

        public Circle() {
            createTime = System.currentTimeMillis();
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - createTime) * 1.0f / 1000;
            percent = Math.min(1,percent);
            return (int) (((1.0f - mInterpolator.getInterpolation(percent)) * 255)*0.2f);
        }

        public float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - createTime) * 1.0f / 1000;
            percent = Math.min(1,percent);
            return mInterpolator.getInterpolation(percent) * mRadius;
        }
    }

}
