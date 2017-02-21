package dotc.android.happybuy.uibase.listview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/3/30.
 */
public class RefreshListView extends FrameLayout {

    private final String TAG = this.getClass().getSimpleName();
    private View refleshView;
    private ListView listView;

    private TextView mRefleshHintTextView;
//    private View loadMoreView;

    private Context mContext;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;

    private static final int TOUCH_STATE_REST = 0x00;
    private static final int TOUCH_STATE_SCROLLING = 0x01;

    private int mTouchState = TOUCH_STATE_REST ;
    private static final int MAX_SETTLE_DURATION = 600; // ms

    private static final int SNAP_VELOCITY = 400;

    private int mTouchSlop;
    private int mPaddingTouchSlop;
    private float mDownMotionX,mDownMotionY;
    private float mLastMotionX,mLastMotionY;
    private boolean mIsBeingDragged;

    private int refleshViewHeight;
    private boolean pullRefreshing = false;
    private boolean animating;

    private boolean firstLayout = true;
    private OnRefreshListener refreshListener;
    private final int WHAT_REFRESHING = 0x00;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == WHAT_REFRESHING){
                dispatchRefreshListener();
            }
        }
    };

    public RefreshListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mPaddingTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mTouchSlop = ViewConfiguration.get(mContext).getTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int mHeight = view.getMeasuredHeight();
            view.layout(0, height, view.getMeasuredWidth(), height + mHeight);
            height += mHeight;
        }
        if(firstLayout){
            refleshView = getChildAt(0);
            listView = (ListView) getChildAt(1);
            refleshViewHeight = refleshView.getHeight();
            scrollTo(0, refleshViewHeight);
            firstLayout = false;
        }
//        printDebug("onLayout child refleshViewHeight:" + refleshViewHeight );
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if ((action == MotionEvent.ACTION_MOVE) && mTouchState == TOUCH_STATE_SCROLLING) {
            return true;
        }

//        final float x = ev.getX();
//        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                mLastMotionX = mDownMotionX = ev.getX();
                mLastMotionY = mDownMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float dy = y - mDownMotionY;
                final float yDiff = Math.abs(dy);

//                printDebug("onInterceptTouchEvent yDiff "+yDiff+" "+mTouchSlop+" "+isScrollTop());

                if(isScrollTop()&&yDiff>=mTouchSlop){
                    if(getScrollY()==refleshViewHeight&&y-mLastMotionY>0){
                        mTouchState = TOUCH_STATE_SCROLLING;
                    } else if(getScrollY()<refleshViewHeight){
                        mTouchState = TOUCH_STATE_SCROLLING;
                    }
                }

                mLastMotionY = y;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetTouch();
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        printDebug("onInterceptTouchEvent action:" + action + " " + mTouchState);
        return mTouchState == TOUCH_STATE_SCROLLING;
    }

    private boolean isScrollTop() {
        if(listView.getVisibility() == View.VISIBLE){
            if(listView.getCount()>0){
                View view = listView.getChildAt(listView.getFirstVisiblePosition());
                return (view != null) && (view.getTop() == 0);
            }
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        printDebug("onTouchEvent action:" + action + " " + mTouchState);
//        final float x = event.getX();
//        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    requestDisallowInterceptTouchEvent(true);
                }
                mLastMotionX = mDownMotionX = event.getX();
                mLastMotionY = mDownMotionY = event.getY();
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                if (pointerIndex == -1) {
                    // A child has consumed some touch events and put us into an inconsistent state.
                    resetTouch();
                    break;
                }
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float xDiff = Math.abs(x - mLastMotionX);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final float yDiff = Math.abs(y - mLastMotionY);
                int deltaY = (int) (mLastMotionY - y);

                if (pullRefreshing){
                    //nothing to do
                } else {
                    if (getScrollY() <= 0) {
                        mRefleshHintTextView.setText(R.string.pull_to_refresh_release_label);
                    } else {
                        mRefleshHintTextView.setText(R.string.pull_to_refresh_pull_label);
                    }
                }
                if(getScrollY()+deltaY<=refleshViewHeight){
                    if(getScrollY()<0){
                        scrollBy(0, deltaY/2);
                    } else {
                        scrollBy(0, deltaY);
                    }

                    ViewCompat.postInvalidateOnAnimation(this);
                }

                mLastMotionX = x;
                mLastMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) velocityTracker.getYVelocity();
                if(pullRefreshing){
                    if(getScrollY()<refleshViewHeight){
                        smoothScrollTo(0,0,velocityY);
                    }
                } else {
                    if(getScrollY()<=0){
                        setPullRefleshState(true);
                        smoothScrollTo(0, 0, velocityY);
                    } else {
                        if(getScrollY()>refleshViewHeight/2&&velocityY > SNAP_VELOCITY){
                            setPullRefleshState(true);
                            smoothScrollTo(0, 0, velocityY);
                        } else {
                            smoothScrollTo(0,refleshViewHeight,velocityY);
                        }
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                resetTouch();
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, index);
                mLastMotionX = x;
                mActivePointerId = MotionEventCompat.getPointerId(event, index);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                mLastMotionX = MotionEventCompat.getX(event,
                        MotionEventCompat.findPointerIndex(event, mActivePointerId));
                break;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void resetTouch() {
        mTouchState = TOUCH_STATE_REST;
        mActivePointerId = INVALID_POINTER;
        mIsBeingDragged = false;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            completeScroll(true);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        printDebug(" onFinishInflate >>>>>" + refleshView);
        if(refleshView==null){
            refleshView = findViewById(R.id.listheader_loading);
            mRefleshHintTextView = (TextView) refleshView.findViewById(R.id.textview_loading_hint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(handler.hasMessages(WHAT_REFRESHING)){
            handler.removeMessages(WHAT_REFRESHING);
        }
    }

    private void smoothScrollTo(int x, int y, int velocity) {
//        printDebug(" smoothScrollTo >>>>>");
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            //todo
            completeScroll(false);
            return;
        }

        int duration = 0;
        velocity = Math.abs(velocity);
        int diff = refleshViewHeight;
        if (velocity > 0) {
            int halfWidth = diff / 2;
            float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / diff);
            distanceRatio -= 0.5f; // center the values about 0.
            distanceRatio *= 0.3f * Math.PI / 2.0f;
            final float distance = (float) (halfWidth + halfWidth * Math.sin(distanceRatio));
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
//            final float pageWidth = width * mAdapter.getPageWidth(mCurItem);
            final float pageDelta = (float) Math.abs(dx) / (diff*1.0f);
            duration = (int) ((pageDelta + 1) * 100);
        }
        printDebug(" smoothScrollTo >>>>> duration："+duration);
        duration = Math.min(duration, MAX_SETTLE_DURATION);
        mScroller.startScroll(sx, sy, dx, dy, duration);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void completeScroll(boolean postEvents) {

    }

    private void setPullRefleshState(boolean refreshing){
        if(pullRefreshing!=refreshing){
            if(refreshing){
                handler.sendEmptyMessageDelayed(WHAT_REFRESHING,100);
            } else {

            }
            this.pullRefreshing = refreshing;
        }
    }

    public void startRefreshing(){
        smoothScrollTo(0, getScrollY()-refleshViewHeight, 40);
        setPullRefleshState(true);
    }

    public void onRefreshComplete() {
        if (pullRefreshing) {
            pullRefreshing = false;
            if(getScrollY()<refleshViewHeight){
                smoothScrollTo(0, refleshViewHeight,0);
            }
            setPullRefleshState(false);
//            setState(State.RESET);
        }
    }

    private void dispatchRefreshListener(){
        if(refreshListener!=null){
            refreshListener.onRefresh(this);
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        refreshListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh(RefreshListView refreshView);
    }

    private void printDebug(String msg) {
        if(true){
            HBLog.d(TAG+" "+msg);
        }
    }

}
