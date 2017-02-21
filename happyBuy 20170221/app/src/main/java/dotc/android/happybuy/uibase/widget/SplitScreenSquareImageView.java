package dotc.android.happybuy.uibase.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public class SplitScreenSquareImageView extends ImageView {
    private int splitWidth;

    public SplitScreenSquareImageView(Context context) {
        super(context);
        init(context);
    }

    public SplitScreenSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SplitScreenSquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int screenWidth = DisplayUtils.getScreenWidth(context);
        splitWidth = (screenWidth - DisplayUtils.dp2Px(context, 16)) / 4;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //noinspection SuspiciousNameCombination
        setMeasuredDimension(splitWidth, splitWidth);
    }
}
