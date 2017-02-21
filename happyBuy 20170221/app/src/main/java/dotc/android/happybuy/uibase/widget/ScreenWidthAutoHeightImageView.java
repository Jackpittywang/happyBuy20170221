package dotc.android.happybuy.uibase.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public class ScreenWidthAutoHeightImageView extends ImageView {
    private int realWidth;
    private int realHeight;
    private int marginLeftRightDp;

    public ScreenWidthAutoHeightImageView(Context context, int realWidth, int realHeight, int marginLeftRightDp) {
        super(context);
        this.marginLeftRightDp = marginLeftRightDp;
        this.realHeight = realHeight;
        this.realWidth = realWidth;

        setScaleType(ScaleType.CENTER_CROP);
    }

    private ScreenWidthAutoHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ScreenWidthAutoHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = DisplayUtils.getScreenWidth(getContext());
        int width = (screenWidth - DisplayUtils.dp2Px(getContext(), marginLeftRightDp * 2));
        double ratio = (double) realWidth / (double) realHeight;
        int height = (int) (width / ratio);
        setMeasuredDimension(width, height);
    }
}
