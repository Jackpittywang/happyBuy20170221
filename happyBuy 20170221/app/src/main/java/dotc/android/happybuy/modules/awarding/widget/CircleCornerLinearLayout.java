package dotc.android.happybuy.modules.awarding.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/9/26.
 */
public class CircleCornerLinearLayout extends FrameLayout {

//    private
    private Path mTopShadowPath;
    private Path mBottomShadowPath;
    private Path mPath;
    private Paint mPaint;
    private LinearGradient mBackgroundShader;
    private LinearGradient mTopShader;
    private LinearGradient mBottomShader;
    private boolean mBoostLayout = true;
    private float mArcRadius;
    private float mShadowSize;
    private int[] mColors;
    private int[] mShadowColors;

    private boolean mDrawShadow;
    private boolean mAnimingDraw;
    private Animator mShadowVisiableAnimator;
    private Animator mShadowGoneAnimator;
    private float mAnimFactor;

    public CircleCornerLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public CircleCornerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleCornerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
//        mPaint.setStrokeWidth();
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStrokeWidth(mEdgeSize);
        mPaint.setColor(0xfffef532);
        mPaint.setAntiAlias(true);

        mColors = new int[]{0xffff0096,0xffe64539};
        mShadowColors = new int[]{0x7f000000,0x00000000};
        mArcRadius = AppUtil.dp2px(context,30);
        mShadowSize = mArcRadius*1.5f;
    }

    public void setShadowVisiable(boolean visiable){
        mDrawShadow = visiable;
        invalidate();
    }

    public Animator getShadowVisiableAnimator(long duration){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimFactor = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimingDraw = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimingDraw = false;
            }
        });
        mShadowVisiableAnimator = valueAnimator;
        mDrawShadow = true;
        return mShadowVisiableAnimator;
    }

    public Animator getShadowGoneAnimator(long duration){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimFactor = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimingDraw = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimingDraw = false;
                setShadowVisiable(false);
            }
        });
        mShadowGoneAnimator = valueAnimator;
        return mShadowGoneAnimator;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mShadowVisiableAnimator!=null&&mShadowVisiableAnimator.isStarted()){
            mShadowVisiableAnimator.cancel();
        }
        if(mShadowGoneAnimator!=null&&mShadowGoneAnimator.isStarted()){
            mShadowGoneAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBoostLayout){
            mBoostLayout = false;
            mPath = initPathWithBoost();
//            mTopShadowPath = initTopPathWithBoost();
//            mBottomShadowPath = initBottomPathWithBoost();
            mBackgroundShader = new LinearGradient(0, 0, getWidth(), getHeight(), mColors, null, Shader.TileMode.MIRROR);
//            mTopShader = new LinearGradient(0, 0, 0, mShadowSize, mShadowColors, null, Shader.TileMode.MIRROR);
//            mBottomShader = new LinearGradient(0, getHeight(), 0, getHeight()-mShadowSize, mShadowColors, null, Shader.TileMode.MIRROR);
        }
        mPaint.setShader(mBackgroundShader);
        canvas.drawPath(mPath,mPaint);

//        if(mAnimingDraw){
//            float alpha = mAnimFactor;
//
//            mPaint.setShader(mTopShader);
//            canvas.drawPath(mTopShadowPath,mPaint);
//            mPaint.setShader(mBottomShader);
//            canvas.drawPath(mBottomShadowPath,mPaint);
//        } else {
//            if(mDrawShadow){
//                mPaint.setShader(mTopShader);
//                canvas.drawPath(mTopShadowPath,mPaint);
//                mPaint.setShader(mBottomShader);
//                canvas.drawPath(mBottomShadowPath,mPaint);
//            }
//        }
    }

    private Path initPathWithBoost(){
        Path path = new Path();
        float left = 0,top = 0,right = getWidth(),bottom = getHeight();
        float delta = mArcRadius*0.5222f;
        path.moveTo(left+mArcRadius,top);
        path.cubicTo(left+mArcRadius-delta,top,left,mArcRadius-delta+top,left,mArcRadius+top);

        path.lineTo(left,bottom-mArcRadius);
        path.cubicTo(left,bottom-mArcRadius+delta,mArcRadius-delta+left,bottom,mArcRadius+left,bottom);

        path.lineTo(right-mArcRadius,bottom);
        path.cubicTo(right-mArcRadius+delta,bottom,right,bottom-(mArcRadius-delta),right,bottom-mArcRadius);

        path.lineTo(right,top+mArcRadius);
        path.cubicTo(right,top+mArcRadius-delta,right-(mArcRadius-delta),top,right -mArcRadius,top);
        path.close();
        return path;
    }

    private Path initTopPathWithBoost(){
        Path path = new Path();
        float left = 0,top = 0,right = getWidth(),bottom = getHeight();
        float delta = mArcRadius*0.5222f;
        path.moveTo(left+mArcRadius,top);
        path.cubicTo(left+mArcRadius-delta,top,left,mArcRadius-delta+top,left,mArcRadius+top);
        path.lineTo(left,top+mShadowSize);
        path.lineTo(right,top+mShadowSize);
        path.lineTo(right,top+mArcRadius);
        path.cubicTo(right,top+mArcRadius-delta,right-(mArcRadius-delta),top,right -mArcRadius,top);
        path.close();
        return path;
    }

    private Path initBottomPathWithBoost(){
        Path path = new Path();
        float left = 0,top = 0,right = getWidth(),bottom = getHeight();
        float delta = mArcRadius*0.5222f;
        path.moveTo(left,bottom-mShadowSize);
        path.lineTo(left,bottom-mArcRadius);
        path.cubicTo(left,bottom-mArcRadius+delta,mArcRadius-delta+left,bottom,mArcRadius+left,bottom);
        path.lineTo(right-mArcRadius,bottom);
        path.cubicTo(right-mArcRadius+delta,bottom,right,bottom-(mArcRadius-delta),right,bottom-mArcRadius);
        path.lineTo(right,bottom-mShadowSize);
        path.close();
        return path;
    }
}
