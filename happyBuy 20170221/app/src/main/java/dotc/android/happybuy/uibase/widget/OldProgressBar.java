package dotc.android.happybuy.uibase.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import dotc.android.happybuy.R;

/**
 * Created by wangjun on 16/2/1.
 */
public class OldProgressBar extends View {
    private final String TAG = this.getClass().getSimpleName();
    private final int DEFAULT_BG_COLOR = 0xfff6efe8;
    private final int DEFAULT_PROGRESS_COLOR = 0xffff383e;
    private Context mContext;
    private float mMaxCount;
    private float mProgress;
    private float mLastDrawProgress;
    private Paint mPaint;
    private int mBgColor = DEFAULT_BG_COLOR;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;

    public OldProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.OldProgressBar);
        mBgColor = a.getInt(R.styleable.ColorProgressBar_bg_color, DEFAULT_BG_COLOR);
        mProgressColor = a.getInt(R.styleable.ColorProgressBar_progress_color, DEFAULT_PROGRESS_COLOR);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMaxCount = 100;
//        mBgColor = 0xfff6efe8;
//        mProgressColor = getResources().getColor(R.color.red);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int round = height/2;
        mPaint.reset();
        mPaint.setColor(mBgColor);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectBlackBg = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectBlackBg, round, round, mPaint);

        float section = mProgress/mMaxCount;
        RectF rectProgressBg = new RectF(0, 0, width*section, height);

        mPaint.setColor(mProgressColor);
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
    }

    public void setProgress(int progress,int max) {
        mMaxCount = max;
        mProgress = progress > mMaxCount ? mMaxCount : progress;
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

}
