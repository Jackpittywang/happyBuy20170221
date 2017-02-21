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
public class TopShadowView extends View {

    private Path mPath;
    private Paint mPaint;
    private LinearGradient mShader;
    private boolean mBoostLayout = true;
    private int[] mShadowColors;

    private float mArcRadius;

    public TopShadowView(Context context) {
        super(context);
        init(context);
    }

    public TopShadowView(Context context, AttributeSet attrs) {
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
            mShader = new LinearGradient(0, 0, 0, getHeight(), mShadowColors, null, Shader.TileMode.MIRROR);
            mPaint.setShader(mShader);
        }
        canvas.drawPath(mPath,mPaint);
    }

    private Path initPathWithBoost(){
        Path path = new Path();
        float left = 0,top = 0,right = getWidth(),bottom = getHeight();
        float delta = mArcRadius*0.5222f;
        path.moveTo(left+mArcRadius,top);
        path.cubicTo(left+mArcRadius-delta,top,left,mArcRadius-delta+top,left,mArcRadius+top);
        path.lineTo(left,bottom);
        path.lineTo(right,bottom);
        path.lineTo(right,top+mArcRadius);
        path.cubicTo(right,top+mArcRadius-delta,right-(mArcRadius-delta),top,right -mArcRadius,top);
        path.close();
        return path;
    }

}
