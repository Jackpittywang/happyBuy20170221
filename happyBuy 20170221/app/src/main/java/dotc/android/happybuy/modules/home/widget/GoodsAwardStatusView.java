package dotc.android.happybuy.modules.home.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;

/**
 * Created by wangjun on 16/3/9.
 */
public class GoodsAwardStatusView extends RelativeLayout{

    private Context mContext;
    private View mStatusAwardingLayout;
    private View mStatusAwardedLayout;
    private View mStatusQueryLayout;

    private TextView digitMinuteTextView,digitSecondextView,digitMSecTextView;
    private TextView awardedResultTextView;

    private Animator mAnimator;

    public GoodsAwardStatusView(Context context) {
        super(context);
        init(context);
    }

    public GoodsAwardStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoodsAwardStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_home_goods_status, this);
        mStatusAwardingLayout = findViewById(R.id.layout_status_awarding);
        mStatusAwardedLayout = findViewById(R.id.layout_status_awarded);
        mStatusQueryLayout = findViewById(R.id.layout_status_query);

        digitMinuteTextView = (TextView) findViewById(R.id.textview_digit_minute);
        digitSecondextView = (TextView) findViewById(R.id.textview_digit_second);
        digitMSecTextView = (TextView) findViewById(R.id.textview_digit_msec);

        awardedResultTextView = (TextView) findViewById(R.id.text_timer_down);
    }

    public void setTimerDown(long minute,long second,long msec){
        mStatusAwardingLayout.setVisibility(View.VISIBLE);
        mStatusQueryLayout.setVisibility(View.INVISIBLE);
        mStatusAwardedLayout.setVisibility(View.INVISIBLE);

        digitMinuteTextView.setText(String.valueOf(minute<10?"0"+minute:minute));
        digitSecondextView.setText(String.valueOf(second<10?"0"+second:second));
        digitMSecTextView.setText(String.valueOf(msec<10?"0"+msec:msec));
    }

    public void setQuering(){
        mStatusAwardingLayout.setVisibility(View.INVISIBLE);
        mStatusQueryLayout.setVisibility(View.VISIBLE);
        mStatusAwardedLayout.setVisibility(View.INVISIBLE);
    }

    public void setQueringWithAnim(){

    }

    public void setAwardedResult(String awardedNickname){
        mStatusAwardingLayout.setVisibility(View.INVISIBLE);
        mStatusQueryLayout.setVisibility(View.INVISIBLE);
        mStatusAwardedLayout.setVisibility(View.VISIBLE);

        awardedResultTextView.setText(awardedNickname);
    }

    public void setAwardedResultWithAnim(String awardedNickname){
        awardedResultTextView.setText(awardedNickname);
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator disappearAnimator = ValueAnimator.ofFloat(1,1.2f,0).setDuration(500);
        disappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mStatusQueryLayout.setScaleX(value);
                mStatusQueryLayout.setScaleY(value);
//                mStatusQueryLayout.setAlpha();
            }
        });
        disappearAnimator.setTarget(mStatusQueryLayout);

        ValueAnimator appearAnimator = ValueAnimator.ofFloat(0,1,1.2f,1f).setDuration(500);
        appearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mStatusAwardedLayout.setScaleX(value);
                mStatusAwardedLayout.setScaleY(value);
            }
        });
        appearAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mStatusQueryLayout.setVisibility(INVISIBLE);
                mStatusAwardedLayout.setVisibility(VISIBLE);
            }
        });
        appearAnimator.setTarget(mStatusAwardedLayout);
        animatorSet.playSequentially(disappearAnimator,appearAnimator);
        mAnimator = animatorSet;
        animatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator!=null&&mAnimator.isStarted()){
            mAnimator.cancel();
        }
    }

}
