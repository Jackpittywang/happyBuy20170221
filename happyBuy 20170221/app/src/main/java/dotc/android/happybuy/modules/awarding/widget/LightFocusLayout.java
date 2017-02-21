package dotc.android.happybuy.modules.awarding.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;


/**
 * Created by wangjun on 16/9/25.
 */
public class LightFocusLayout extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();
    private Animator mAnimator;

    private Paint mPaint;
    private Paint mAnimPaint;
    private PathMeasure mPathMeasure;
    private boolean mPathInit = false;
    private boolean mAnim;
    private float mPathTotalLength;
    private float mAnimFactor;

    private Path mPath;
    private Path mAnimPath;

//    private float mArcLength;
    private float mArcRadius;
    private int mPadding;
    private int mAnimStrokeWidth;

    public LightFocusLayout(Context context) {
        super(context);
        init(context);
    }

    public LightFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mPadding = dp2px(context,8);
        mAnimStrokeWidth = dp2px(context,2);
        mPaint = new Paint();
//        mPaint.setStrokeWidth();
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStrokeWidth(mPadding);
        mPaint.setColor(0xfffff600);
        mPaint.setAntiAlias(true);

        mAnimPaint = new Paint();
        mAnimPaint.setStrokeWidth(mAnimStrokeWidth);
        mAnimPaint.setStyle(Paint.Style.STROKE);
        mAnimPaint.setColor(Color.WHITE);
        mAnimPaint.setAntiAlias(true);

    }

    public Animator getHighLightAnim(long duration,long delay,final View sunShineView){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(delay);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.5f);//,mPathTotalLength*1.5f
        valueAnimator.setDuration(duration);
//        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean firstFrame = true;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                if(firstFrame){
                    firstFrame = false;
                    initPathIfNull();
                }
                mAnimFactor = mPathTotalLength*value;
                printDebug("onAnimationUpdate mAnimFactor:"+mAnimFactor);
                postInvalidate();
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mAnim = true;
//                setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnim = false;
            }
        });

        ValueAnimator sunShineAnimator = ValueAnimator.ofFloat(0f,1.0f,0f);
        sunShineAnimator.setDuration(duration/3);
        sunShineAnimator.setStartDelay(duration*2/3 - duration/10);
//        sunShineAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        sunShineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean rotate = true;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                printDebug("onAnimationUpdate value:"+value);
//                sunShineView.setAlpha(value);
                if(rotate){
                    sunShineView.setRotation(180*value);
                    if(value>=0.98f){
                        rotate = false;
                    }
                }
                sunShineView.setScaleX(value);
                sunShineView.setScaleY(value);
            }
        });
        sunShineAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                sunShineView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                sunShineView.setVisibility(View.INVISIBLE);
//                sunShineView.setScaleX(1);
//                sunShineView.setScaleY(1);
            }
        });
        animatorSet.playTogether(valueAnimator,sunShineAnimator);
        mAnimator = animatorSet;
        return mAnimator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        printDebug("onMeasure ----- "+getMeasuredWidth());
    }

    private void initPathIfNull(){
        printDebug("initPathIfNull "+mPathInit+" "+getHeight()+" "+getWidth());
        if(!mPathInit&&getHeight()>0){
            mPathInit = true;
            mPath = initPathWithBoost(getWidth(),getHeight());
            mPathMeasure = new PathMeasure(mPath,false);
            mPathTotalLength = mPathMeasure.getLength();
//            mPathTotalLength = mPathMeasure.getLength();
//            mArcLength = (float) (Math.PI * getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPathIfNull();
        printDebug("onDraw mAnim:"+mAnim);
        if(mAnim&&mPathInit){
            float maxLength = Math.min(mAnimFactor,mPathTotalLength/2);
            float startD,stopD;
            if(mAnimFactor<mPathTotalLength/2){
                startD = 0;
                stopD = mAnimFactor;
            } else if(mAnimFactor>=mPathTotalLength) {
                startD = mPathTotalLength/2+(mAnimFactor - mPathTotalLength);
                stopD = mPathTotalLength;
            } else {
                startD = mAnimFactor - maxLength;
                stopD = mAnimFactor;
            }
            printDebug("onDraw startD:"+startD+" startD:"+startD+" maxLength:"+maxLength);
            int segmentCount = mAnimStrokeWidth,positon = 0;
            float segmentLength = Math.max(dp2px(getContext(),1),(stopD-startD)/segmentCount);
            float tempD = startD;
            while (positon<segmentCount){
                Path partialPath = new Path();
                partialPath.lineTo(0,0);
                mPathMeasure.getSegment(tempD,tempD+segmentLength,partialPath,true);
//                int alpha = (int) (255*(0.8f+(positon+1.0f)/(segmentCount*5)));
//                mAnimPaint.setAlpha(alpha);
                mAnimPaint.setStrokeWidth(mAnimStrokeWidth*(positon+1.0f)/segmentCount);
                printDebug("onDraw "+mAnimStrokeWidth*(positon+1.0f)/segmentCount+" "+segmentLength);

                canvas.drawPath(partialPath,mAnimPaint);
                tempD +=segmentLength;
                positon++;
            }
        }
//        canvas.drawPath(mPath,mPaint);
    }

    private Path initPathWithBoost(int width,int height){
        Path path = new Path();
//        printDebug("--------------2-- "+Math.tan(Math.toRadians(20)));
        float left = mPadding,top = mPadding,right = width- mPadding,bottom = height - mPadding;
        mArcRadius = getHeight()/2 - mPadding;
        float delta = mArcRadius*0.5222f;
        path.moveTo(left+mArcRadius,top);
        path.cubicTo(left+mArcRadius-delta,top,left,mArcRadius-delta+top,left,mArcRadius+top);
        path.cubicTo(left,mArcRadius+delta+top,mArcRadius-delta+left,mArcRadius*2+top,mArcRadius+left,mArcRadius*2+top);

        path.lineTo(right-mArcRadius,bottom);
        path.cubicTo(right-mArcRadius+delta,bottom,right,mArcRadius+delta+top,right,bottom-mArcRadius);
        path.cubicTo(right,top+mArcRadius-delta,right-(mArcRadius-delta),top,right -mArcRadius,top);
        path.close();
        return path;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator!=null&&mAnimator.isStarted()){
            mAnimator.cancel();
        }
    }

    public static int dp2px(Context context,float dp) {
        final float dentisy = context.getResources().getDisplayMetrics().density;
        return (int) (0.5f + dentisy * dp);
    }

    private void printDebug(String msg) {
        if (false&&HBLog.isLogEnable()) {
            HBLog.d(TAG + " " + msg);
        }
    }
}
