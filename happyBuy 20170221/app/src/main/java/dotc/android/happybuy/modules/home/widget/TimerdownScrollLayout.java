package dotc.android.happybuy.modules.home.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.home.obj.AwardEventsObj;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/9/21.
 */
public class TimerdownScrollLayout extends ViewGroup implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    private boolean mLooper = false;
    private int mLooperChildCountLimit = 5;
    private int mLooperEdgeDiff = 1;
    private boolean mMatchLooper = false;
    private List<AwardEventsObj> mAwardItems;
    private boolean mFirstLayout = true;
    private int mCurrentPosition = -1;

    private LayoutInflater mInflater;

    private List<OnPageChangeListener> mOnPageChangeListeners;
    private List<OnTouchListener> mOnTouchListeners;
    private ItemClickListener mItemClickListener;

    private float mScaleFactor = 7*1.0f/6 -1;
    private float mAlphaFactor = 0.3f;
    private int mChildWidth = 1;

    //scroller
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private static final int MAX_SETTLE_DURATION = 500;

    private int mTouchSlop;

    public static final int SCROLL_STATE_IDLE = 0x00;
    public static final int SCROLL_STATE_DRAGGING = 0x01;
    public static final int SCROLL_STATE_SMOOTH = 0x02;
    private int mScrollState = SCROLL_STATE_IDLE;

    private float mInitialTouchX, mInitialTouchY;
    private float mLastMotionX,mLastMotionY;
    private boolean mIsBeingTouch;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;

    private int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;

    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mFlingDistance;
    private int mCloseEnough;

    private final Runnable mEndScrollRunnable = new Runnable() {
        public void run() {
            setScrollState(SCROLL_STATE_IDLE);
            populate();
        }
    };

    public TimerdownScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TimerdownScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimerdownScrollLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        mInflater = LayoutInflater.from(context);
        mScroller = new Scroller(context);
        mCurrentPosition = -1;

        final ViewConfiguration configuration = ViewConfiguration.get(context);
//        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        final float density = context.getResources().getDisplayMetrics().density;
        mMinimumVelocity = (int) (400 * density);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mCloseEnough = (int) (2 * density);
        mFlingDistance = (int) (25 * density);

        mChildWidth = (int) (110 * density);

        mAwardItems = new ArrayList<>();
        mOnPageChangeListeners = new ArrayList<>();
        mOnTouchListeners = new ArrayList<>();
    }

    public void setLooper(boolean looper){
        mLooper = looper;
    }

    public boolean isMatchLooper(){
        return mMatchLooper;
    }

    public void snapToNext(){
        if(isBeingIdle()){
            printDebug("snapToNext - --------mCurrentPosition:"+mCurrentPosition);
            if(mMatchLooper){
//                int position = mCurrentPosition;
//                if(mCurrentPosition<mLooperChildCountLimit){
//                    position = mAwardItems.size() - mLooperChildCountLimit + position ;
//                } else if(position>=mAwardItems.size()+mLooperChildCountLimit){
//                    position = position - mAwardItems.size() - mLooperChildCountLimit;
//                } else {
//                    position = position- mLooperChildCountLimit;
//                }
//
//                int fixedPosition = mLooperChildCountLimit+ ((position+1)%mAwardItems.size());
                setCurrentItemInternal(mCurrentPosition+1,true,0);
            } else {
                setCurrentItemInternal(mCurrentPosition+1,true,0);
            }
        }
    }

    public void setSelection(int position,boolean smooth){
        HBLog.d(TAG+" setSelection position:"+position+" smooth:"+smooth);
        if(isBeingIdle()){
            setCurrentItemInternal(position,smooth,0);
        }
    }

//    public void appendItem(List<AwardEventsObj> awardItems){
//        int start = getChildCount();
//        for(int i=start;i<awardItems.size()+start;i++){
//            AnimationTabView itemView = buildTabItemView(awardItems.get(i-start),i);
//            addView(itemView);
//        }
//        requestLayout();
//    }

    public AwardEventsObj getEvent(int position){
        if(position<mAwardItems.size()&&position>-1){
            return mAwardItems.get(position);
        }
        return null;
    }

    public void setItems(List<AwardEventsObj> awardItems, int position){
        mAwardItems.clear();
        mAwardItems.addAll(awardItems);
//        mCurrentPosition = position;

        removeAllViews();
        int itemCount = awardItems.size();
        mMatchLooper = mLooper&&itemCount>=mLooperChildCountLimit;
        if(mMatchLooper){
            for(int i=itemCount-mLooperChildCountLimit;i<itemCount;i++){
                AnimationTabView itemView = buildTabItemView(awardItems.get(i),i);
                addView(itemView);
            }
        }

        for(int i=0;i<awardItems.size();i++){
            AnimationTabView itemView = buildTabItemView(awardItems.get(i),i);
            addView(itemView);
        }

        if(mMatchLooper){
            for(int i=0;i<mLooperChildCountLimit;i++){
                AnimationTabView itemView = buildTabItemView(awardItems.get(i),i);
                addView(itemView);
            }
        }
        printDebug("setItems position:"+position);
        requestLayout();
        int fixPosition = mMatchLooper?position+mLooperChildCountLimit:position;
        printDebug("setItems count:"+itemCount+" fixPosition:"+fixPosition);
        setCurrentItemInternal(fixPosition,false,0);
    }

    private int fixedPosition(int position){
        if(mMatchLooper){
            if(position<mLooperChildCountLimit){
                return mAwardItems.size() - mLooperChildCountLimit + position ;
            } else if(position>=mAwardItems.size()+mLooperChildCountLimit){
                return position - mAwardItems.size() - mLooperChildCountLimit;
            } else {
                return position- mLooperChildCountLimit;
            }
        } else {
            return position;
        }
    }

    public boolean isBeingIdle(){
        return !mIsBeingTouch && mScrollState == SCROLL_STATE_IDLE;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if(mItemClickListener!=null){
//                position--;
//            ToastUtils.showLongToast(v.getContext(),"click position " + position);
            mItemClickListener.onItemClick(v,position);
        }
    }

    private AnimationTabView buildTabItemView(AwardEventsObj event, int position){
        AnimationTabView itemView = new AnimationTabView(getContext(),event);
        itemView.setTag(position);
        itemView.setOnClickListener(this);
        return itemView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
//            LayoutParams lp = child.getLayoutParams();
//            printDebug("onMeasure i:"+i+"   "+lp.width);
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
//         printDebug("onMeasure mChildWidth:"+mChildWidth);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                childLeft = childLeft + childWidth;
            }
        }
//        printDebug("onLayout childLeft:"+childLeft+" "+getChildCount()+" "+getClientWidth());
        if (mFirstLayout) {
            scrollToItem(mCurrentPosition, false, 0, false);
            mFirstLayout = false;
        } else {
            int scrollX = getScrollX();
            wrapScrollTo(scrollX,0);
        }

        // TODO: 16/8/19
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the drag.
//            printDebug("onInterceptTouchEvent Intercept done!");
            resetTouch();
            return false;
        }
        if(!mIsBeingTouch){
            dispatchOnTouchDownListener();
            mIsBeingTouch = true;
        }

        // Nothing more to do here if we have decided whether or not we
        // are dragging.
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
//                printDebug("onInterceptTouchEvent Intercept returning true!");
                return true;
            }
            if (mIsUnableToDrag) {
//                printDebug("onInterceptTouchEvent Intercept returning false!");
                return false;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialTouchX = ev.getX();
                mLastMotionY = mInitialTouchY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsUnableToDrag = false;

                mScroller.computeScrollOffset();
                if (mScrollState == SCROLL_STATE_SMOOTH &&
                        Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough) {
                    // Let the user 'catch' the pager as it animates.
                    mScroller.abortAnimation();
                    populate();
                    mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(SCROLL_STATE_DRAGGING);
                } else {
                    completeScroll(false);
                    mIsBeingDragged = false;
                }
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
                final float yDiff = Math.abs(y - mInitialTouchY);
                if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                    mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(SCROLL_STATE_DRAGGING);
                    mLastMotionX = dx > 0 ? mInitialTouchX + mTouchSlop :
                            mInitialTouchX - mTouchSlop;
                    mLastMotionY = y;
                } else if (yDiff > mTouchSlop) {
                    mIsUnableToDrag = true;
                }

                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    if (performDrag(x,y)) {
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
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
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        }
        if (getChildCount() == 0) {
            // Nothing to present or scroll; nothing to touch.
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        boolean needsInvalidate = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                populate();

                // Remember where the motion event started
                mLastMotionX = mInitialTouchX = event.getX();
                mLastMotionY = mInitialTouchY = event.getY();
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                    if (pointerIndex == -1) {
                        // A child has consumed some touch events and put us into an inconsistent state.
                        needsInvalidate = resetTouch();
                        break;
                    }
                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float y = MotionEventCompat.getY(event, pointerIndex);
                    final float yDiff = Math.abs(y - mLastMotionY);
//                    printDebug("Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        printDebug("Starting drag!");
                        mIsBeingDragged = true;
                        requestParentDisallowInterceptTouchEvent(true);
                        mLastMotionX = x - mInitialTouchX > 0 ? mInitialTouchX + mTouchSlop :
                                mInitialTouchX - mTouchSlop;
                        mLastMotionY = y;
                        setScrollState(SCROLL_STATE_DRAGGING);

                    }
                }
                // Not else! Note that mIsBeingDragged can be set above.
                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(
                            event, mActivePointerId);
                    final float x = MotionEventCompat.getX(event, activePointerIndex);
                    final float y = MotionEventCompat.getY(event, activePointerIndex);
                    needsInvalidate |= performDrag(x,y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocitX = (int) VelocityTrackerCompat.getXVelocity(
                            velocityTracker, mActivePointerId);

                    final int currentPage = currentScrollPosition();
                    final int activePointerIndex =
                            MotionEventCompat.findPointerIndex(event, mActivePointerId);
                    final float x = MotionEventCompat.getX(event, activePointerIndex);
                    final int totalDelta = (int) (x - mInitialTouchX);
//                    final float pageOffset = (getScrollX()- mLeftItemScrollDistance)/getClientWidth()*1.0f / getClientWidth();
                    final float pageOffset = getScrollX()%getClientWidth()*1.0f / getClientWidth();
                    int nextPage = determineTargetPage(currentPage,pageOffset, initialVelocitX,totalDelta);
                    setCurrentItemInternal(nextPage, true, initialVelocitX);

                    needsInvalidate = resetTouch();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    scrollToItem(mCurrentPosition, true, 0, false);
                    needsInvalidate = resetTouch();
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(event);
                mLastMotionX = MotionEventCompat.getX(event, index);
                mActivePointerId = MotionEventCompat.getPointerId(event, index);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                mLastMotionX = MotionEventCompat.getX(event,
                        MotionEventCompat.findPointerIndex(event, mActivePointerId));
                break;
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
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

    private boolean resetTouch() {
//        mTouchState = TOUCH_STATE_REST;
        dispatchOnTouchReleaseListener();
        mActivePointerId = INVALID_POINTER;
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        mIsBeingTouch = false;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int x = mScroller.getCurrX();
//            printDebug("computeScroll x:" + x + " oldX:" + oldX);
            if (oldX != x) {
                wrapScrollTo(x, 0);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            completeScroll(true);
        }
    }

    private void completeScroll(boolean postEvents) {
        boolean needPopulate = mScrollState == SCROLL_STATE_SMOOTH;
//        printDebug("completeScroll postEvents:" + postEvents+" needPopulate:"+needPopulate);
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int x = mScroller.getCurrX();
            if (oldX != x) {
                wrapScrollTo(x,0);
                if (oldX != x) {
//                    fixedScrolled(y);
                }
            }
        }

        if (needPopulate) {
            if (postEvents) {
                ViewCompat.postOnAnimation(this, mEndScrollRunnable);
            } else {
                mEndScrollRunnable.run();
            }
        }
    }

    private void wrapScrollTo(int x,int y){
        scrollTo(x, 0);
//        HBLog.d("wrapScrollTo x: "+x);
        //0-0  width-1  2width-2
        int scrollPosition = currentScrollPosition();
//        int fixedPosition = fixedPosition(scrollPosition);
        int offsetPixels = Math.max(0,getScrollX()%getClientWidth());
        float offset = offsetPixels*1.0f/getClientWidth();//0-0.99

        if(scrollPosition<0||scrollPosition>=getChildCount()){
            return;
        }

        if(mLooper&&getChildCount()>=mLooperChildCountLimit){
            if(scrollPosition<=mLooperChildCountLimit-mLooperEdgeDiff){
//                setSelection(scrollPosition+mAwardItems.size(),false);
//                setCurrentItemInternal(scrollPosition+mAwardItems.size(),false,0);
//                scrollToItem(scrollPosition+mAwardItems.size(),false,0,false);
                wrapScrollTo(x+getClientWidth()*mAwardItems.size(),0);
                HBLog.d("wrapScrollTo fix to looper position "+mLastMotionX);
                return;
            } else if(scrollPosition>=mAwardItems.size()+mLooperChildCountLimit+mLooperEdgeDiff){
                wrapScrollTo(x-getClientWidth()*mAwardItems.size(),0);
                return;
            }
        }

//        Log.d(TAG,"wrapScrollTo "+scrollPosition+" "+offset);

        for (int i=scrollPosition-1;i>=0;i--){
            AnimationTabView view = (AnimationTabView) getChildAt(i);
            view.setTextAlpha(1.0f);
            view.setImageScaleAndTranslate(1.0f + mScaleFactor);
            view.setTextFocus(true);
        }

        AnimationTabView currentView = (AnimationTabView) getChildAt(scrollPosition);
        currentView.setTextAlpha(Math.abs(1.0f-offset));
        currentView.setImageScaleAndTranslate(1.0f + mScaleFactor);
        currentView.setTextFocus(true);

        if(scrollPosition<getChildCount()-1){
//            float nextOffset = 1.0f - offset;
            AnimationTabView nextView = (AnimationTabView) getChildAt(scrollPosition+1);
            nextView.setTextAlpha(Math.abs(1.0f-offset*2));
            nextView.setImageScaleAndTranslate(1.0f +offset*mScaleFactor);
            if(offset<0.5f){
                nextView.setTextFocus(false);
            } else {
                nextView.setTextFocus(true);
            }
        }

        for(int i=scrollPosition+2;i<getChildCount();i++){
            AnimationTabView view = (AnimationTabView) getChildAt(i);
            view.setImageScaleAndTranslate(1.0f);
            view.setTextFocus(false);
        }
        dispatchOnScrollListener(scrollPosition,offset);
    }

    private int currentScrollPosition(){
        return getScrollX()/getClientWidth();
    }

    private int determineTargetPage(int currentPage, float pageOffset,int velocity, int deltaX) {
//        printDebug("determineTargetPage - currentPage:"+currentPage+" pageOffset:"+pageOffset+" deltaX:"+deltaX);
        int targetPage;
        if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
            targetPage = velocity > 0 ? currentPage : currentPage + 1;
        } else {
            final float truncator = currentPage >= mCurrentPosition ? 0.4f : 0.6f;
            targetPage = (int) (currentPage + pageOffset + truncator);
        }

        // Only let the user target pages we have items for
        targetPage = Math.max(0, Math.min(targetPage, getChildCount()-1));
        return targetPage;
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, int velocitX) {
        if (item < 0) {
            item = 0;
        } else if (item > getChildCount()-1) {
            item = getChildCount() - 1;//
        }
        printDebug("setCurrentItemInternal - item:"+item);
        final boolean dispatchSelected = mCurrentPosition != item;

        if (mFirstLayout) {
            mCurrentPosition = item;
            if (dispatchSelected) {
                dispatchOnPageSelected(item);
            }
            requestLayout();
        } else {
            populate(item);
            scrollToItem(item, smoothScroll, velocitX, dispatchSelected);
        }
    }

    private boolean performDrag(float x,float y) {
        boolean needsInvalidate = true;
        final float deltaX = mLastMotionX - x;
        mLastMotionX = x;
        printDebug("performDrag x:"+ x +" deltaX:"+deltaX+" mLastMotionX:"+mLastMotionX);
        float oldScrollX = getScrollX();
        float scrollX = oldScrollX + deltaX;
//        final int width = getClientWidth();

//        int leftBound = 0;
//        int rightBound = (getChildCount()-1)*width;
//        printDebug("performDrag scrollX:"+scrollX+" rightBound:"+rightBound);
        // Don't lose the rounded component
        mLastMotionX += scrollX - (int) scrollX;
        printDebug("performDrag scrollX:"+scrollX+ " mLastMotionX:"+mLastMotionX);
        wrapScrollTo((int) scrollX, getScrollY());
        return needsInvalidate;
    }

    private void smoothScrollTo(int x, int y, int velocity) {
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            completeScroll(false);
            populate();
            setScrollState(SCROLL_STATE_IDLE);
            return;
        }
        setScrollState(SCROLL_STATE_SMOOTH);
        int duration = 0;
//        velocity = Math.abs(velocity);
//        int diff = getHeight();
//        if (velocity > 0) {
//            int halfWidth = diff / 2;
//            float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / diff);
//            distanceRatio -= 0.5f; // center the values about 0.
//            distanceRatio *= 0.3f * Math.PI / 2.0f;
//            final float distance = (float) (halfWidth + halfWidth * Math.sin(distanceRatio));
//            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
//        } else {
//            final float pageDelta = (float) Math.abs(dx) / (diff * 1.0f);
//            duration = (int) ((pageDelta + 1) * 100);
//        }
        duration = MAX_SETTLE_DURATION;//Math.min(duration, MAX_SETTLE_DURATION);
        printDebug(" smoothScrollTo x:" + x + " duration：" + duration + " " + System.currentTimeMillis());
        mScroller.startScroll(sx, sy, dx, dy, duration);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void scrollToItem(int position, boolean smoothScroll, int velocity,
                              boolean dispatchSelected) {

        int destX = position * getClientWidth();
        printDebug(" scrollToItem destX:" + destX+" position:"+position);
        if (smoothScroll) {
            smoothScrollTo(destX, 0, velocity);
            if (dispatchSelected) {
                dispatchOnPageSelected(position);
            }
        } else {
            if (dispatchSelected) {
                dispatchOnPageSelected(position);
            }
            completeScroll(false);
            wrapScrollTo(destX, 0);

        }
    }

    private int getClientWidth(){
        return mChildWidth;
    }

    private void populate() {
        populate(mCurrentPosition);
    }

    void populate(int newCurrentItem) {
        mCurrentPosition = newCurrentItem;

    }

    public void addOnPageChangeListener(OnPageChangeListener listener){
        mOnPageChangeListeners.add(listener);
    }

    public void removeOnPageChangeListener(OnPageChangeListener listener){
        mOnPageChangeListeners.remove(listener);
    }

    public void addOnTouchListener(OnTouchListener listener){
        mOnTouchListeners.add(listener);
    }

    public void removeOnTouchListener(OnTouchListener listener){
        mOnTouchListeners.remove(listener);
    }

    public void addOnItemClickListener(ItemClickListener listener){
        mItemClickListener = listener;
    }

    public void removeOnItemClickListener(ItemClickListener listener){
        mItemClickListener = null;
    }


    private void dispatchOnPageSelected(int position) {
        int fixedPosition = fixedPosition(position);

        for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
            OnPageChangeListener listener = mOnPageChangeListeners.get(i);
            if (listener != null) {
                listener.onPageSelected(fixedPosition);
            }
        }
    }

    private void dispatchOnScrollListener(int position,float positionOffset) {
        if(isMatchLooper()&&positionOffset ==0.0f){
            if(position == mLooperChildCountLimit-mLooperEdgeDiff -1){
                printDebug(TAG+" dispatchOnPageSelected enter ******************** l position:"+position);
                setCurrentItemInternal(mAwardItems.size() + position,false,0);
            } else if(position == mAwardItems.size()+mLooperChildCountLimit+mLooperEdgeDiff-1){
                printDebug(TAG+" dispatchOnPageSelected enter ******************** 2 position:"+position);
                setCurrentItemInternal(position - mAwardItems.size(),false,0);
            }
        }


        int fixedPosition = fixedPosition(position);
        for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
            OnPageChangeListener listener = mOnPageChangeListeners.get(i);
            if (listener != null) {
                listener.onPageScrolled(position,positionOffset);
            }
        }
        updateProgressWhenScroll(fixedPosition,positionOffset);
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

    private void updateProgressWhenScroll(int position,float positionOffset){
        if(getChildCount()>0){
            AnimationTabView itemView = (AnimationTabView) getChildAt(position);

        }

        if(position<getChildCount()-1){
            AnimationTabView nextItemView = (AnimationTabView) getChildAt(position+1);

        }
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }
        mScrollState = newState;
    }

    private void printDebug(String msg) {
        if (true&&HBLog.isLogEnable()) {
            HBLog.d(TAG + " " + msg);
        }
    }

    class AnimationTabView extends LinearLayout {

        ImageView imageView;
        TextView textView;

        AwardEventsObj event;

        public AnimationTabView(Context context, AwardEventsObj event) {
            super(context);
            this.event = event;
            init(context);
        }

        private void init(Context context){
            mInflater.inflate(R.layout.listitem_home_timerdown, this);
            imageView = (ImageView) findViewById(R.id.imageview);
            textView = (TextView) findViewById(R.id.textview);
            if(event!=null){
                textView.setText(event.awardEvent.name);
                Glide.with(context).load(event.awardEvent.default_image).placeholder(R.drawable.ic_home_timedown_default).dontAnimate().into(imageView);
            } else {
                textView.setText("");
                imageView.setImageResource(R.drawable.ic_home_timedown_ending);
            }
        }

        void setTextAlpha(float alpha){
            textView.setAlpha(alpha);
        }

        void setTextFocus(boolean focus){
            if(focus){
                textView.setTextColor(Color.parseColor("#ffff4e00"));
            } else {
                textView.setTextColor(Color.parseColor("#ff333333"));
            }
        }

        void setImageScaleAndTranslate(float scale){
            imageView.setScaleX(scale);
            imageView.setScaleY(scale);

            int height = imageView.getHeight();
            imageView.setTranslationY(-height*(scale-1.0f)/2);
        }

    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset);
        void onPageSelected(int position);
    }

    public interface OnTouchListener {
        void onTouchRelease();
        void onTouchDown();
    }

    public interface ItemClickListener {
        void onItemClick(View view,int position);
    }

}
