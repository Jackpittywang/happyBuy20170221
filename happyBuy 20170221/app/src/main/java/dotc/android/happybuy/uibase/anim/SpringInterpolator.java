package dotc.android.happybuy.uibase.anim;

import android.view.animation.Interpolator;

/**
 * Created by wangjun on 16/9/26.
 */
public class SpringInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return (float) Math.sin(input * 2 * Math.PI);
    }
}
