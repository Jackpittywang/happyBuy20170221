package dotc.android.happybuy.modules.awarding.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/10/9.
 */
public class BottomShadowView extends View {

    private Path mPath;
    private Paint mPaint;
    private LinearGradient mShader;
    private boolean mBoostLayout = true;
    private int[] mShadowColors;

    private float mArcRadius;

    public BottomShadowView(Context context) {
        super(context);
        init(context);
    }

    public BottomShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
//        mPaint.setStrokeWidth();
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStrokeWidth(mEdgeSize);
        mPaint.setColor(0xfffef532);
        mPaint.setAntiAlias(true);

        mShadowColors = new int[]{0x7f000000,0x00000000};
        mArcRadius = AppUtil.dp2px(context,30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mBoostLayout){
            mBoostLayout = false;
            mPath = initPathWithBoost();
            mShader = new LinearGradient(0, getHeight(), 0, 0, mShadowColors, null, Shader.TileMode.MIRROR);
            mPaint.setShader(mShader);
        }
        canvas.drawPath(mPath,mPaint);
    }

    private Path initPathWithBoost(){
        Path path = new Path();
        float left = 0,top = 0,right = getWidth(),bottom = getHeight();
        float delta = mArcRadius*0.5222f;
        path.moveTo(left,top);
        path.lineTo(left,bottom-mArcRadius);
        path.cubicTo(left,bottom-mArcRadius+delta,mArcRadius-delta+left,bottom,mArcRadius+left,bottom);
        path.lineTo(right-mArcRadius,bottom);
        path.cubicTo(right-mArcRadius+delta,bottom,right,bottom-(mArcRadius-delta),right,bottom-mArcRadius);
        path.lineTo(right,top);
        path.close();
        return path;
    }

}
