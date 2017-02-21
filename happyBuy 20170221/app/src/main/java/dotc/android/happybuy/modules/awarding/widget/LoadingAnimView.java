package dotc.android.happybuy.modules.awarding.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.uibase.anim.Duration;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/9/27.
 */
public class LoadingAnimView extends View {
    private final String TAG = this.getClass().getSimpleName();
    private Animator mIncreaseAnimator;
    private Animator mMaskAnimator;
    private Paint mPaint;
    private int mMargin;
    private int mSize;
    private int mDotCount;
//    private int mIndex;
    private boolean mBoostLayout = true;
    private int m20Color = 0x33ffffff;
    private int m30Color = 0x4cffffff;
    private int m40Color = 0x66ffffff;
    private int m50Color = 0x7fffffff;
    private int m60Color = 0x99ffffff;
    private int m70Color = 0xb2ffffff;
    private int m80Color = 0xccffffff;
    private int m90Color = 0xe5ffffff;
    private int m100Color = 0xffffffff;

    private int mIncreasingAnimationValue;
    private float mMaskAnimationValue;

    public LoadingAnimView(Context context) {
        super(context);
        init(context);
    }

    public LoadingAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(m100Color);
        mMargin = AppUtil.dp2px(context,3);
        mSize = AppUtil.dp2px(context,5);
        mDotCount = 8;
    }

    public Animator getLoadingAnimator(long duration){
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator increaseAnimator = ValueAnimator.ofInt(0, mDotCount);
        increaseAnimator.setDuration(mDotCount* Duration.value(0.05f));
        increaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIncreasingAnimationValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator maskAnimator = ValueAnimator.ofFloat(1f, 0f);
        maskAnimator.setStartDelay(mDotCount* Duration.value(0.05f)/2);
        maskAnimator.setDuration(duration);
        maskAnimator.setRepeatMode(ValueAnimator.RESTART);
        maskAnimator.setRepeatCount(ValueAnimator.INFINITE);
        maskAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMaskAnimationValue = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animatorSet.playTogether(increaseAnimator,maskAnimator);
        mIncreaseAnimator = increaseAnimator;
        mMaskAnimator = maskAnimator;
        return animatorSet;
    }

    private int[] mAnimMask = new int[]{m100Color,m90Color,m80Color,m70Color,m60Color,m50Color,m40Color,
            m30Color,m20Color,m30Color,m40Color,m50Color,m60Color,m70Color,m80Color,m90Color,m100Color};

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        HBLog.d(TAG+" onDraw ----");
        if(mMaskAnimator!=null&&mMaskAnimator.isStarted()){
            int position = (int) (mMaskAnimationValue /(1.0f/mAnimMask.length));
//            int firstColor =
            HBLog.d(TAG+" onDraw position:"+position);
            for (int i = 0; i < mIncreasingAnimationValue; i++) {
                final int left = (mSize + mMargin) * i;
                if(position<mAnimMask.length){
                    mPaint.setColor(mAnimMask[position++]);
                } else {
                    position = 0;
                    mPaint.setColor(mAnimMask[position++]);
                }

                canvas.drawRect(left, 0, left+mSize,mSize, mPaint);
            }
        } else {
            if(mIncreaseAnimator!=null&&mIncreaseAnimator.isStarted()){
                for (int i = 0; i < mIncreasingAnimationValue; i++) {
                    final int left = (mSize + mMargin) * i;
                    canvas.drawRect(left, 0, left+mSize,mSize, mPaint);
                }
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mSize * mDotCount + (mDotCount - 1) * mMargin;
        setMeasuredDimension(width, mSize);
    }

    private void drawLoading(Canvas canvas){

    }

}
