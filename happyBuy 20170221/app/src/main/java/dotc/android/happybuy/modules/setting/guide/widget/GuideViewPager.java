package dotc.android.happybuy.modules.setting.guide.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by huangli on 16/4/11.
 * 该view pager专门针对导航页定制，可以支持最后一页向右滑动的情况下，调用LastPageLeftMoveListener的接口实现进入应用程序.
 */
public class GuideViewPager extends ViewPager {
    private LastPageLeftMoveListener lastPageLeftMoveListener;
    private float touchX,downX;
    public interface LastPageLeftMoveListener{
        void lastpageleftmoving();
    }
    public GuideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public void setLastPageLeftMoveListener(LastPageLeftMoveListener lastPageLeftMoveListener){
        this.lastPageLeftMoveListener = lastPageLeftMoveListener;
    }

    private boolean isinvokeLastpage = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        int action = MotionEventCompat.getActionMasked(ev);
        if(action == MotionEvent.ACTION_DOWN){
            downX = ev.getX();
        }else if(action == MotionEvent.ACTION_MOVE){
            PagerAdapter adapter = getAdapter();
            if(adapter != null){
                int currentItem = getCurrentItem();
                if(currentItem == (adapter.getCount()-1)){
                    touchX = ev.getX();
                    if(touchX < downX){
                        if(lastPageLeftMoveListener != null){
                            isinvokeLastpage = true;
                        }
                    }
                }
            }
        }else if(action == MotionEvent.ACTION_UP){
            PagerAdapter adapter = getAdapter();
            int currentItem = getCurrentItem();
            if(currentItem == (adapter.getCount()-1)){
                if(isinvokeLastpage){
                    Log.i("flag", "lastpageleftmoving has been invoked ...");
                    lastPageLeftMoveListener.lastpageleftmoving();
                }
            }
        }else{

        }
        return super.dispatchTouchEvent(ev);
    }
}
