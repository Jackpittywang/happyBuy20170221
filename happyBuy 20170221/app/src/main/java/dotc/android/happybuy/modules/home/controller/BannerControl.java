package dotc.android.happybuy.modules.home.controller;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoAd;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.home.adapter.BannerAdapter;
import dotc.android.happybuy.modules.home.widget.LoopViewPager;
import dotc.android.happybuy.uibase.widget.IndicatorView;

/**
 * Created by wangjun on 16/12/2.
 */

public class BannerControl implements ViewPager.OnPageChangeListener,LoopViewPager.OnTouchListener{

    private final int WHAT_USER_INTERACTIVE = 0x01;
    private final int WHAT_SNAP_NEXT = 0x02;
    private final String TAG = this.getClass().getSimpleName();

    private boolean mPause;
    private boolean mDestroy;
    private final int INTERVAL = 3*1000;

    private int mPosition = -1;

    private View mContentView;
    private View mBannerRootView;
    private LoopViewPager mLoopViewPager;
    private IndicatorView mIndicatorView;
    private BannerAdapter mBannerAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            HBLog.d(TAG, "handleMessage " + msg.what);
            if (msg.what == WHAT_SNAP_NEXT){
                mLoopViewPager.moveToNext();
                startSnapToNext();
            }
        }
    };

    public BannerControl(View contentView){
        this.mContentView = contentView;
        mBannerRootView = contentView.findViewById(R.id.layout_banner);
        mLoopViewPager = (LoopViewPager) contentView.findViewById(R.id.auto_view_pager);
        mIndicatorView = (IndicatorView) contentView.findViewById(R.id.dotsList);

        mBannerAdapter = new BannerAdapter();
        mLoopViewPager.setAdapter(mBannerAdapter);
        mLoopViewPager.addOnPageChangeListener(this);
        mLoopViewPager.addOnTouchListener(this);
    }

    private void setBannerHeight(){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBannerRootView.getLayoutParams();
        HBLog.d(TAG,"setBannerHeight "+layoutParams.width);
//        layoutParams.height = ;
        mBannerRootView.setLayoutParams(layoutParams);
    }

    public void updateBannerAdapter(final List<PojoAd> pojoAds){
        mBannerAdapter.setData(pojoAds);
        mLoopViewPager.getLoopAdapter().notifyDataSetChanged();
        mLoopViewPager.invalidatePosition();

        mIndicatorView.setDotCount(pojoAds.size());
        mIndicatorView.setCurrentDot(0);
        startSnapIfNeeded();
    }

    public void pause(){
        stopSnapToNext();
    }

    public void resume(){
        startSnapIfNeeded();
    }

    public void destroy(){
        mLoopViewPager.removeOnPageChangeListener(this);
        mLoopViewPager.removeOnTouchListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        mIndicatorView.setCurrentDot(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onTouchRelease() {
        startSnapIfNeeded();
    }

    @Override
    public void onTouchDown() {
        stopSnapToNext();
    }

    private void startSnapIfNeeded(){
        if(mBannerAdapter.getCount()>1){
            startSnapToNext();
        }
    }

    private void startSnapToNext(){
        HBLog.d(TAG,"startSnapToNext ");
        stopSnapToNext();
        mHandler.sendEmptyMessageDelayed(WHAT_SNAP_NEXT,INTERVAL);
    }

    private void stopSnapToNext(){
        if(mHandler.hasMessages(WHAT_SNAP_NEXT)){
            mHandler.removeMessages(WHAT_SNAP_NEXT);
        }
    }
}
