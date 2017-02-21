package dotc.android.happybuy.modules.awarding.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;

/**
 * Created by wangjun on 16/3/9.
 */
public class TimeCountView extends RelativeLayout{

    private Context mContext;

    private AnimTextView digitMinute1TextView,digitMinute2TextView;
    private AnimTextView digitSecond1TextView,digitSecond2TextView;
    private TextView digitMSec1TextView,digitMSec2TextView;
//    private TextView awardedResultTextView;

    private Animator mAnimator;

    public TimeCountView(Context context) {
        super(context);
        init(context);
    }

    public TimeCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_time_count, this);

        digitMinute1TextView = (AnimTextView) findViewById(R.id.textview_digit_minute_1);
        digitMinute2TextView = (AnimTextView) findViewById(R.id.textview_digit_minute_2);
        digitSecond1TextView = (AnimTextView) findViewById(R.id.textview_digit_second_1);
        digitSecond2TextView = (AnimTextView) findViewById(R.id.textview_digit_second_2);
        digitMSec1TextView = (TextView) findViewById(R.id.textview_digit_msec_1);
        digitMSec2TextView = (TextView) findViewById(R.id.textview_digit_msec_2);
//        awardedResultTextView = (TextView) findViewById(R.id.text_timer_down);
    }

    public void setTimerDown(long millisUntilFinished){
        long minute = (millisUntilFinished%(60*60*1000))/(60*1000);
        long second = (millisUntilFinished%(60*1000))/1000;
        long millSecond = (millisUntilFinished%100);

        if(minute<10){
            digitMinute1TextView.setText(String.valueOf("0"));
            digitMinute2TextView.setText(String.valueOf(minute));
        } else {
            digitMinute1TextView.setText(String.valueOf(minute/10));
            digitMinute2TextView.setText(String.valueOf(minute%10));
        }

        if(second<10){
            digitSecond1TextView.setText(String.valueOf("0"));
            digitSecond2TextView.setText(String.valueOf(second));
        } else {
            digitSecond1TextView.setText(String.valueOf(second/10));
            digitSecond2TextView.setText(String.valueOf(second%10));
        }

        if(millSecond<10){
            digitMSec1TextView.setText(String.valueOf("0"));
            digitMSec2TextView.setText(String.valueOf(millSecond));
        } else {
            digitMSec1TextView.setText(String.valueOf(millSecond/10));
            digitMSec2TextView.setText(String.valueOf(millSecond%10));
        }
    }

//    public void setAwardedResultWithAnim(String awardedNickname){
//        awardedResultTextView.setText(awardedNickname);
//        AnimatorSet animatorSet = new AnimatorSet();
//        ValueAnimator disappearAnimator = ValueAnimator.ofFloat(1,1.2f,0).setDuration(500);
//        disappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                mStatusQueryLayout.setScaleX(value);
//                mStatusQueryLayout.setScaleY(value);
////                mStatusQueryLayout.setAlpha();
//            }
//        });
//        disappearAnimator.setTarget(mStatusQueryLayout);
//
//        ValueAnimator appearAnimator = ValueAnimator.ofFloat(0,1,1.2f,1f).setDuration(500);
//        appearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                mStatusAwardedLayout.setScaleX(value);
//                mStatusAwardedLayout.setScaleY(value);
//            }
//        });
//        appearAnimator.addListener(new WrapAnimatorListener(){
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mStatusQueryLayout.setVisibility(INVISIBLE);
//                mStatusAwardedLayout.setVisibility(VISIBLE);
//            }
//        });
//        appearAnimator.setTarget(mStatusAwardedLayout);
//        animatorSet.playSequentially(disappearAnimator,appearAnimator);
//        mAnimator = animatorSet;
//        animatorSet.start();
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator!=null&&mAnimator.isStarted()){
            mAnimator.cancel();
        }
    }

}
