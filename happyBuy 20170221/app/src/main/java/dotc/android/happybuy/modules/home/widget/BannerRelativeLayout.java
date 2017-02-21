package dotc.android.happybuy.modules.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/12/20.
 */

public class BannerRelativeLayout extends RelativeLayout {

    private float aspectRatio = 3*1.0f/1f;

    public BannerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){

    }

    public void setAspectRatio(float aspectRatio){
        this.aspectRatio = aspectRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (widthSize/aspectRatio);
        int fixHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, fixHeightMeasureSpec);
    }
}
