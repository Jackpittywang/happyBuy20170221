package dotc.android.happybuy.modules.show.widget;

/**
 * Created by wangzhiyuan on 2016/10/20.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageViewPage extends android.support.v4.view.ViewPager {

    public ImageViewPage(Context context) {
        super(context);
    }

    public ImageViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
