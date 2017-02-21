package dotc.android.happybuy.modules.awarding.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/9/26.
 */
public class AnimationLayout extends FrameLayout {
    private static String TAG = AnimationLayout.class.getSimpleName();

    private static List<Target> sBallTargetList = new ArrayList<>();
    private static List<Target> sCoinTargetList = new ArrayList<>();
    static {
        //left top
        sBallTargetList.add(new Target(true,true,40,70,R.drawable.ic_anim_ball_style_3,2.8f,0));
        sBallTargetList.add(new Target(true,true,120,168,R.drawable.ic_anim_ball_style_2,1.8f,0.2f));//
        sBallTargetList.add(new Target(true,true,64,232,R.drawable.ic_anim_ball_style_1,1.3f,0.3f));
        //left bottom
        sBallTargetList.add(new Target(true,false,100,250,R.drawable.ic_anim_ball_style_3,0.7f,0.3f));
        sBallTargetList.add(new Target(true,false,82,179,R.drawable.ic_anim_ball_style_2,1.8f,0.2f));
        sBallTargetList.add(new Target(true,false,25,112,R.drawable.ic_anim_ball_style_3,1.4f,0));
        //right top
        sBallTargetList.add(new Target(false,true,55,75,R.drawable.ic_anim_ball_style_3,1f,0));
        sBallTargetList.add(new Target(false,true,81,160,R.drawable.ic_anim_ball_style_2,1f,0.2f));
        sBallTargetList.add(new Target(false,true,30,190,R.drawable.ic_anim_ball_style_3,1.4f,0.1f));
        sBallTargetList.add(new Target(false,true,132,201,R.drawable.ic_anim_ball_style_1,0.7f,0.3f));
        //right bottom
        sBallTargetList.add(new Target(false,false,37,250,R.drawable.ic_anim_ball_style_3,1.9f,0.2f));
        sBallTargetList.add(new Target(false,false,75,195,R.drawable.ic_anim_ball_style_1,0.7f,0.2f));
        sBallTargetList.add(new Target(false,false,95,75,R.drawable.ic_anim_ball_style_3,2.8f,0));

        sCoinTargetList.add(new Target(true,true,60,80,R.drawable.ic_cion_seven));
        sCoinTargetList.add(new Target(true,true,94,146,R.drawable.ic_cion_three));
        sCoinTargetList.add(new Target(true,true,48,240,R.drawable.ic_cion_eight));
        sCoinTargetList.add(new Target(true,false,60,250,R.drawable.ic_cion_three));
        sCoinTargetList.add(new Target(true,false,125,205,R.drawable.ic_cion_four));
        sCoinTargetList.add(new Target(true,false,30,150,R.drawable.ic_cion_four));
        sCoinTargetList.add(new Target(false,true,30,137,R.drawable.ic_cion_six));
        sCoinTargetList.add(new Target(false,true,130,185,R.drawable.ic_cion_three));
        sCoinTargetList.add(new Target(false,true,62,250,R.drawable.ic_cion_four));
        sCoinTargetList.add(new Target(false,false,50,200,R.drawable.ic_cion_eight));

    }

    private Animator mBallExplodeAnimator;
    private Animator mBallShakeAnimator;
    private Animator mBallTwoDropAnimator;
    private Animator mBallDropDisappearAnimator;
    private Animator mCoinAnimator;

    private float mBallShakeFactor;
    private float mBallDropDistance;

//    private Paint mPaint;
//    private boolean mBoostLayout = true;
    private List<Cell> mAnimCells;

    public AnimationLayout(Context context) {
        super(context);
        init(context);
    }

    public AnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
//        LayoutInflater.from(context).inflate(R.layout.layout_awarding_anim, this);
        mAnimCells = new ArrayList<>();
//        mShakeRadianLimit = AppUtil.dp2px(context,18);//36
        mBallShakeFactor = 36*1.0f /197;//197 is ic_ball_anim_13 height
        mBallDropDistance = AppUtil.dp2px(context,80);
    }

    private void initCellBeforeAnim(){
        mAnimCells.clear();
        for(int i = 0; i<getChildCount()&&i< sBallTargetList.size(); i++){
            ImageView view = (ImageView) getChildAt(i);
            Target target = sBallTargetList.get(i);
            view.setImageResource(target.resid);

            float offsetX = 0,offsetY = 0;
            if(target.alignLeft){
                offsetX = AppUtil.dp2px(GlobalContext.get(),target.hOffset) - getWidth()/2;
            } else {
                offsetX = getWidth()/2 - AppUtil.dp2px(GlobalContext.get(),target.hOffset);
            }

            if(target.alignTop){
                offsetY = AppUtil.dp2px(GlobalContext.get(),target.vOffset) - getHeight()/2;
            } else {
                offsetY = getHeight()/2 - AppUtil.dp2px(GlobalContext.get(),target.vOffset);
            }
            Cell cell = new Cell(target,view,offsetX,offsetY);
//            Cell cell = new Cell(resPosition.hOffset,resPosition.vOffset,resPosition.scale,view);
            mAnimCells.add(cell);
        }
    }

    private float accleValue(float offset,float value,float accelerate){
        if(offset<0){
            return Math.max(offset*value*(1+accelerate),offset);
        } else {
            return Math.min(offset*value*(1+accelerate),offset);
        }
    }

    public Animator getExplodeAnimator(long duration,long delay){
        initCellBeforeAnim();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setStartDelay(delay);
//        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
//                HBLog.d(TAG+" --------------------ExplodeAnimator  onAnimationUpdate"+animatedValue);
                for(Cell cell:mAnimCells){
                    float translationX = accleValue(cell.destOffsetX,value,cell.target.accelerate);
                    float translationY = accleValue(cell.destOffsetY,value,cell.target.accelerate);
                    cell.view.setTranslationX(translationX);
                    cell.view.setTranslationY(translationY);
                    cell.view.setScaleX(cell.target.scale *value);
                    cell.view.setScaleY(cell.target.scale *value);
//                    cell.view.setAlpha(animatedValue);
                }
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                for(Cell cell:mAnimCells){
                    cell.view.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                for(Cell cell:mAnimCells){
                    cell.lastOffsetX = cell.view.getTranslationX();
                    cell.lastOffsetY = cell.view.getTranslationY();
                }
            }
        });
        mBallExplodeAnimator = valueAnimator;
        return mBallExplodeAnimator;
    }

    public Animator getShakeAnimator(long duration){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1,0f);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (Float) animation.getAnimatedValue();
                for(int i=0;i<mAnimCells.size();i++){
                    Cell cell = mAnimCells.get(i);
                    if(i<mAnimCells.size()/2){
                        float translationY = cell.lastOffsetY + cell.view.getHeight()* mBallShakeFactor *animatedValue;
//                        cell.currentOffsetY = cell.destOffsetY*animatedValue;
                        cell.view.setTranslationY(translationY);
                    } else {
                        float translationY = cell.lastOffsetY - cell.view.getHeight()* mBallShakeFactor *animatedValue;
                        cell.view.setTranslationY(translationY);
                    }
                }
            }
        });
        mBallShakeAnimator = valueAnimator;
        return mBallShakeAnimator;
    }

    public Animator getTwoDropAnimator(long duration,long delay){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setStartDelay(delay);
        final List<Cell> twoCells = new ArrayList<>();
        twoCells.add(mAnimCells.remove(9));
        twoCells.add(mAnimCells.remove(1));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean fistFrame = true;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(fistFrame){
                    fistFrame = false;
                    for(int i=0;i<twoCells.size();i++){
                        Cell cell = twoCells.get(i);
                        cell.lastOffsetY = cell.view.getTranslationY();
                    }
                }

                float value = (Float) animation.getAnimatedValue();
                for(Cell cell:twoCells){
                    if(value < cell.target.accelerate){
                        value = 0;
                    } else {
                        value = (value - cell.target.accelerate)*1/(1-cell.target.accelerate);
                    }
                    cell.view.setTranslationY(cell.lastOffsetY+ mBallDropDistance *value);
                    cell.view.setAlpha((1-value));
                }
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                for(Cell cell:twoCells){
                    cell.view.setVisibility(View.GONE);
                }
            }
        });

        mBallTwoDropAnimator = valueAnimator;
        return mBallTwoDropAnimator;
    }

    public Animator getDropDisappearAnimator(long duration,long delay){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setStartDelay(delay);
//        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean fistFrame = true;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(fistFrame){
                    fistFrame = false;
                    HBLog.d("print onAnimationUpdate first frame ------------------");
                    for(int i=0;i<mAnimCells.size();i++){
                        Cell cell = mAnimCells.get(i);
                        cell.lastOffsetY = cell.view.getTranslationY();
                    }
                }

                float value = (Float) animation.getAnimatedValue();
                for(Cell cell:mAnimCells){
                    if(value < cell.target.accelerate){
                        value = 0;
                    } else {
                        value = (value - cell.target.accelerate)*1/(1-cell.target.accelerate);
                    }
                    cell.view.setTranslationY(cell.lastOffsetY+ mBallDropDistance *value);
                    cell.view.setAlpha((1-value));
                }
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                HBLog.d("getDropDisappearAnimator onAnimationEnd ------");
                for(Cell cell:mAnimCells){
                    cell.view.setVisibility(View.GONE);
                }
            }
        });
        mBallDropDisappearAnimator = valueAnimator;
        return mBallDropDisappearAnimator;
    }

    public Animator getCoinAnimator(long duration,long delay){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setStartDelay(delay);
//        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean firstFrame = true;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                HBLog.d("coin animator onAnimationUpdate ------firstFrame:"+firstFrame);
                if(firstFrame){
                    firstFrame = false;
                    fitAnimationWithCoin();
                }
                for(Cell cell:mAnimCells){
                    cell.view.setTranslationX(cell.destOffsetX*value);
                    cell.view.setTranslationY(cell.destOffsetY*value);
                    cell.view.setAlpha(1.0f);
                    cell.view.setScaleX(1.0f);
                    cell.view.setScaleY(1.0f);
                }
            }
        });
        valueAnimator.addListener(new WrapAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                HBLog.d("coin animator onAnimationStart ------");
            }
        });
        mCoinAnimator = valueAnimator;
        return mCoinAnimator;
    }

    private void fitAnimationWithCoin(){
        mAnimCells.clear();
        for(int i = 0; i<getChildCount()&&i< sCoinTargetList.size(); i++){
            ImageView view = (ImageView) getChildAt(i);
            Target coinTarget = sCoinTargetList.get(i);
            view.setImageResource(coinTarget.resid);
            float offsetX = 0,offsetY = 0;
            if(coinTarget.alignLeft){
                offsetX = AppUtil.dp2px(GlobalContext.get(),coinTarget.hOffset) - getWidth()/2;
            } else {
                offsetX = getWidth()/2 - AppUtil.dp2px(GlobalContext.get(),coinTarget.hOffset);
            }

            if(coinTarget.alignTop){
                offsetY = AppUtil.dp2px(GlobalContext.get(),coinTarget.vOffset) - getHeight()/2;
            } else {
                offsetY = getHeight()/2 - AppUtil.dp2px(GlobalContext.get(),coinTarget.vOffset);
            }

            Cell cell = new Cell(coinTarget,view,offsetX,offsetY);
            mAnimCells.add(cell);
        }

        for (Cell cell : mAnimCells) {
            cell.view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mBallExplodeAnimator !=null&& mBallExplodeAnimator.isStarted()){
            mBallExplodeAnimator.cancel();
        }
        if(mBallShakeAnimator !=null&& mBallShakeAnimator.isStarted()){
            mBallShakeAnimator.cancel();
        }
        if(mBallTwoDropAnimator !=null&& mBallTwoDropAnimator.isStarted()){
            mBallTwoDropAnimator.cancel();
        }
        if(mBallDropDisappearAnimator !=null&& mBallDropDisappearAnimator.isStarted()){
            mBallDropDisappearAnimator.cancel();
        }
        if(mCoinAnimator !=null&& mCoinAnimator.isStarted()){
            mCoinAnimator.cancel();
        }
    }

    class Cell {
        float destOffsetX;
        float destOffsetY;
//        float scale;

        Target target;
        float lastOffsetX,lastOffsetY;
        ImageView view;

//        public Cell(float destOffsetX, float destOffsetY, float scale, ImageView view) {
//            this.destOffsetX = destOffsetX;
//            this.destOffsetY = destOffsetY;
//            this.scale = scale;
//            this.view = view;
//        }

        public Cell(Target target,ImageView view,float destOffsetX, float destOffsetY){
            this.target = target;
            this.view = view;
            this.destOffsetX = destOffsetX;
            this.destOffsetY = destOffsetY;
        }
    }

    static class Target {
        boolean alignTop;
        boolean alignLeft;
        int hOffset;
        int vOffset;
        int resid;
        float scale;
        float accelerate;

        public Target(boolean alignLeft, boolean alignTop, int hOffset, int vOffset, int resid) {
            this(alignLeft,alignTop,hOffset,vOffset,resid,1f);
        }

        public Target(boolean alignLeft, boolean alignTop, int hOffset, int vOffset, int resid, float scaleFactor) {
            this(alignLeft,alignTop,hOffset,vOffset,resid,scaleFactor,0f);
        }

        public Target(boolean alignLeft, boolean alignTop, int offsetX, int offsetY, int resid, float scaleFactor, float accelerate) {
            this.alignTop = alignTop;
            this.alignLeft = alignLeft;
            this.hOffset = offsetX;
            this.vOffset = offsetY;
            this.resid = resid;
            this.scale = scaleFactor;
            this.accelerate = accelerate;
        }
    }

}
