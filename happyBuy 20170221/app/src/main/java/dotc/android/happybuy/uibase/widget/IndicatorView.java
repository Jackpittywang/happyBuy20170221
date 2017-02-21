package dotc.android.happybuy.uibase.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import dotc.android.happybuy.R;

public class IndicatorView extends View {

	private static final int MARGIN_DP = 5;
	private static final int RADIUS_DP = 3;
//	private static final int DOT_COLOR = 0xff000000;
//	private static final int DOT_ALPHA_NORMAL = 0x4c6c1a1a;//30%
	private static final int DOT_ALPHA_NORMAL = 0xffeee4da;
//	private static final int DOT_ALPHA_SELECTED = 0xe5ffffff;//90%
	private static final int DOT_ALPHA_SELECTED = 0xfffa8a05;

	private Paint mPaint;
	private int mMargin;
	private int mRadius;

	private int mDotCount;
	private int mIndex;

	private int mNormalColor;
	private int mSelectedColor;


	public IndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.IndicatorView);
		mNormalColor = a.getInt(R.styleable.IndicatorView_n_color, DOT_ALPHA_NORMAL);
		mSelectedColor = a.getInt(R.styleable.IndicatorView_s_color, DOT_ALPHA_SELECTED);
		a.recycle();
		init();
	}

	public void setDotCount(int count) {
		if (count == mDotCount) {
			return;
		}

		mDotCount = count;
		requestLayout();
	}

	public void setCurrentDot(int index) {
		mIndex = index;
		invalidate();
	}

	@SuppressLint("NewApi")
	private void init() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
//		mPaint.setColor(DOT_COLOR);
		final float dentisy = getContext().getResources().getDisplayMetrics().density;
		mMargin = (int) (dentisy * MARGIN_DP + 0.5f);
		mRadius = (int) (dentisy * RADIUS_DP + 0.5f);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int dotCount = mDotCount;
		for (int i = 0; i < dotCount; i++) {
			if (i == mIndex) {
				mPaint.setColor(mSelectedColor);
			} else {
				mPaint.setColor(mNormalColor);
			}
			final int cx = mRadius + (mRadius * 2 + mMargin) * i;
			final int cy = mRadius;
			canvas.drawCircle(cx, cy, mRadius, mPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int height = mRadius * 2;
		int width = mRadius * 2 * mDotCount + (mDotCount - 1) * mMargin;
		if (width < 0) {
			width = 0;
		}
		this.setMeasuredDimension(width, height);
	}

}
