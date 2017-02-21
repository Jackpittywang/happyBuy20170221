package dotc.android.happybuy.uibase.component;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/3/30.
 */
public class RefreshLayout extends FrameLayout {

    private final String TAG = this.getClass().getSimpleName();
    private View refleshView;
    private View headerView;
    private View tabView;
    private TabListViewPager viewPager;
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

    private int mFlingDistance;
    private int mTouchSlop;
    private int mPaddingTouchSlop;
    private float mDownMotionX,mDownMotionY;
    private float mLastMotionX,mLastMotionY;
    private boolean mIsBeingDragged;

    private int refleshViewHeight,headerViewHeight,tabViewHeight;
    private boolean pullRefreshing = false;
    private boolean animating;

    private boolean firstLayout = true;
    private List<OnRefreshListener> refreshListeners;
    private final int WHAT_REFRESHING = 0x00;
    private final int WHAT_DELAY_STOP_REFRESHING = 0x01;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == WHAT_REFRESHING){
                dispatchRefreshListener();
            } else if(msg.what == WHAT_DELAY_STOP_REFRESHING){
                stopRefreshing();
            }
        }
    };

    public RefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mPaddingTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mTouchSlop = ViewConfiguration.get(mContext).getTouchSlop();
        mFlingDistance = AppUtil.dp2px(context,25);
        mScroller = new Scroller(context);
        refreshListeners = new ArrayList<>();
    }

    public int getTopAndTabHeight(){
        return headerView.getHeight()+tabView.getHeight();
    }

    public void smoothWithAnim(int deltaY,int duration){
        int sx = getScrollX();
        int sy = getScrollY();
        int dy = deltaY - sy;
        mScroller.startScroll(sx, sy, sx, dy, duration);
        ViewCompat.postInvalidateOnAnimation(this);
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
            headerView = getChildAt(1);
            tabView = getChildAt(2);
            viewPager = (TabListViewPager) getChildAt(3);
            refleshViewHeight = refleshView.getHeight();
            headerViewHeight = headerView.getHeight();
            tabViewHeight = tabView.getHeight();
            scrollTo(0, refleshViewHeight);
            firstLayout = false;
        } else {
            headerViewHeight = headerView.getHeight();
        }
//        printDebug("onLayout child refleshViewHeight:" + refleshViewHeight + " " + headerViewHeight + " " + tabViewHeight);
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
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float dy = y - mLastMotionY;
                final float yDiff = Math.abs(dy);

//                final float xDiff = Math.abs(mLastMotionX - x);
//                final float yDiff = Math.abs(mLastMotionY - y);
                printDebug("onInterceptTouchEvent yDiff "+yDiff+" "+mTouchSlop+" ");
                if(getScrollY()>=refleshViewHeight+headerViewHeight){
//                    printDebug("onInterceptTouchEvent y "+y+" "+mLastMotionY+" "+viewPager.isScrollTop());
                    if (yDiff>mTouchSlop&&y-mLastMotionY>0 &&viewPager.isScrollTop()) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                    }
                } else if(Math.abs(x-mDownMotionX)>Math.abs(y-mDownMotionY)&&isTouchingAtHeader((int)x,(int)y)) {
                    //ignore
                } else if(xDiff > mPaddingTouchSlop && xDiff * 0.5f > Math.abs(y - mDownMotionY)){//
//                    mTouchState = TOUCH_STATE_SCROLLING;
                } else if(Math.abs(y - mDownMotionY)>=mTouchSlop){
                    mTouchState = TOUCH_STATE_SCROLLING;
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

                if (getScrollY() < refleshViewHeight + headerViewHeight || deltaY < 0) {
                    if (pullRefreshing && getScrollY() <= 0 && deltaY < 0) {

                    } else {
                        if (getScrollY() <= 0) {
                            mRefleshHintTextView.setText(R.string.pull_to_refresh_release_label);
                            scrollBy(0, deltaY/2);
                        } else {
                            mRefleshHintTextView.setText(R.string.pull_to_refresh_pull_label);
                            scrollBy(0, deltaY);
                        }
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                int velocityY = (int) velocityTracker.getYVelocity();
                if(pullRefreshing){
                    if(getScrollY()<refleshViewHeight){
                        smoothScrollTo(0,0,velocityY);
                    } else if(velocityY > SNAP_VELOCITY){
                        smoothScrollTo(0,0,velocityY);
                    } else if (velocityY < -SNAP_VELOCITY){//上划
                        smoothScrollTo(0,refleshViewHeight+headerViewHeight,velocityY);
                    }
                } else {
//                    boolean isYVelocity = Math.abs(velocityY)>Math.abs(velocityX);
                    float detaY = mLastMotionY - mDownMotionY;
                    boolean canFling = Math.abs(detaY)>mFlingDistance;
                    if (velocityY > SNAP_VELOCITY&&canFling &&detaY>0){//下划
                        if(getScrollY()<=0) {
                            setPullRefleshState(true);
                            smoothScrollTo(0, 0, velocityY);
                        } else {
                            smoothScrollTo(0,refleshViewHeight,velocityY);
                        }
                    } else if (velocityY < -SNAP_VELOCITY&&canFling&&detaY<0){//上划
                        smoothScrollTo(0,refleshViewHeight+headerViewHeight,velocityY);
                    } else {
                        if(getScrollY()<=0){
                            setPullRefleshState(true);
                            smoothScrollTo(0,0,velocityY);

                        } else if(getScrollY()<refleshViewHeight){
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
        if(handler.hasMessages(WHAT_DELAY_STOP_REFRESHING)){
            handler.removeMessages(WHAT_DELAY_STOP_REFRESHING);
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
        int diff = headerViewHeight;
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
        printDebug(" smoothScrollTo y:"+y+" duration："+duration);
        duration = Math.min(duration, MAX_SETTLE_DURATION);
        mScroller.startScroll(sx, sy, dx, dy, duration);
        ViewCompat.postInvalidateOnAnimation(this);
//        invalidate();
//        printDebug("smoothScrollTo velocity:" + velocity + " duration:" + duration);
    }

    private void completeScroll(boolean postEvents) {

    }

    private boolean isTouchingAtHeader(int x,int y){
        Rect rect = new Rect();
        headerView.getLocalVisibleRect(rect);
        rect.offset(-getScrollX(), -getScrollY());
        boolean result = rect.contains(x, y);
        return result;
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

    private long mStartRefreshingTimestamp;

    public void startRefreshing(){
        printDebug("startRefreshing "+getScrollY());
        smoothScrollTo(0, getScrollY()-refleshViewHeight, 0);
        setPullRefleshState(true);
        mStartRefreshingTimestamp = System.currentTimeMillis();
    }

    public void onRefreshComplete() {
        final long delta = 500 - (System.currentTimeMillis() - mStartRefreshingTimestamp);
        printDebug("onRefreshComplete delta:"+delta);
        if(delta>0){
            handler.sendEmptyMessageDelayed(WHAT_DELAY_STOP_REFRESHING,delta);
        } else {
            stopRefreshing();
        }

    }

    private void stopRefreshing(){
        if (pullRefreshing) {
            printDebug("stopRefreshing "+getScrollY()+" "+refleshViewHeight);
            pullRefreshing = false;
            if(getScrollY()<refleshViewHeight){
                smoothScrollTo(0, refleshViewHeight,0);
            } else {
                smoothScrollTo(0, getScrollY()+refleshViewHeight,0);
            }
            setPullRefleshState(false);
//            setState(State.RESET);
        }
    }

    private void dispatchRefreshListener(){
        for(OnRefreshListener listener:refreshListeners){
            listener.onRefresh(this);
        }
    }

    public void addOnRefreshListener(OnRefreshListener listener) {
        refreshListeners.add(listener);
    }

    public interface OnRefreshListener {
        void onRefresh(RefreshLayout refreshView);
    }

    private void printDebug(String msg) {
        if(true){
//            HBLog.d(TAG+" "+msg);
            Log.d(TAG,msg);
        }
    }

}
