package dotc.android.happybuy.modules.awarding.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;

/**
 * Created by wangjun on 16/10/17.
 */

public class AnimTextView extends FrameLayout {

    private TextView[] mTextViews;
    private Animator mAnimator;
    private String mText;
    private int mCursor;

    public AnimTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_anim_textview, this);
        TextView textView1 = (TextView) findViewById(R.id.text1);
        TextView textView2 = (TextView) findViewById(R.id.text2);
        mTextViews = new TextView[]{textView1,textView2};

    }

    public void setText(String text){
        if(TextUtils.isEmpty(mText)){
            this.mText = text;
            final TextView appearTextView = mTextViews[mCursor];
            appearTextView.setText(mText);
            appearTextView.setVisibility(View.VISIBLE);
        } else if(!text.equals(this.mText)){
            setTextWithAnim(text);
        }
    }

    public void setTextWithAnim(String text){
        this.mText = text;
        final int nextCursor = (mCursor+1)%mTextViews.length;
        final TextView disappearTextView = mTextViews[mCursor];
        final TextView appearTextView = mTextViews[nextCursor];
        appearTextView.setText(text);
        AnimatorSet animatorSet = new AnimatorSet();
        long duration = 500;
        int offset = getHeight();
        Animator disappearAnimator = ObjectAnimator.ofFloat(disappearTextView, "translationY", 0, -offset).setDuration(duration);
        Animator appearAnimator = ObjectAnimator.ofFloat(appearTextView, "translationY", offset, 0).setDuration(duration);
        animatorSet.playTogether(disappearAnimator,appearAnimator);
        animatorSet.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                appearTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                disappearTextView.setVisibility(View.INVISIBLE);
                mCursor = nextCursor;
            }
        });
        animatorSet.start();
        mAnimator = animatorSet;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator!=null&&mAnimator.isStarted()){
            mAnimator.cancel();
        }
    }

}
