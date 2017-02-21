package dotc.android.happybuy.modules.home.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 16/5/16.
 */
public class LoopViewPager extends ViewPager {
    private static final int DEFAULT_COUNT = 10000;
    private static final int DEFAULT_START = 5000;
    private static final String TAG = "loop";

    private ArrayList<OnPageChangeListener> mOnPageChangeListenerList;
    private List<OnTouchListener> mOnTouchListeners;
    private boolean mIsBeingTouch;

    public LoopViewPager(Context context) {
        super(context);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mOnTouchListeners = new ArrayList<>();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(new LoopPagerAdapter(adapter));
        if (adapter.getCount() != 0) {
            super.setCurrentItem(DEFAULT_START, false);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    public void invalidatePosition() {
        super.setCurrentItem(DEFAULT_START, false);
    }

    @Override
    public int getCurrentItem() {
        return getRealPosition(super.getCurrentItem(), getAdapter().getCount());
    }

    @Override
    public void setCurrentItem(int item) {
        item += DEFAULT_START;
        super.setCurrentItem(item);
    }

    public void moveToNext() {
        setCurrentItem(LoopViewPager.super.getCurrentItem() + 1, true);
    }

    @Override
    public PagerAdapter getAdapter() {
        return ((LoopPagerAdapter) super.getAdapter()).getSourcePagerAdapter();
    }

    public PagerAdapter getLoopAdapter() {
        return super.getAdapter();
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListenerList == null) {
            mOnPageChangeListenerList = new ArrayList<>();
            super.addOnPageChangeListener(mOnPageChangeListener);
        }

        mOnPageChangeListenerList.add(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handleEvent = super.onInterceptTouchEvent(ev);
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if(action == MotionEvent.ACTION_DOWN){
            handleTouchDownEvent();
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            hanldeTouchReleaseEvent();
        }
        return handleEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handleEvent =  super.onTouchEvent(ev);
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if(action == MotionEvent.ACTION_DOWN){
            handleTouchDownEvent();
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            hanldeTouchReleaseEvent();
        }
        return handleEvent;
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            position = getRealPosition(position, getAdapter().getCount());
            if (mOnPageChangeListenerList != null) {
                for (OnPageChangeListener listener : mOnPageChangeListenerList) {
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            position = getRealPosition(position, getAdapter().getCount());
            if (mOnPageChangeListenerList != null) {
                for (OnPageChangeListener listener : mOnPageChangeListenerList) {
                    listener.onPageSelected(position);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListenerList != null) {
                for (OnPageChangeListener listener : mOnPageChangeListenerList) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }
    };

    public void addOnTouchListener(OnTouchListener listener){
        mOnTouchListeners.add(listener);
    }

    public void removeOnTouchListener(OnTouchListener listener){
        mOnTouchListeners.remove(listener);
    }

    private void handleTouchDownEvent(){
        if(!mIsBeingTouch){
            mIsBeingTouch = true;
            dispatchOnTouchDownListener();
        }
    }

    private void hanldeTouchReleaseEvent(){
        if(mIsBeingTouch){
            mIsBeingTouch = false;
            dispatchOnTouchReleaseListener();
        }
    }

    private void dispatchOnTouchDownListener() {
        for (int i = 0, z = mOnTouchListeners.size(); i < z; i++) {
            OnTouchListener listener = mOnTouchListeners.get(i);
            if (listener != null) {
                listener.onTouchDown();
            }
        }
    }

    private void dispatchOnTouchReleaseListener() {
        for (int i = 0, z = mOnTouchListeners.size(); i < z; i++) {
            OnTouchListener listener = mOnTouchListeners.get(i);
            if (listener != null) {
                listener.onTouchRelease();
            }
        }
    }

    public interface OnTouchListener {
        void onTouchRelease();
        void onTouchDown();
    }

    private static class LoopPagerAdapter extends PagerAdapter {

        private PagerAdapter mSource;

        public LoopPagerAdapter(PagerAdapter adapter) {
            mSource = adapter;
        }

        @Override
        public int getCount() {
            if (mSource.getCount() == 0) {
                return 0;
            }
            return DEFAULT_COUNT;
        }

        public PagerAdapter getSourcePagerAdapter() {
            return mSource;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mSource.isViewFromObject(view, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = getRealPosition(position, mSource.getCount());
            return mSource.instantiateItem(container, position);
        }

        @Override
        public void notifyDataSetChanged() {
            mSource.notifyDataSetChanged();
            super.notifyDataSetChanged();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            position = getRealPosition(position, mSource.getCount());
            mSource.destroyItem(container, position, object);
        }
    }

    private static int getRealPosition(int position, int count) {
        if (count == 0) {
            return 0;
        }
        position = (position - DEFAULT_START) % count;
        if (position < 0) {
            position += count;
        }
        return position;
    }
}
