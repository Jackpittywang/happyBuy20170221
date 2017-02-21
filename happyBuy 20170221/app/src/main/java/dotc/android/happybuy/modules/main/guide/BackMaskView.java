package dotc.android.happybuy.modules.main.guide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/23.
 */

public class BackMaskView extends View {

    private Paint mPaint;
    private Path mPath;
//    private int mZoneHeight;
    private boolean mBoost;
    private float mDensity;

    public BackMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mDensity = context.getResources().getDisplayMetrics().density;
//        mZoneHeight = AppUtil.dp2px(context,176);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xcc000000);//0x99000000 0xcc000000
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mBoost && getWidth() > 0) {
            mBoost = false;
            initOnBoost();
        }
        canvas.drawPath(mPath,mPaint);
    }

    private void initOnBoost() {
        float centerX = getWidth()*1.0f / 2;
        float centerY = getHeight()*1.0f / 2;



        float zoneLeft = 0;
        float zoneRight = centerX + AppUtil.dp2px(getContext(),80);
        int zoneTop = (int) (centerY - mDensity*154);
        int zoneBottom = (int) (centerY + mDensity*154);

        mPath = new Path();
        mPath.lineTo(zoneLeft,zoneTop);
        mPath.lineTo(zoneRight,zoneTop);
        mPath.lineTo(zoneRight,zoneBottom);
        mPath.lineTo(zoneLeft,zoneBottom);
        mPath.lineTo(zoneLeft,getHeight());
        mPath.lineTo(getWidth(),getHeight());
        mPath.lineTo(getWidth(),0);
        mPath.close();
    }


}
