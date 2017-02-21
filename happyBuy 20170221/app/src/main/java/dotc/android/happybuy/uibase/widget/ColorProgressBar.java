package dotc.android.happybuy.uibase.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/2/1.
 */
public class ColorProgressBar extends View {
    private final String TAG = this.getClass().getSimpleName();
    private final int DEFAULT_BG_COLOR = 0xfff6efe8;
    private final int DEFAULT_PROGRESS_COLOR = 0xffff383e;
    private final int START_COLOR = 0xFFFFD800;
    private final int TRANSITION_COLOR = 0xFFFF9A00;
    private final int END_COLOR = 0xFFFE0101;
    private final int STRIPE_WIDTH = 20;
    private Context mContext;
    private float mMaxCount;
    private float mProgress;
    private float mLastDrawProgress;
    private Paint mPaint;
    private int mBgColor = DEFAULT_BG_COLOR;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;
    private int mStartColor = START_COLOR;
    private int mTransitionColor = TRANSITION_COLOR;
    private int mEndColor = END_COLOR;
    private int mStripeWidth = STRIPE_WIDTH;


    //    private static final int[] SECTION_COLORS = {START_COLOR,TRANSITION_COLOR,END_COLOR};
    private int[] SECTION_COLORS = {mStartColor, mTransitionColor, mEndColor};

    public ColorProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorProgressBar);
        mBgColor = a.getInt(R.styleable.ColorProgressBar_bg_color, DEFAULT_BG_COLOR);
        mProgressColor = a.getInt(R.styleable.ColorProgressBar_progress_color, DEFAULT_PROGRESS_COLOR);
        mStartColor = a.getInt(R.styleable.ColorProgressBar_start_color, START_COLOR);
        mTransitionColor = a.getInt(R.styleable.ColorProgressBar_transition_color, TRANSITION_COLOR);
        mEndColor = a.getInt(R.styleable.ColorProgressBar_end_color, END_COLOR);
        mStripeWidth = a.getInt(R.styleable.ColorProgressBar_stripe_width, STRIPE_WIDTH);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
//        mMaxCount = 100;
        SECTION_COLORS[0] = mStartColor;
        SECTION_COLORS[1] = mTransitionColor;
        SECTION_COLORS[2] = mEndColor;
//        mBgColor = 0xfff6efe8;
//        mProgressColor = getResources().getColor(R.color.red);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int round = height / 2;
        mPaint.reset();
        mPaint.setColor(mBgColor);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectBlackBg = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectBlackBg, round, round, mPaint);

        float section = mProgress / mMaxCount;
        RectF rectProgressBg = new RectF(0, 0, width * section, height);
        if (section <= 1.0f / 3.0f) {
            if (section != 0.0f) {
                mPaint.setColor(mStartColor);
            } else {
                mPaint.setColor(Color.TRANSPARENT);
            }
        } else {
            int count = (section <= 1.0f / 3.0f * 2) ? 2 : 3;
            int[] colors = new int[count];
            System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
            float[] positions = new float[count];
            if (count == 2) {
                positions[0] = 0.0f;
                positions[1] = 1.0f - positions[0];
            } else {
                positions[0] = 0.0f;
                positions[1] = (mMaxCount / 3) / mProgress;
                positions[2] = 1.0f - positions[0] * 2;
            }
            positions[positions.length - 1] = 1.0f;
            LinearGradient shader = new LinearGradient(0, 0, width * section, height, colors, null, Shader.TileMode.MIRROR);
            mPaint.setShader(shader);
        }
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(100);
        mPaint.setStyle(Paint.Style.FILL);
        float startPoint = 0;
        while (startPoint + 2 * mStripeWidth <= width * section) {
            Path path = new Path();
            path.moveTo(startPoint + mStripeWidth, 0);
            path.lineTo(startPoint + 2 * mStripeWidth, 0);
            path.lineTo(startPoint + mStripeWidth, height);
            path.lineTo(startPoint, height);
            path.close();
            startPoint = startPoint + 2 * mStripeWidth;
            canvas.drawPath(path, mPaint);
        }

    }

    public void setProgress(int progress, int max) {
        mMaxCount = max;
        float newprogress=progress;
        float proportion= newprogress/mMaxCount;
        if(proportion<2.0f/100.0f){
            mProgress=(2.0f/100.0f)*mMaxCount;
        }else {
        mProgress = progress > mMaxCount ? mMaxCount : progress;
        }
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

}
