package dotc.android.happybuy.modules.recharge.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangjun on 16/12/16.
 */

public class GridLayout extends ViewGroup {

    private final static int COLUMN = 3;

    private float aspectRatio;
    private int cellWidth;
    private int cellHeight;

    public GridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {}

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int row = getChildCount() / COLUMN + (getChildCount() % COLUMN == 0 ? 0 : 1);
        cellWidth = width / COLUMN;
        cellHeight = (int) (cellWidth / aspectRatio);
        int cellWidthMeasureSpec = MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY);
        int cellHeightMeasureSpec = MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(cellWidthMeasureSpec, cellHeightMeasureSpec);
        }
        setMeasuredDimension(width, cellHeight * row);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int row = getChildCount() / COLUMN + (getChildCount() % COLUMN == 0 ? 0 : 1);
        for (int m = 0; m < row; m++) {
            int left = 0;
            int top = m * cellHeight;
            for (int n = m * COLUMN; n < (m + 1) * COLUMN && n < getChildCount(); n++) {
                View view = getChildAt(n);
                view.layout(left, top, left + cellWidth, top + cellHeight);
                left += cellWidth;
            }
        }
    }
}
